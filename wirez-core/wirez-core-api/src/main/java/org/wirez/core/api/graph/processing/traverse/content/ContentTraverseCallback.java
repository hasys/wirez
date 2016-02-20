package org.wirez.core.api.graph.processing.traverse.content;

import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Graph;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.view.View;

public interface ContentTraverseCallback<C, N extends Node<View, Edge>, E extends Edge<C, Node>> {

    void traverse(E edge);

    void traverseView(Graph<View, N> graph);
    
    void traverseView(N node);
    
    void traverseCompleted();
    
}
