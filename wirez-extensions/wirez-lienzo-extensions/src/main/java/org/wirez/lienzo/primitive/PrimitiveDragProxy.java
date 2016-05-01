package org.wirez.lienzo.primitive;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;

public class PrimitiveDragProxy {

    public interface Callback {

        void onMove(int x, int y);

        void onComplete(int x, int y);

    }
    
    private boolean attached = false;
    
    public PrimitiveDragProxy(final Layer layer,
                              final IPrimitive<?> shape,
                              final int x,
                              final int y,
                              final Callback callback) {
        
        final IPrimitive<?> copy = shape.copy();
        create( layer, copy, callback );
        
    }
    
    private void create(final Layer layer, final IPrimitive<?> copy, final Callback callback ) {
        final HandlerRegistration[] handlerRegs = new HandlerRegistration[ 2 ];

        handlerRegs[ 0 ] = RootPanel.get().addDomHandler(new MouseMoveHandler() {

            @Override
            public void onMouseMove( final MouseMoveEvent mouseMoveEvent ) {
                final int x = mouseMoveEvent.getX();
                final int y = mouseMoveEvent.getY();

                if ( !attached ) {
                    layer.add( copy.setX(x).setY(y) );
                    attached = true;
                }
                
                copy.setX(x).setY(y);
                layer.batch();
                
                callback.onMove( x, y );
                
            }
            
        }, MouseMoveEvent.getType() );

        handlerRegs[ 1 ] = RootPanel.get().addDomHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp( final MouseUpEvent mouseUpEvent ) {
                handlerRegs[ 0 ].removeHandler();
                handlerRegs[ 1 ].removeHandler();

                if ( attached ) {
                    
                    final int x = mouseUpEvent.getX();
                    final int y = mouseUpEvent.getY();

                    callback.onComplete( x, y );
                    copy.removeFromParent();
                    
                }
                
                
            }
            
        }, MouseUpEvent.getType() );
    }
}
