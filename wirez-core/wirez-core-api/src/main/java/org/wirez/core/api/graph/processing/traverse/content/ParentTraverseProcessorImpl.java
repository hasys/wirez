package org.wirez.core.api.graph.processing.traverse.content;

import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.Parent;
import org.wirez.core.api.graph.content.view.View;
import org.wirez.core.api.graph.processing.traverse.tree.TreeWalkTraverseProcessor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public final class ParentTraverseProcessorImpl extends AbstractContentTraverseProcessor<Parent, Node<View, Edge>, Edge<Parent, Node>>
        implements ParentTraverseProcessor {

    @Inject
    public ParentTraverseProcessorImpl(final TreeWalkTraverseProcessor treeWalkTraverseProcessor) {
        super(treeWalkTraverseProcessor);
    }

    @Override
    protected boolean doTraverse(final Edge edge) {
        return edge.getContent() instanceof Parent;
    }

}
