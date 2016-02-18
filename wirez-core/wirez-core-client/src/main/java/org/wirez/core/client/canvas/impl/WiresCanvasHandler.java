/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wirez.core.client.canvas.impl;

import com.ait.lienzo.client.core.shape.wires.*;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import org.wirez.core.api.command.CommandManager;
import org.wirez.core.api.definition.DefinitionSet;
import org.wirez.core.api.factory.ModelFactory;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.ViewContent;
import org.wirez.core.api.rule.Rule;
import org.wirez.core.api.rule.RuleManager;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.Shape;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.CanvasHandler;
import org.wirez.core.client.canvas.command.CanvasCommandViolation;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.canvas.control.ConnectionAcceptor;
import org.wirez.core.client.canvas.control.ContainmentAcceptor;
import org.wirez.core.client.canvas.listener.CanvasModelListener;
import org.wirez.core.client.canvas.settings.WiresCanvasSettings;
import org.wirez.core.client.control.ShapeControl;
import org.wirez.core.client.factory.ShapeFactory;
import org.wirez.core.client.factory.control.HasShapeControlFactories;
import org.wirez.core.client.factory.control.ShapeControlFactory;
import org.wirez.core.client.impl.BaseConnector;
import org.wirez.core.client.service.ClientRuntimeError;
import org.wirez.core.client.service.ServiceCallback;
import org.wirez.core.client.util.WirezLogger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class WiresCanvasHandler extends AbstractWiresCanvasHandler<WiresCanvasSettings, CanvasModelListener> {

    private static Logger LOGGER = Logger.getLogger("org.wirez.core.client.canvas.impl.WiresCanvasHandler");

    ClientDefinitionManager clientDefinitionManager;
    
    @Inject
    public WiresCanvasHandler(final ShapeManager shapeManager,
                              final CanvasCommandFactory commandFactory,
                              final ClientDefinitionManager clientDefinitionManager) {
        super(shapeManager, commandFactory);
        this.clientDefinitionManager = clientDefinitionManager;
    }


    @Override
    protected void doInitialize() {

        // Load the rules that apply for the diagram.
        final String defSetId = getDiagram().getSettings().getDefinitionSetId();
        final ModelFactory modelFactory = clientDefinitionManager.getModelFactory(defSetId);
        final DefinitionSet definitionSet = (DefinitionSet) modelFactory.build(defSetId); 
        
        loadRules(definitionSet, new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                // Initialization complete.
                doAfterInitialize();
            }
        }, new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                // Do nothing
            }
        });
        
        
    }

    private void loadRules(final DefinitionSet definitionSet, final org.uberfire.mvp.Command sucessCallback, final org.uberfire.mvp.Command errorCallback) {

        clientDefinitionManager.getRules(definitionSet, new ServiceCallback<Collection<Rule>>() {
            @Override
            public void onSuccess(final Collection<Rule> rules) {

                if (rules != null) {
                    for (final Rule rule : rules) {
                        settings.getRuleManager().addRule(rule);
                    }
                }

                final WiresManager wiresManager = canvas.getWiresManager();
                wiresManager.setConnectionAcceptor(CONNECTION_ACCEPTOR);
                wiresManager.setContainmentAcceptor(CONTAINMENT_ACCEPTOR);

                sucessCallback.execute();
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                log(Level.SEVERE, WirezLogger.getErrorMessage(error));
                errorCallback.execute();
            }
        });

    }

    @Override
    protected void doRegister(Shape shape, Element element, ShapeFactory factory) {
        super.doRegister(shape, element, factory);
        doAddShapeControls(shape, element, factory);
    }

    protected void doAddShapeControls(final Shape shape,
                                      final Element element,
                                      final ShapeFactory factory) {
        
        // Shape controls.
        if (factory instanceof HasShapeControlFactories) {

            final Collection<ShapeControlFactory<?, ?>> factories = ((HasShapeControlFactories) factory).getFactories();
            for (ShapeControlFactory controlFactory : factories) {
                ShapeControl control = controlFactory.build(shape);

                // Some controls needs to add elements on the DOM.
                if (control instanceof IsWidget) {
                    final IsWidget controlWidget = (IsWidget) control;
                    canvas.addControl(controlWidget);
                }

                // Enable the stateful control.
                control.enable(this, shape, element);

            }
        }
        
    }
    
    public RuleManager getRuleManager() {
        return settings.getRuleManager();
    }

    public CommandManager<WiresCanvasHandler, CanvasCommandViolation> getCommandManager() {
        return settings.getCommandManager();
    }

    public ConnectionAcceptor getConnectionAcceptor() {
        return settings.getConnectionAcceptor();
    }

    public ContainmentAcceptor getContainmentAcceptor() {
        return settings.getContainmentAcceptor();
    }

    private final IConnectionAcceptor CONNECTION_ACCEPTOR = new IConnectionAcceptor() {

        // Set the source Node for the connector.
        @Override
        public boolean acceptHead(WiresConnection head, WiresMagnet magnet) {
            final BaseConnector connector = (BaseConnector) head.getConnector();
            final Shape sourceShape = (Shape) magnet.getMagnets().getWiresShape();
            final Node sourceNode = getGraphHandler().getNode(sourceShape.getId());
            final Edge edge = getGraphHandler().getEdge(connector.getId());
            final String sourceUUID = sourceNode != null ? sourceNode.getUUID() : null;

            final int mIndex = getMagnetIndex(magnet);

            final String message = "Executed SetConnectionSourceNodeCommand [source=" + sourceUUID + ", magnet=" + mIndex +  "]";
            log(Level.FINE, message);

            return getConnectionAcceptor().acceptSource(sourceNode, edge, mIndex);
        }

        // Set the target Node for the connector.
        @Override
        public boolean acceptTail(WiresConnection tail, WiresMagnet magnet) {
            WiresConnection head = tail.getConnector().getHeadConnection();
            final BaseConnector connector = (BaseConnector) head.getConnector();
            final Shape targetShape = (Shape) magnet.getMagnets().getWiresShape();
            final Node targetNode = getGraphHandler().getNode(targetShape.getId());
            final Edge edge = getGraphHandler().getEdge(connector.getId());
            final String targetUUID = targetNode != null ? targetNode.getUUID() : null;

            final int mIndex = getMagnetIndex(magnet);

            final String message = "Executed SetConnectionTargetNodeCommand [target=" + targetUUID + ", magnet=" + mIndex +  "]";
            log(Level.FINE, message);

            return getConnectionAcceptor().acceptTarget(targetNode, edge, mIndex);
        }

        @Override
        public boolean headConnectionAllowed(WiresConnection head, WiresShape shape) {
            WiresConnection tail = head.getConnector().getTailConnection();

            final BaseConnector connector = (BaseConnector) tail.getConnector();
            final Shape outNode = (Shape) shape;

            final Node sourceNode = getGraphHandler().getNode(outNode.getId());
            final Edge<ViewContent<?>, Node> edge = getGraphHandler().getEdge(connector.getId()); 
            
            return getConnectionAcceptor().allowSource(sourceNode, edge, 0);
        }

        @Override
        public boolean tailConnectionAllowed(WiresConnection tail, WiresShape shape) {
            final BaseConnector connector = (BaseConnector) tail.getConnector();
            final Shape inNode = (Shape) shape;

            final Node targetNode = getGraphHandler().getNode(inNode.getId());
            final Edge<ViewContent<?>, Node> edge = getGraphHandler().getEdge(connector.getId());
            
            return getConnectionAcceptor().allowTarget(targetNode, edge, 0);
        }

        private int getMagnetIndex(final WiresMagnet magnet) {
            if ( null != magnet ) {
                MagnetManager.Magnets magnets = magnet.getMagnets();
                for (int x = 0; x < magnets.size(); x++) {
                    WiresMagnet _m = magnets.getMagnet(x);
                    if ( _m.equals(magnet) ) {
                        return x;
                    }
                }
            }
            return -1;
        }

    };

    private final IContainmentAcceptor CONTAINMENT_ACCEPTOR = new IContainmentAcceptor() {
        @Override
        public boolean containmentAllowed(final WiresContainer wiresContainer, final WiresShape wiresShape) {

            final Shape parent = (Shape) wiresContainer;
            final Shape child = (Shape) wiresShape;

            final Node childNode = getGraphHandler().getNode(child.getId());
            final Node parentNode = getGraphHandler().getNode(parent.getId());

            return getContainmentAcceptor().allow(parentNode, childNode);
        }

        @Override
        public boolean acceptContainment(final WiresContainer wiresContainer, final WiresShape wiresShape) {

            final Shape parent = (Shape) wiresContainer;
            final Shape child = (Shape) wiresShape;

            final Node childNode = getGraphHandler().getNode(child.getId());
            final Node parentNode = getGraphHandler().getNode(parent.getId());
            
           return getContainmentAcceptor().accept(parentNode, childNode);
        }

    };

    private void log(final Level level, final String message) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log(level, message);
        }
    }
    
}