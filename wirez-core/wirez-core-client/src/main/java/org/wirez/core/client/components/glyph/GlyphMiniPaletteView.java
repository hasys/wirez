package org.wirez.core.client.components.glyph;

import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.IPrimitive;
import org.wirez.core.client.canvas.Layer;
import org.wirez.core.client.canvas.lienzo.LienzoLayer;
import org.wirez.lienzo.palette.AbstractMiniPalette;
import org.wirez.lienzo.palette.HoverMiniPalette;

import javax.enterprise.context.Dependent;

@Dependent
public class GlyphMiniPaletteView implements GlyphMiniPalette.View<GlyphMiniPaletteView> {
    
    private final HoverMiniPalette miniPalette = new HoverMiniPalette();
    
    private CloseCallback closeCallback;
    private ItemHoverCallback hoverCallback;
    private ItemOutCallback outCallback;
    private ItemMouseDownCallback mouseDownCallback;
    private ItemClickCallback clickCallback;
    private double animation_duration = 1000;
    
    private final CloseCallback thisCloseCallback = new CloseCallback() {

        @Override
        public void onClose() {
            
            GlyphMiniPaletteView.this.clear();
            
            if ( null != closeCallback ) {
                closeCallback.onClose();
            }
            
        }
    };
    
    @Override
    public GlyphMiniPaletteView setCloseCallback(final CloseCallback callback) {
        this.closeCallback = callback;
        return this;
    }

    @Override
    public GlyphMiniPaletteView setItemHoverCallback(final ItemHoverCallback callback) {
        this.hoverCallback = callback;
        return this;
    }

    @Override
    public GlyphMiniPaletteView setItemOutCallback(final ItemOutCallback callback) {
        this.outCallback = callback;
        return this;
    }

    @Override
    public GlyphMiniPaletteView setItemMouseDownCallback(final ItemMouseDownCallback callback) {
        this.mouseDownCallback = callback;
        return this;
    }

    @Override
    public GlyphMiniPaletteView setItemClickCallback(final ItemClickCallback callback) {
        this.clickCallback = callback;
        return this;
    }

    @Override
    public GlyphMiniPaletteView setX(final int x) {
        miniPalette.setX(x);
        return this;
    }

    @Override
    public GlyphMiniPaletteView setY(final int y) {
        miniPalette.setY(y);
        return this;
    }

    @Override
    public GlyphMiniPaletteView setIconSize(final int iconSize) {
        miniPalette.setIconSize( iconSize );
        return this;
    }

    @Override
    public GlyphMiniPaletteView setPadding(final int padding) {
        miniPalette.setPadding( padding );
        return this;
    }

    @Override
    public GlyphMiniPaletteView show(final Layer layer, final IPrimitive<?>[] items) {
        
        miniPalette.setCloseCallback(() -> thisCloseCallback.onClose());
        
        miniPalette.setItemCallback(new AbstractMiniPalette.Callback() {
            
            @Override
            public void onItemHover(final int index, 
                                    final double x, 
                                    final double y) {
                hoverCallback.onItemHover( index, x, y );
            }

            @Override
            public void onItemOut(final int index) {
                outCallback.onItemOut( index );
            }

            @Override
            public void onItemMouseDown(final int index, 
                                        final int x, 
                                        final int y) {
                mouseDownCallback.onItemMouseDown( index, x, y );
            }

            @Override
            public void onItemClick(final int index, 
                                    final int x, 
                                    final int y) {
                clickCallback.onItemClick( index, x, y );
            }
            
        });

        ( (LienzoLayer) layer).getLienzoLayer().add( miniPalette );
        
        miniPalette
                .build( items )
                .setAlpha( 0 )
                .animate( AnimationTweener.LINEAR,
                        AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)),
                        animation_duration, new AnimationCallback() );
        
        
        return this;
    }

    @Override
    public GlyphMiniPaletteView clear() {
        
        miniPalette.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(0)),
                animation_duration, new AnimationCallback() {

                    @Override
                    public void onClose(IAnimation animation, IAnimationHandle handle) {
                        super.onClose(animation, handle);
                        miniPalette.clear();
                    }

                });
        
        return this;
    }

    @Override
    public GlyphMiniPalette.View<GlyphMiniPaletteView> setAnimationDuration(final double millis) {
        this.animation_duration = millis;
        return this;
    }
    
    private void fireCloseCallback() {
        
    }
    
}
