package org.wirez.core.client.canvas.command.factory;

import org.wirez.core.graph.Edge;
import org.wirez.core.graph.Element;
import org.wirez.core.graph.Node;
import org.wirez.core.graph.content.view.View;
import org.wirez.core.client.canvas.command.impl.*;
import org.wirez.core.client.shape.factory.ShapeFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CanvasCommandFactoryImpl implements CanvasCommandFactory {
    
    @Override
    public AddCanvasNodeCommand ADD_NODE(Node candidate, ShapeFactory factory) {
        return new AddCanvasNodeCommand(candidate, factory);
    }

    @Override
    public AddCanvasEdgeCommand ADD_EDGE(Node parent, Edge candidate, ShapeFactory factory) {
        return new AddCanvasEdgeCommand(parent, candidate, factory);
    }

    @Override
    public DeleteCanvasNodeCommand DELETE_NODE(Node candidate) {
        return new DeleteCanvasNodeCommand(candidate);
    }

    @Override
    public DeleteCanvasEdgeCommand DELETE_EDGE(Edge candidate) {
        return new DeleteCanvasEdgeCommand(candidate);
    }

    @Override
    public ClearCanvasCommand CLEAR_CANVAS() {
        return new ClearCanvasCommand();
    }

    @Override
    public AddCanvasChildEdgeCommand ADD_CHILD_EDGE(final Node parent, final Node candidate) {
        return new AddCanvasChildEdgeCommand(parent, candidate);
    }

    @Override
    public DeleteCanvasChildEdgeCommand DELETE_CHILD_EDGE(final Node parent, final Node candidate) {
        return new DeleteCanvasChildEdgeCommand(parent, candidate);
    }

    @Override
    public AddCanvasParentEdgeCommand ADD_PARENT_EDGE(Node parent,
                                                      Node candidate) {
        return new AddCanvasParentEdgeCommand(parent, candidate);
    }

    @Override
    public DeleteCanvasParentEdgeCommand DELETE_PARENT_EDGE(final Node parent, final Node candidate) {
        return new DeleteCanvasParentEdgeCommand(parent, candidate);
    }

    @Override
    public UpdateCanvasElementPositionCommand UPDATE_POSITION(Element element,
                                                              Double x,
                                                              Double y) {
        return new UpdateCanvasElementPositionCommand(element, x, y);
    }

    @Override
    public UpdateCanvasElementPropertyCommand UPDATE_PROPERTY(Element element,
                                                              String propertyId,
                                                              Object value) {
        return new UpdateCanvasElementPropertyCommand(element, propertyId, value);
    }

    @Override
    public UpdateCanvasElementPropertiesCommand UPDATE_PROPERTIES(final Element element) {
        return new UpdateCanvasElementPropertiesCommand(element);
    }

    @Override
    public AddCanvasChildNodeCommand ADD_CHILD_NODE(Node parent, Node candidate, ShapeFactory factory) {
        return new AddCanvasChildNodeCommand(parent, candidate, factory);
    }

    @Override
    public SetCanvasConnectionSourceNodeCommand SET_SOURCE_NODE(Node<? extends View<?>, Edge> node, Edge<? extends View<?>, Node> edge, int magnetIndex) {
        return new SetCanvasConnectionSourceNodeCommand(node, edge, magnetIndex);
    }

    @Override
    public SetCanvasConnectionTargetNodeCommand SET_TARGET_NODE(Node<? extends View<?>, Edge> node, Edge<? extends View<?>, Node> edge, int magnetIndex) {
        return new SetCanvasConnectionTargetNodeCommand(node, edge, magnetIndex);
    }


}
