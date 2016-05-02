package org.wirez.core.client.canvas.util;

import org.wirez.core.client.canvas.AbstractCanvas;
import org.wirez.core.client.canvas.lienzo.LienzoLayer;
import org.wirez.core.client.canvas.lienzo.LienzoLayerUtils;
import org.wirez.core.client.shape.Shape;

import javax.enterprise.context.Dependent;

@Dependent
public class CanvasBoundsIndexerImpl implements CanvasBoundsIndexer {

    private AbstractCanvas canvas;
    
    @Override
    public CanvasBoundsIndexer forCanvas(final AbstractCanvas canvas) {
        this.canvas = canvas;
        return this;
    }

    @Override
    public Shape<?> get(final double x, 
                        final double y) {
    
        final LienzoLayer lienzoLayer = (LienzoLayer) canvas.getLayer();
        
        final String viewUUID = LienzoLayerUtils.getUUID_At( lienzoLayer, x, y );
        
        return canvas.getShape( viewUUID );
    }
    
}
