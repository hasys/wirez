/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.wirez.core.client.util;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;
import org.uberfire.mvp.Command;
import org.wirez.core.graph.Edge;
import org.wirez.core.graph.Graph;
import org.wirez.core.graph.Node;
import org.wirez.core.graph.content.view.View;
import org.wirez.core.graph.processing.traverse.content.AbstractFullContentTraverseCallback;
import org.wirez.core.graph.processing.traverse.content.FullContentTraverseProcessorImpl;
import org.wirez.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.wirez.core.client.animation.ShapeAnimation;
import org.wirez.core.client.canvas.CanvasHandler;
import org.wirez.core.client.canvas.ShapeState;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.view.HasCanvasState;
import org.wirez.core.client.shape.view.HasDecorators;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Visits the graph and highlights elements while visiting. Just for development use.
 */
public class CanvasHighlightVisitor {

    private static Logger LOGGER = Logger.getLogger("org.wirez.core.client.util.CanvasHighlightVisitor");
    private static final long DURATION = 500;

    private CanvasHandler canvasHandler;
    private final List<Shape> shapes = new LinkedList<Shape>();
    private ShapeAnimation highlightAnimation;

    public CanvasHighlightVisitor(  ) {
    }

    public void run(final CanvasHandler canvasHandler,
                    final ShapeAnimation highlightAnimation,
                    final Command callback) {

        this.canvasHandler = canvasHandler;
        this.highlightAnimation = highlightAnimation;
      
        prepareSimulation(() -> animate(0, () -> {
            CanvasHighlightVisitor.this.log(Level.FINE, "CanvasHighlightVisitor - FINISHED");
            if ( null != callback ) {
                callback.execute();
                CanvasHighlightVisitor.this.canvasHandler = null;
                CanvasHighlightVisitor.this.highlightAnimation = null;
                CanvasHighlightVisitor.this.shapes.clear();
            }
        }));
    }
    
    private void animate(final int index, final Command callback) {
        if (index < shapes.size()) {
            final Shape shape = shapes.get(index);

            if (shape.getShapeView() instanceof HasCanvasState) {
                
                final HasCanvasState canvasStateMutation = (HasCanvasState) shape.getShapeView();

                Timer t = new Timer() {
                    @Override
                    public void run() {
                        canvasStateMutation.applyState(ShapeState.UNHIGHLIGHT);
                        animate(index + 1, callback);
                    }
                };

                canvasStateMutation.applyState(ShapeState.HIGHLIGHT);
                t.schedule( (int) DURATION );
                
            } else if (shape.getShapeView() instanceof HasDecorators) {

                
                highlightAnimation.forShape( shape )
                        .forCanvas( canvasHandler.getCanvas() )
                        .setCallback( new ShapeAnimation.AnimationCallback() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onFrame() {

                            }

                            @Override
                            public void onComplete() {
                                animate(index + 1, callback);
                            }

                        } )
                        .setDuration( DURATION )
                        .run();
                
            }
            
        } else {
            
            callback.execute();
            
        }
        
    }
    
    private void prepareSimulation(final Command command) {

        final Graph graph = canvasHandler.getDiagram().getGraph();

        new FullContentTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()).traverse(graph, new AbstractFullContentTraverseCallback<Node<View, Edge>, Edge<Object, Node>>() {

            @Override
            public void startViewEdgeTraversal(final Edge<Object, Node> edge) {
                super.startViewEdgeTraversal(edge);
                addShape(edge.getUUID());
            }

            @Override
            public void startChildEdgeTraversal(final Edge<Object, Node> edge) {
                super.startChildEdgeTraversal(edge);
                addShape(edge.getUUID());
            }

            @Override
            public void startParentEdgeTraversal(final Edge<Object, Node> edge) {
                super.startParentEdgeTraversal(edge);
                addShape(edge.getUUID());
            }

            @Override
            public void startEdgeTraversal(final Edge<Object, Node> edge) {
                super.startEdgeTraversal(edge);
                addShape(edge.getUUID());
            }

            @Override
            public void startNodeTraversal(final Node<View, Edge> node) {
                super.startNodeTraversal(node);
                addShape(node.getUUID());
            }
            
            @Override
            public void endGraphTraversal() {
                command.execute();
            }

            private void addShape(String uuid) {
                final Shape shape = canvasHandler.getCanvas().getShape(uuid);
                if ( null != shape ) {
                    shapes.add(shape);
                }
            }
            
        });
        
    }

    private void log(final Level level, final String message) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log(level, message);
        }
    }
    
}
