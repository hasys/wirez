package org.wirez.core.api.graph.util;

import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Graph;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.relationship.Child;
import org.wirez.core.api.graph.content.view.Bounds;
import org.wirez.core.api.graph.content.view.View;
import org.wirez.core.api.graph.processing.traverse.content.AbstractContentTraverseCallback;
import org.wirez.core.api.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.wirez.core.api.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;

import java.util.Stack;

public class GraphBoundsIndexerImpl implements GraphBoundsIndexer {
    
    private Graph<View, Node<View, Edge>> graph;

    @Override
    public GraphBoundsIndexerImpl forGraph(Graph<View, Node<View, Edge>> graph) {
        this.graph = graph;
        return this;
    }
    
    @Override
    public Node get(final double x, 
                    final double y) {
        return traverseChildren(x, y);
    }

    public Node traverseChildren(final double x, final double y) {
        final Node[] result = new Node[1];


        new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()).traverse(graph, new AbstractContentTraverseCallback<Child, Node<View, Edge>, Edge<Child, Node>>() {

            final Stack<Node> parents = new Stack<Node>();

            @Override
            public void startGraphTraversal(Graph<View, Node<View, Edge>> graph) {
                super.startGraphTraversal(graph);
                parents.clear();
            }

            @Override
            public void startEdgeTraversal(Edge<Child, Node> edge) {
                Node parent = edge.getSourceNode();
                parents.push(parent);
            }

            @Override
            public void endEdgeTraversal(Edge<Child, Node> edge) {
                super.endEdgeTraversal(edge);
                parents.pop();
            }

            @Override
            public void startNodeTraversal(final Node<View, Edge> node) {
                super.startNodeTraversal(node);
                double parentX = 0;
                double parentY = 0;

                if ( !parents.isEmpty() ) {

                    for ( Node tParent : parents ) {
                        final Object content = tParent.getContent();
                        if (content instanceof View) {
                            final View viewContent = (View) content;
                            final Double[] parentCoords = GraphUtils.getPosition(viewContent);
                            parentX += parentCoords[0];
                            parentY += parentCoords[1];
                        }
                    }

                }

                if (isNodeAt(node, parentX, parentY, x, y)) {
                    result[0] = node;
                }
            }
        });

        return result[0];
    }

    private boolean isNodeAt(final Node node,
                             final double parentX,
                             final double parentY,
                             final double mouseX,
                             final double mouseY) {

        final Object c = node.getContent();
        final Bounds bounds = ( (View) node.getContent()).getBounds();
        final Bounds.Bound ulBound = bounds.getUpperLeft();
        final Bounds.Bound lrBound = bounds.getLowerRight();
        final double ulX = ulBound.getX() + parentX;
        final double ulY = ulBound.getY() + parentY;
        final double lrX = lrBound.getX() + parentX;
        final double lrY = lrBound.getY() + parentY;


        if ( mouseX >= ulX && mouseX <= lrX &&
                mouseY >= ulY && mouseY <= lrY ) {
            return true;
        }

        return false;
    }
    
}
