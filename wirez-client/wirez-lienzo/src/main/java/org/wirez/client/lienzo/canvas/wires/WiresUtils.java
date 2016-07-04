package org.wirez.client.lienzo.canvas.wires;

import com.ait.lienzo.client.core.shape.wires.*;
import org.wirez.core.graph.Edge;
import org.wirez.core.graph.Node;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.shape.view.ShapeView;

public final class WiresUtils {
    
    public static Node getNode(final AbstractCanvasHandler canvasHandler,
                               final WiresContainer shape) {
        
        if ( shape instanceof ShapeView ) {

            final ShapeView view = (ShapeView) shape;

            return canvasHandler.getGraphIndex().getNode(view.getUUID());
            
        } else if ( shape instanceof WiresLayer ) {

            final String canvasRoot = canvasHandler.getDiagram().getSettings().getCanvasRootUUID();

            if ( null != canvasRoot ) {

                return canvasHandler.getGraphIndex().getNode( canvasRoot );

            }

        }
        
        return null;
    }
    
    public static Node getNode(final AbstractCanvasHandler canvasHandler,
                               final WiresMagnet magnet) {

        final WiresShape shape = magnet.getMagnets().getWiresShape();

        return getNode( canvasHandler, shape );
    }

    public static Edge getEdge(final AbstractCanvasHandler canvasHandler,
                               final WiresConnector connector) {

        if ( connector instanceof ShapeView ) {

            final ShapeView view = (ShapeView) connector;

            return canvasHandler.getGraphIndex().getEdge(view.getUUID());
            
        }
        
        return null;
    }
    
}
