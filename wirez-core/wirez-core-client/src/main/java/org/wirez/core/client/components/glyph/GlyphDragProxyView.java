package org.wirez.core.client.components.glyph;

import com.ait.lienzo.client.core.shape.IPrimitive;
import org.wirez.core.client.canvas.Layer;
import org.wirez.core.client.canvas.lienzo.LienzoLayer;
import org.wirez.lienzo.primitive.PrimitiveDragProxy;

public class GlyphDragProxyView implements GlyphDragProxy.View {
    
    public void create(final Layer layer, 
                       final int x,
                       final int y,
                       final IPrimitive<?> copy, 
                       final Callback callback ) {

        final LienzoLayer lienzoLayer = (LienzoLayer) layer;
        
        new PrimitiveDragProxy(lienzoLayer.getLienzoLayer(), copy, x, y, new PrimitiveDragProxy.Callback() {
            @Override
            public void onMove(final int x, final int y) {
                callback.onMove( x, y );
            }

            @Override
            public void onComplete(final int x, final int y) {
                callback.onComplete( x, y );
            }
        });
        
    }
    
}
