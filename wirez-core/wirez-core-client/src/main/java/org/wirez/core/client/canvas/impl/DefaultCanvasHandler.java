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
import com.google.gwt.core.client.GWT;
import org.wirez.core.api.command.Command;
import org.wirez.core.api.command.CommandResult;
import org.wirez.core.api.command.CommandResults;
import org.wirez.core.api.command.DefaultCommandManager;
import org.wirez.core.api.definition.Definition;
import org.wirez.core.api.definition.DefinitionSet;
import org.wirez.core.api.event.NotificationEvent;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.commands.AddChildNodeCommand;
import org.wirez.core.api.graph.commands.SetConnectionSourceNodeCommand;
import org.wirez.core.api.graph.commands.SetConnectionTargetNodeCommand;
import org.wirez.core.api.graph.impl.ChildRelationship;
import org.wirez.core.api.graph.impl.ViewEdge;
import org.wirez.core.api.graph.impl.DefaultGraph;
import org.wirez.core.api.graph.impl.ViewNode;
import org.wirez.core.api.graph.processing.AbstractVisitor;
import org.wirez.core.api.graph.processing.GraphHandler;
import org.wirez.core.api.graph.processing.GraphVisitor;
import org.wirez.core.api.rule.DefaultRuleManager;
import org.wirez.core.api.rule.Rule;
import org.wirez.core.api.util.Logger;
import org.wirez.core.client.WirezClientManager;
import org.wirez.core.client.canvas.CanvasHandler;
import org.wirez.core.client.canvas.CanvasSettings;
import org.wirez.core.client.canvas.command.BaseCanvasCommand;
import org.wirez.core.client.canvas.command.CanvasCommand;
import org.wirez.core.client.canvas.command.impl.DefaultCanvasCommands;
import org.wirez.core.client.factory.ShapeFactory;
import org.wirez.core.client.impl.BaseConnector;
import org.wirez.core.client.impl.BaseShape;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Collection;

// TODO: Implement SelectionManager<Element>
@Dependent
public class DefaultCanvasHandler extends BaseCanvasHandler {

    WirezClientManager wirezClientManager;
    DefaultCanvasCommands defaultCanvasCommands;
    GraphHandler graphHandler;
    CanvasSettings settings;
    
    @Inject
    public DefaultCanvasHandler(final WirezClientManager wirezClientManager,
                                final Event<NotificationEvent> notificationEvent, 
                                final DefaultCommandManager commandManager, 
                                final DefaultRuleManager ruleManager,
                                final DefaultCanvasCommands defaultCanvasCommands,
                                final GraphHandler graphHandler) {
        super(notificationEvent, commandManager, ruleManager);
        this.wirezClientManager = wirezClientManager;
        this.defaultCanvasCommands = defaultCanvasCommands;
        this.graphHandler = graphHandler;
    }

/*
        *********************************************************
        * Initialization & rules
        *********************************************************
     */
    
    @Override
    public CanvasHandler initialize(final CanvasSettings settings) {
        super.initialize(settings);

        // Load the rules to apply for this graph.
        loadRules(settings.getDefinitionSet());

        // Build the shapes that represents the graph on canvas.
        drawGraph();

        // Draw it.
        canvas.draw();
        
        return this;
    }

    private void loadRules(final DefinitionSet definitionSet) {
        Collection<Rule> rules = definitionSet.getRules();
        ruleManager.clearRules();
        if (rules != null) {
            for (final Rule rule : rules) {
                ruleManager.addRule(rule);
            }
        }

        final WiresManager wiresManager = getBaseCanvas().getWiresManager();
        wiresManager.setConnectionAcceptor(CONNECTION_ACCEPTOR);
        wiresManager.setContainmentAcceptor(CONTAINTMENT_ACCEPTOR);
    }
    
    /*
        ***************************************************************************************
        * Graph management
        ***************************************************************************************
     */

