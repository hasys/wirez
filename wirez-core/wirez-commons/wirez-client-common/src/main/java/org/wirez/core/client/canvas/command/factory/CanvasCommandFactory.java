package org.wirez.core.client.canvas.command.factory;

import org.wirez.core.graph.Edge;
import org.wirez.core.graph.Element;
import org.wirez.core.graph.Node;
import org.wirez.core.graph.content.view.View;
import org.wirez.core.client.canvas.command.impl.*;
import org.wirez.core.client.shape.factory.ShapeFactory;

public interface CanvasCommandFactory {
    
    /* ******************************************************************************************
                                    Atomic commands.
       ****************************************************************************************** */
    
    AddCanvasNodeCommand ADD_NODE(Node candidate, ShapeFactory factory);
    
    AddCanvasEdgeCommand ADD_EDGE(Node parent, Edge candidate, ShapeFactory factory);
    
    DeleteCanvasNodeCommand DELETE_NODE(Node candidate);
    
    DeleteCanvasEdgeCommand DELETE_EDGE(Edge candidate);
    
    ClearCanvasCommand CLEAR_CANVAS();
    
    AddCanvasChildEdgeCommand ADD_CHILD_EDGE(Node parent,
                                             Node candidate);
    
    DeleteCanvasChildEdgeCommand DELETE_CHILD_EDGE(Node parent,
                                                   Node candidate);
    
    AddCanvasParentEdgeCommand ADD_PARENT_EDGE(Node parent,
                                               Node candidate);
    
    DeleteCanvasParentEdgeCommand DELETE_PARENT_EDGE(Node parent,
                                                     Node candidate);

    UpdateCanvasElementPositionCommand UPDATE_POSITION(Element element,
                                                       Double x,
                                                       Double y);
    
    UpdateCanvasElementPropertyCommand UPDATE_PROPERTY(Element element,
                                                       String propertyId,
                                                       Object value);

    UpdateCanvasElementPropertiesCommand UPDATE_PROPERTIES(Element element);
    
    AddCanvasChildNodeCommand ADD_CHILD_NODE(Node parent, Node candidate, ShapeFactory factory);
    
    SetCanvasConnectionSourceNodeCommand SET_SOURCE_NODE(Node<? extends View<?>, Edge> node,
                                                         Edge<? extends View<?>, Node> edge,
                                                         int magnetIndex);
    
    SetCanvasConnectionTargetNodeCommand SET_TARGET_NODE(Node<? extends View<?>, Edge> node,
                                                         Edge<? extends View<?>, Node> edge,
                                                         int magnetIndex);
    
}
