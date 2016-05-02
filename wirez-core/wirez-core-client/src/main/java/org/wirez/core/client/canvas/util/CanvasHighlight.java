package org.wirez.core.client.canvas.util;

import com.ait.lienzo.shared.core.types.ColorName;
import org.wirez.core.api.graph.Node;
import org.wirez.core.client.animation.ShapeDeSelectionAnimation;
import org.wirez.core.client.animation.ShapeSelectionAnimation;
import org.wirez.core.client.canvas.AbstractCanvas;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.ShapeState;
import org.wirez.core.client.shape.HasDecorators;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.view.HasCanvasState;

public class CanvasHighlight {

    private final AbstractCanvasHandler canvasHandler;
    private Shape shape;
    private long duration = 200;
    
    public CanvasHighlight(final AbstractCanvasHandler canvasHandler) {
        this.canvasHandler = canvasHandler;
    }

    public CanvasHighlight setDuration(final long duration) {
        this.duration = duration;
        return this;
    }

    public void highLight(final Node node) {
        if ( null != this.shape && !node.getUUID().equals(shape.getUUID()) ) {
            unhighLight();
        }
        if ( null == this.shape ) {
            final String uuid = node.getUUID();
            final Shape shape = getShape(uuid);
            if ( null != shape ) {
                this.shape = shape;

                if (shape.getShapeView() instanceof HasCanvasState) {
                    
                    final HasCanvasState canvasStateMutation = (HasCanvasState) shape.getShapeView();
                    canvasStateMutation.applyState(ShapeState.HIGHLIGHT);
                    
                } else if (shape.getShapeView() instanceof HasDecorators) {
                    
                    new ShapeSelectionAnimation( shape )
                            .setCanvas( getCanvas() )
                            .setDuration( duration )
                            .run();

                }

            }
        }
    }

    public void unhighLight() {
        if ( null != this.shape ) {

            if (shape instanceof HasCanvasState) {
                
                final HasCanvasState canvasStateMutation = (HasCanvasState) shape.getShapeView();
                canvasStateMutation.applyState(ShapeState.UNHIGHLIGHT);
                
            } else if (shape.getShapeView() instanceof HasDecorators) {
                
                new ShapeDeSelectionAnimation(shape, 0, 0, ColorName.BLACK)
                        .setCanvas( getCanvas() )
                        .setDuration( duration )
                        .run();
                
            }

            this.shape = null;

        }
    }


    private Shape getShape(final String uuid) {
        return getCanvas().getShape(uuid);
    }
    
    private AbstractCanvas getCanvas() {
        return canvasHandler.getCanvas();
    }
}