    private void drawGraph() {
        new GraphVisitor(graph, new AbstractVisitor() {

            @Override
            public void visitViewNode(final ViewNode node) {
                // Apply add node on this canvas, but do not execute it as node it's already present in the graph.
                final ShapeFactory factory = wirezClientManager.getFactory(node.getDefinition());
                defaultCanvasCommands.ADD_NODE(node, factory)
                        .setCanvas(DefaultCanvasHandler.this)
                        .apply();
            }

            @Override
            public void visitViewEdge(final ViewEdge edge) {
                final ShapeFactory factory = wirezClientManager.getFactory(edge.getDefinition());
                defaultCanvasCommands.ADD_EDGE( edge, factory )
                        .setCanvas(DefaultCanvasHandler.this)
                        .apply();
            }
            
            @Override
            public void visitChildRelationship(final ChildRelationship childRelationship) {
                final ViewNode parent = (ViewNode) childRelationship.getSourceNode();
                final ViewNode child = (ViewNode) childRelationship.getTargetNode();
                final ShapeFactory factory = wirezClientManager.getFactory(child.getDefinition());
                defaultCanvasCommands.ADD_CHILD( parent, child, factory )
                        .setCanvas(DefaultCanvasHandler.this)
                        .apply();
            }

            @Override
            public void visitUnconnectedEdge(Edge edge) {
                if (edge instanceof ViewEdge) {
                    final ViewEdge viewEdge = (ViewEdge) edge;
                    final ShapeFactory factory = wirezClientManager.getFactory(viewEdge.getDefinition());
                    defaultCanvasCommands.ADD_EDGE(viewEdge, factory).setCanvas(DefaultCanvasHandler.this).apply();
                }
                
            }

            // Shapes for edges usually requires both nodes created, so let's build the shape after node's shapes exists (EDGE_LAST visitor policy).
        }, GraphVisitor.VisitorPolicy.EDGE_LAST).run();
    }
    
    /*
        ***************************************************************************************
        * Rules for connections and containtment acceptors.
        ***************************************************************************************
     */

