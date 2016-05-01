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

package org.wirez.core.client.canvas;

import com.google.gwt.logging.client.LogConfiguration;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.api.definition.adapter.DefinitionSetRuleAdapter;
import org.wirez.core.api.diagram.Diagram;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Graph;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.relationship.Child;
import org.wirez.core.api.graph.content.view.View;
import org.wirez.core.api.graph.processing.index.IncrementalIndexBuilder;
import org.wirez.core.api.graph.processing.index.Index;
import org.wirez.core.api.graph.processing.index.IndexBuilder;
import org.wirez.core.api.graph.processing.traverse.tree.AbstractTreeTraverseCallback;
import org.wirez.core.api.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.wirez.core.api.graph.util.GraphUtils;
import org.wirez.core.api.rule.Rule;
import org.wirez.core.api.rule.graph.GraphRulesManager;
import org.wirez.core.api.util.UUID;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.event.*;
import org.wirez.core.client.service.ClientFactoryServices;
import org.wirez.core.client.service.ClientRuntimeError;
import org.wirez.core.client.service.ServiceCallback;
import org.wirez.core.client.shape.Lifecycle;
import org.wirez.core.client.shape.MutableShape;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.shape.impl.AbstractConnector;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractCanvasHandler<D extends Diagram, C extends AbstractCanvas> 
        implements CanvasHandler<D, C> {

    private static Logger LOGGER = Logger.getLogger(AbstractCanvasHandler.class.getName());

    protected ClientDefinitionManager clientDefinitionManager;
    protected ClientFactoryServices clientFactoryServices;
    protected GraphRulesManager rulesManager;
    protected GraphUtils graphUtils;
    protected IndexBuilder<Graph<?, Node>, Node, Edge, Index<Node, Edge>> indexBuilder;
    protected TreeWalkTraverseProcessor treeWalkTraverseProcessor;
    protected ShapeManager shapeManager;
    protected Event<CanvasInitializationCompletedEvent> canvasInitializationCompletedEvent;
    protected Event<CanvasElementAddedEvent> canvasElementAddedEvent;
    protected Event<CanvasElementRemovedEvent> canvasElementRemovedEvent;
    protected Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;
    protected Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent;
    protected Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent;
    
    protected C canvas;
    protected D diagram;
    protected Index<?, ?> graphIndex;
    private final String uuid;

    @Inject
    public AbstractCanvasHandler(final ClientDefinitionManager clientDefinitionManager,
                                 final ClientFactoryServices clientFactoryServices,
                                 final GraphRulesManager rulesManager,
                                 final GraphUtils graphUtils,
                                 final IncrementalIndexBuilder indexBuilder,
                                 final TreeWalkTraverseProcessor treeWalkTraverseProcessor,
                                 final ShapeManager shapeManager,
                                 final Event<CanvasInitializationCompletedEvent> canvasInitializationCompletedEvent,
                                 final Event<CanvasElementAddedEvent> canvasElementAddedEvent,
                                 final Event<CanvasElementRemovedEvent> canvasElementRemovedEvent,
                                 final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent,
                                 final Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent, 
                                 final Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.rulesManager = rulesManager;
        this.graphUtils = graphUtils;
        this.indexBuilder = (IndexBuilder<Graph<?, Node>, Node, Edge, Index<Node, Edge>>) indexBuilder;
        this.treeWalkTraverseProcessor = treeWalkTraverseProcessor;
        this.shapeManager = shapeManager;
        this.canvasInitializationCompletedEvent = canvasInitializationCompletedEvent;
        this.canvasElementAddedEvent = canvasElementAddedEvent;
        this.canvasElementRemovedEvent = canvasElementRemovedEvent;
        this.canvasElementUpdatedEvent = canvasElementUpdatedEvent;
        this.canvasProcessingStartedEvent = canvasProcessingStartedEvent;
        this.canvasProcessingCompletedEvent = canvasProcessingCompletedEvent;
        this.uuid = UUID.uuid();
    }

    @Override
    public CanvasHandler<D, C> initialize(final C canvas) {
        this.canvas = canvas;
        return this;
    }

    @Override
    public AbstractCanvasHandler<D, C> draw(final D diagram) {
        this.diagram = diagram;

        // Initialize the graph handler that provides processing and querying operations over the graph.
        
        this.graphIndex = indexBuilder.build( diagram.getGraph() );
        
        doLoadRules();

        return this;
    }
    
    protected void doLoadRules() {

        // Load the rules that apply for the diagram.
        final String defSetId = getDiagram().getSettings().getDefinitionSetId();

        clientFactoryServices.newDomainObject( defSetId, new ServiceCallback<Object>() {
            @Override
            public void onSuccess(Object definitionSet) {

                DefinitionSetRuleAdapter adapter = clientDefinitionManager.getDefinitionSetRuleAdapter( definitionSet.getClass() );

                final Collection<Rule> rules = adapter.getRules( definitionSet );
                if (rules != null) {
                    for (final Rule rule : rules) {
                        rulesManager.addRule(rule);
                    }
                }

                doDraw();


            }

            @Override
            public void onError(ClientRuntimeError error) {
                showError( error );
            }
        });
        
    }
    
    protected void doDraw() {
        
        // Build the shapes that represents the graph on canvas.
        draw();
        
        // Draw it.
        canvas.draw();
        
        // Fire initialization completed event.
        canvasInitializationCompletedEvent.fire(new CanvasInitializationCompletedEvent( this ) );
        
    }

    @Override
    public C getCanvas() {
        return canvas;
    }

    @Override
    public D getDiagram() {
        return diagram;
    }

    protected void draw() {

        // Start processing.
        fireProcessingStarted();
        
        treeWalkTraverseProcessor
                .usePolicy(TreeWalkTraverseProcessor.TraversePolicy.VISIT_EDGE_AFTER_TARGET_NODE)
                .traverse(diagram.getGraph(), new AbstractTreeTraverseCallback<Graph, Node, Edge>() {
            @Override
            public void startGraphTraversal(final Graph graph) {

            }

            @Override
            public boolean startNodeTraversal(final Node node) {
                
                if ( node.getContent() instanceof View ) {
                    final View viewContent = (View) node.getContent();
                    final ShapeFactory factory = 
                            shapeManager.getFactory( getDefinitionId( viewContent.getDefinition() ) );

                    // Add the node shape into the canvas.
                    register(factory, node);
                    applyElementMutation(node);
                    
                    return true;
                }
                
                return false;
            }

            @Override
            public boolean startEdgeTraversal(final Edge edge) {
                
                final Object content = edge.getContent();

                if ( content instanceof View ) {

                    final View viewContent = (View) edge.getContent();
                    final ShapeFactory factory = 
                            shapeManager.getFactory( getDefinitionId( viewContent.getDefinition() ) );

                    // Add the edge shape into the canvas.
                    register(factory, edge);
                    applyElementMutation(edge);
                    final String uuid = edge.getUUID();
                    AbstractConnector connector = (AbstractConnector) getCanvas().getShape(uuid);
                    connector.applyConnections(edge, AbstractCanvasHandler.this);
                    
                    return true;

                } else if ( content instanceof Child ) {

                    final Node child = edge.getTargetNode();
                    final Node parent = edge.getSourceNode();

                    final Object childContent = child.getContent();
                    if (childContent instanceof View) {
                        addChild(parent, child);
                        applyElementMutation(child);
                    }
                    
                    return true;
                } 

                return false;
                
            }

            @Override
            public void endGraphTraversal() {

                // Processing completed.
                fireProcessingCompleted();
                
            }
        });
        
    }

    /*
        ***************************************************************************************
        * Shape/element handling
        ***************************************************************************************
     */

    @SuppressWarnings("unchecked")
    public void register(final ShapeFactory<Object, AbstractCanvasHandler, Shape> factory, final Element<View<?>> candidate) {
        assert factory != null && candidate != null;
        
        final Shape shape = factory.build( candidate.getContent().getDefinition(), AbstractCanvasHandler.this );

        // Set the same identifier as the graph element's one.
        if ( null == shape.getUUID() ) {
            shape.setUUID(candidate.getUUID());
        }
        
        // Add the shapes on canvas and fire events.
        canvas.addShape(shape);
        canvas.draw();

        // Parents can register controls etc here.
        doRegister(shape, candidate, factory);
        
        // Fire updates.
        afterElementAdded(candidate, shape);
    }
    
    protected void doRegister(final Shape shape, final Element element, final ShapeFactory factory) {
        
    }

    public void deregister(final Element element) {
        final Shape shape = canvas.getShape(element.getUUID());
        beforeElementDeleted(element, shape);
        // TODO: Delete connector connections to the node being deleted?
        doDeregister(shape, element);
        canvas.deleteShape(shape);
        canvas.draw();
        afterElementDeleted(element, shape);

    }

    protected void doDeregister(final Shape shape, final Element element) {
        
    }

    public void applyElementMutation( final Element element ) {
        applyElementMutation( element, true , true );
    }

    public void updateElementPosition(final Element element) {
        applyElementMutation( element, true , false );
    }

    public void updateElementProperties(final Element element) {
        applyElementMutation( element, false , true);
    }

    public void applyElementMutation(final Element candidate, 
                                     final boolean applyPosition, 
                                     final boolean applyProperties) {
        
        final Shape shape = canvas.getShape( candidate.getUUID() );
        
        if ( shape instanceof MutableShape) {

            final MutableShape graphShape = (MutableShape) shape;

            if ( applyPosition ) {
                graphShape.applyPosition( candidate );
            }
            
            if ( applyProperties ) {
                graphShape.applyProperties( candidate );
            }
            
            canvas.draw();
            
            afterElementUpdated(candidate, graphShape);
            
        }
        
    }
    
    public void addChild(final Element parent, final Element child) {
        final Shape parentShape = canvas.getShape(parent.getUUID());
        final Shape childShape = canvas.getShape(child.getUUID());
        canvas.addChildShape(parentShape, childShape);
    }

    public void removeChild(final String parentUUID, final String childUUID) {
        final Shape parentShape = canvas.getShape( parentUUID );
        final Shape childShape = canvas.getShape( childUUID );
        canvas.deleteChildShape(parentShape, childShape);
    }

    public void clear() {
        fireProcessingCompleted();
        canvas.clear();
        canvas.draw();
    }
    
  
    protected void afterElementAdded(final Element element, final Shape shape) {
        
        // Fire a canvas element added event. 
        canvasElementAddedEvent.fire( new CanvasElementAddedEvent( this, element ) );
        
    }

    protected void beforeElementDeleted(final Element element, final Shape shape) {

        // Fire a canvas element deleted event. 
        canvasElementRemovedEvent.fire( new CanvasElementRemovedEvent( this, element ) );

    }

    protected void afterElementDeleted(final Element element, final Shape shape) {
        
    }

    protected void afterElementUpdated(final Element element, final Shape shape) {
        if ( shape instanceof Lifecycle) {
            final Lifecycle lifecycle = (Lifecycle) shape;
            lifecycle.afterDraw();
        }

        // Fire a canvas element added event. 
        canvasElementUpdatedEvent.fire( new CanvasElementUpdatedEvent( this, element ) );
        
    }
    
    protected void fireProcessingStarted() {
        canvasProcessingStartedEvent.fire( new CanvasProcessingStartedEvent(this) );
    }

    protected void fireProcessingCompleted() {
        canvasProcessingCompletedEvent.fire( new CanvasProcessingCompletedEvent(this) );
    }
    
    protected void showError( final ClientRuntimeError error ) {
        fireProcessingCompleted();
        final String message = error.getThrowable() != null ? 
                error.getThrowable().getMessage() : error.getMessage();
        log( Level.SEVERE, message);
    }

    public ClientDefinitionManager getClientDefinitionManager() {
        return clientDefinitionManager;
    }

    public ClientFactoryServices getClientFactoryServices() {
        return clientFactoryServices;
    }

    public GraphRulesManager getRuleManager() {
        return rulesManager;
    }

    public GraphUtils getGraphUtils() {
        return graphUtils;
    }

    public TreeWalkTraverseProcessor getTreeWalkTraverseProcessor() {
        return treeWalkTraverseProcessor;
    }

    public Index<?, ?> getGraphIndex() {
        return graphIndex;
    }

    public IndexBuilder<Graph<?, Node>, Node, Edge, Index<Node, Edge>> getIndexBuilder() {
        return indexBuilder;
    }

    public ShapeManager getShapeManager() {
        return shapeManager;
    }

    protected String getDefinitionId( final Object definition ) {
        final DefinitionAdapter<Object> adapter = clientDefinitionManager.getDefinitionAdapter( definition.getClass() );
        return adapter.getId( definition );
    }
    
    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof AbstractCanvasHandler) ) {
            return false;
        }

        AbstractCanvasHandler that = (AbstractCanvasHandler) o;

        return uuid.equals(that.uuid);

    }
    
    private void log(final Level level, final String message) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log(level, message);
        }
    }
    
}
