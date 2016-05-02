package org.wirez.lienzo.primitive;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

public class PrimitiveDragProxy {

    public interface Callback {

        void onMove(int x, int y);

        void onComplete(int x, int y);

    }
    
    private boolean attached = false;
    private Timer timer;
    private Runnable timeoutRunnable;
    
    public PrimitiveDragProxy(final Layer layer,
                              final IPrimitive<?> shape,
                              final int x,
                              final int y,
                              final int timeout,
                              final Callback callback) {
        
        final IPrimitive<?> copy = shape.copy();
        
        this.timer = new Timer() {
            @Override
            public void run() {
                
                if ( null != timeoutRunnable ) {
                    timeoutRunnable.run();;
                }
                
            }
        };
        
        this.timer.schedule( timeout );
        
        create( layer, copy, timeout, callback );
        
    }

    private void create(final Layer layer, final IPrimitive<?> copy, final int timeout, final Callback callback ) {
        final HandlerRegistration[] handlerRegs = new HandlerRegistration[ 2 ];

        handlerRegs[ 0 ] = RootPanel.get().addDomHandler(new MouseMoveHandler() {

            @Override
            public void onMouseMove( final MouseMoveEvent mouseMoveEvent ) {
                
                timer.schedule( timeout );
                
                final int x = mouseMoveEvent.getX();
                final int y = mouseMoveEvent.getY();

                if ( !attached ) {
                    layer.add( copy.setX(x).setY(y) );
                    attached = true;
                }
                
                copy.setX(x).setY(y);
                layer.batch();
                
                timeoutRunnable = () -> callback.onMove( x, y );
                
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

                    if ( null != timer ) {
                        timer.cancel();
                    }
                    
                    callback.onComplete( x, y );
                    copy.removeFromParent();
                    
                }
                
                
            }
            
        }, MouseUpEvent.getType() );
    }
}
