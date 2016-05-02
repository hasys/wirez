package org.wirez.core.api.graph.util;

import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Graph;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.view.View;
import org.wirez.core.api.util.BoundsIndexer;

public interface GraphBoundsIndexer extends BoundsIndexer<Node<View<?>, Edge>> {

    GraphBoundsIndexer forGraph(  Graph<View, Node<View, Edge>> graph );
    
}
