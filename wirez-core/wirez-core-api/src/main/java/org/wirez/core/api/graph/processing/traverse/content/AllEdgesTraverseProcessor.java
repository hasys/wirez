package org.wirez.core.api.graph.processing.traverse.content;

import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.Relationship;
import org.wirez.core.api.graph.content.view.View;

/**
 * <p>Traverses over the graph by moving to adjacent nodes through edges/relationships with both <i>view</i> and <i>child</i> contents.</p>
 */
public interface AllEdgesTraverseProcessor 
        extends ContentTraverseProcessor<Object, Node<View, Edge>, Edge<Object, Node>, AllEdgesTraverseCallback<Node<View, Edge>, Edge<Object, Node>>> {
   
}
