package org.wirez.core.client.canvas.controls.toolbox.command.node;

import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.view.ShapeGlyph;
import org.wirez.lienzo.palette.HoverMiniPalette;
import org.wirez.lienzo.palette.MiniPalette;

import javax.enterprise.context.Dependent;

@Dependent
public class NewNodeCommandView implements NewNodeCommand.View {

    private NewNodeCommand presenter ;
    private final HoverMiniPalette miniPallete = new HoverMiniPalette();
    private int padding = 10;
    private int iconSize = 16;
    private int timeout = 3000;
    
    @Override
    public NewNodeCommand.View init(final NewNodeCommand presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public NewNodeCommand.View setPadding(final int padding) {
        this.padding = padding;
        return this;
    }

    @Override
    public NewNodeCommand.View setIconSize(final int iconSize) {
        this.iconSize = iconSize;
        return this;
    }

    @Override
    public NewNodeCommand.View setTimeout(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public NewNodeCommand.View showPalette(final Shape shape,
                                           final int x,
                                           final int y,
                                           final ShapeGlyph... glyphs) {
        final WiresShape wiresShape = (WiresShape) shape.getShapeView();
        showMiniPalette( wiresShape, x, y, glyphs );
        return this;
    }

    @Override
    public NewNodeCommand.View clear() {
        removeMiniPalette();
        return this;
    }
    
    private void showMiniPalette(final WiresShape shape, 
                                 final int x, 
                                 final int y,
                                 final ShapeGlyph... glyphs) {
        
        if ( null != shape && null != glyphs && glyphs.length > 0 ) {
            
            final IPrimitive<?>[] items = new IPrimitive[ glyphs.length ];
            for ( int c = 0; c < glyphs.length; c++ ) {
                items[c] = glyphs[c].getGroup();
            }

            shape.getWiresLayer().getLayer().add( miniPallete );

            miniPallete
                    .setPadding( padding )
                    .setIconSize( iconSize )
                    .setTimeout( timeout )
                    .setX( x )
                    .setY( y )
                    .setCloseCallback(this::removeMiniPalette)
                    .setItemCallback( new MiniPalette.Callback() {
                        @Override
                        public void onItemHover(final int index,
                                                final double x,
                                                final double y) {

                            presenter.onItemHover( index, x ,y );

                        }

                        @Override
                        public void onItemOut(final int index) {

                            presenter.onItemOut( index );

                        }

                        @Override
                        public void onDragProxyMove(final int index, 
                                                    final int x, 
                                                    final int y) {
                            
                            presenter.onDragProxyMove( index, x, y );
                            
                        }

                        @Override
                        public void onDragProxyEnd(final int index, 
                                                   final int x, 
                                                   final int y) {
                            
                            presenter.onDragProxyEnd( index, x, y );
                            
                        }
                        
                    } )
                    .build( items )
                    .setAlpha( 0 )
                    .animate( AnimationTweener.LINEAR, 
                                AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)), 
                                500, new AnimationCallback() );
        
        } else {
            
            miniPallete.clear();
            
        }
        
    }
    
    private void removeMiniPalette() {
        
        miniPallete.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)), 
                500, new AnimationCallback() {
                    
            @Override
            public void onClose(IAnimation animation, IAnimationHandle handle) {
                super.onClose(animation, handle);
                miniPallete.clear();
            }
                    
        });
        
    }
    
}