    private final IConnectionAcceptor CONNECTION_ACCEPTOR = new IConnectionAcceptor() {

        // Set the source Node for the connector.
        @Override
        public boolean acceptHead(WiresConnection head, WiresMagnet magnet) {
            final BaseConnector connector = (BaseConnector) head.getConnector();
            final BaseShape sourceShape = (BaseShape) magnet.getMagnets().getWiresShape();
            final ViewNode sourceNode = (ViewNode) graphHandler.getNode(graph, sourceShape.getId());
            final ViewEdge edge = (ViewEdge) graphHandler.getEdge(graph, connector.getId());
            final String sourceUUID = sourceNode != null ? sourceNode.getUUID() : null;

            final String message = "Executed SetConnectionSourceNodeCommand [source=" + sourceUUID + "]";
            Logger.log(message);
            CommandResults results = execute(new BaseCanvasCommand() {
                @Override
                protected Command getCommand() {
                    return new SetConnectionSourceNodeCommand(sourceNode, edge);
                }

                @Override
                public CanvasCommand apply() {
                    // Do nothing, lienzo wires do it for us.
                    return this;
                }

                @Override
                public String toString() {
                    return getCommand().toString();
                }

            });


            final boolean isAccept = isAccept(results);
            return isAccept;
        }

        // Set the target Node for the connector.
        @Override
        public boolean acceptTail(WiresConnection tail, WiresMagnet magnet) {
            WiresConnection head = tail.getConnector().getHeadConnection();
            final BaseConnector connector = (BaseConnector) head.getConnector();
            final BaseShape targetShape = (BaseShape) magnet.getMagnets().getWiresShape();
            final ViewNode targetNode = (ViewNode) graphHandler.getNode(graph, targetShape.getId());
            final ViewEdge edge = (ViewEdge) graphHandler.getEdge(graph, connector.getId());
            final String targetUUID = targetNode != null ? targetNode.getUUID() : null;

            final String message = "Executed SetConnectionTargetNodeCommand [target=" + targetUUID + "]";
            Logger.log(message);
            CommandResults results = execute(new BaseCanvasCommand() {
                @Override
                protected Command getCommand() {
                    return new SetConnectionTargetNodeCommand(targetNode, edge);
                }

                @Override
                public CanvasCommand apply() {
                    // Do nothing, lienzo wires do it for us.
                    return this;
                }

                @Override
                public String toString() {
                    return getCommand().toString();
                }

            });

            final boolean isAccept = isAccept(results);
            return isAccept;
        }

        @Override
        public boolean headConnectionAllowed(WiresConnection head, WiresShape shape) {
            WiresConnection tail = head.getConnector().getTailConnection();
            WiresMagnet m = tail.getMagnet();

            final BaseConnector connector = (BaseConnector) tail.getConnector();
            final BaseShape outNode = (BaseShape) shape;
            final BaseShape inNode = tail.getMagnet() != null ? (BaseShape) tail.getMagnet().getMagnets().getWiresShape() : null;

            final boolean isAllowed = allow(new BaseCanvasCommand() {
                @Override
                protected Command getCommand() {
                    return new SetConnectionSourceNodeCommand((ViewNode) graphHandler.getNode(graph, outNode.getId()), (ViewEdge) graphHandler.getEdge(graph, connector.getId()));
                }

                @Override
                public CanvasCommand apply() {
                    // Do nothing, lienzo wires do it for us.
                    return this;
                }

                @Override
                public String toString() {
                    return getCommand().toString();
                }

            });
            final String outUUID = outNode != null ? outNode.getId() : null;
            final String inUUID = inNode != null ? inNode.getId() : null;
            final String message = "HeadConnectionAllowed  [out=" + outUUID + "] [in=" + inUUID + "] [isAllowed=" + isAllowed + "]";
            Logger.log(message);
            return isAllowed;
        }

        @Override
        public boolean tailConnectionAllowed(WiresConnection tail, WiresShape shape) {
            WiresConnection head = tail.getConnector().getHeadConnection();

            final BaseConnector connector = (BaseConnector) tail.getConnector();
            final BaseShape inNode = (BaseShape) shape;
            final BaseShape outNode = head.getMagnet() != null ? (BaseShape) head.getMagnet().getMagnets().getWiresShape() : null;

            final boolean isAllowed = allow(new BaseCanvasCommand() {
                @Override
                protected Command getCommand() {
                    return new SetConnectionTargetNodeCommand((ViewNode) graphHandler.getNode(graph, inNode.getId()), (ViewEdge) graphHandler.getEdge(graph, connector.getId()));
                }

                @Override
                public CanvasCommand apply() {
                    // Do nothing, lienzo wires do it for us.
                    return this;
                }

                @Override
                public String toString() {
                    return getCommand().toString();
                }

            });
            final String outUUID = outNode != null ? outNode.getId() : null;
            final String inUUID = inNode != null ? inNode.getId() : null;
            final String message = "TailConnectionAllowed  [out=" + outUUID + "] [in=" + inUUID + "] [isAllowed=" + isAllowed + "]";
            Logger.log(message);
            return isAllowed;
        }

    };

    private final IContainmentAcceptor CONTAINTMENT_ACCEPTOR = new IContainmentAcceptor() {
        @Override
        public boolean containmentAllowed(final WiresContainer wiresContainer, final WiresShape wiresShape) {
            // TODO
            return true;
        }

        @Override
        public boolean acceptContainment(final WiresContainer wiresContainer, final WiresShape wiresShape) {

            final BaseShape parent = (BaseShape) wiresContainer;
            final BaseShape child = (BaseShape) wiresShape;
            final ViewNode parentNode = (ViewNode) graphHandler.getNode(graph, parent.getId());
            final ViewNode childNode = (ViewNode) graphHandler.getNode(graph, child.getId());

            final String message = "Executed AddChildNodeCommand [parent=" + parentNode.getUUID() + ", child=" + childNode.getUUID() + "]";
            GWT.log(message);
            
            CommandResults results = execute(new BaseCanvasCommand() {
                @Override
                protected Command getCommand() {
                    return new AddChildNodeCommand(graph, parentNode, childNode);
                }

                @Override
                public CanvasCommand apply() {
                    // Do nothing, lienzo wires do it for us.
                    return this;
                }

                @Override
                public String toString() {
                    return getCommand().toString();
                }

            });

            final boolean isAccept = isAccept(results);
            return isAccept;

        }
    };
    
    private boolean isAccept(final CommandResults results) {
        Logger.logCommandResults(results.results());
        final boolean hasCommandErrors = results.results(CommandResult.Type.ERROR) != null
                && results.results(CommandResult.Type.ERROR).iterator().hasNext();
        return hasCommandErrors;
    }
}

