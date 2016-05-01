package org.wirez.lienzo.toolbox;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import org.wirez.lienzo.primitive.PrimitiveDragProxy;
import org.wirez.lienzo.toolbox.builder.Button;
import org.wirez.lienzo.toolbox.event.ToolboxButtonEvent;
import org.wirez.lienzo.toolbox.event.ToolboxButtonEventHandler;

import java.util.List;

public class ToolboxButton {
    private final WiresShape primitive;
    public static final double BUTTON_SIZE = 16;
    private final Layer layer;
    private final HandlerRegistrationManager handlerRegistrationManager = new HandlerRegistrationManager();
    private ToolboxButtonEventHandler clickHandler;
    private ToolboxButtonEventHandler dragEndHandler;
    
    private final MultiPath decorator = new MultiPath().rect(0, 0, BUTTON_SIZE, BUTTON_SIZE)
            .setFillColor(ColorName.LIGHTGREY)
            .setFillAlpha(0.1)
            .setStrokeWidth(0)
            .setDraggable(false);

    public ToolboxButton(final Layer layer, 
                         final Shape<?> shape, 
                         final List<Button.WhenReady> callbacks,
                         final ToolboxButtonEventHandler clickHandler,
                         final ToolboxButtonEventHandler dragEndHandler) {
        this.layer = layer;
        this.clickHandler = clickHandler;
        this.dragEndHandler = dragEndHandler;
        this.primitive = build(shape, BUTTON_SIZE, BUTTON_SIZE);
        
        for (Button.WhenReady callback : callbacks) {
            callback.whenReady(this);
        }
    }

    public WiresShape getShape() {
        return primitive;
    }

    public MultiPath getDecorator() {
        return decorator;
    }
    
    public void remove() {
        handlerRegistrationManager.removeHandler();
        primitive.removeFromParent();
    }

    private WiresShape build(final Shape<?> shape,
                             final double width,
                             final double height) {
        WiresManager manager = WiresManager.get(layer);
        WiresShape wiresShape = manager.createShape(decorator).setDraggable( false );

        Geometry.setScaleToFit(shape, width, height);
        wiresShape.getContainer().add( shape.setDraggable( false ) );
        decorator.moveToTop();
        
        if ( null != clickHandler ) {

            wiresShape.getGroup().addNodeMouseClickHandler(new NodeMouseClickHandler() {
                @Override
                public void onNodeMouseClick(final NodeMouseClickEvent event) {
                    clickHandler.fire( buildEvent( event.getX(), event.getY() ) );
                }
            });
            
        }
        
        if ( null != dragEndHandler ) {

            wiresShape.getGroup().addNodeMouseDownHandler(new NodeMouseDownHandler() {
                @Override
                public void onNodeMouseDown(final NodeMouseDownEvent event) {
                    
                    new PrimitiveDragProxy(layer, shape, event.getX(), event.getY(), new PrimitiveDragProxy.Callback() {
                        @Override
                        public void onMove(final int x, final int y) {
                        }

                        @Override
                        public void onComplete(final int x, final int y) {
                            dragEndHandler.fire( buildEvent( x, y ) );
                        }
                    });
                }
                
            });
            
        }
        
        return wiresShape;
    }
    
    private ToolboxButtonEvent buildEvent( final int x, final int y ) {
        
        return new ToolboxButtonEvent() {

            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
        
    }

}
