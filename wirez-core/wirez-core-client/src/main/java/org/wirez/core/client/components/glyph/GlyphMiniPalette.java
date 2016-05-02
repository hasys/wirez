package org.wirez.core.client.components.glyph;

import com.ait.lienzo.client.core.shape.IPrimitive;
import org.wirez.core.client.canvas.Layer;
import org.wirez.core.client.components.palette.Palette;
import org.wirez.core.client.shape.view.ShapeGlyph;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class GlyphMiniPalette implements Palette<GlyphMiniPalette, ShapeGlyph> {
    
    public interface View<T> extends Palette<T, IPrimitive<?>> {
        
        View<T> setAnimationDuration( double millis );
        
    }

    View<?> view;

    @Inject
    public GlyphMiniPalette(final View view) {
        this.view = view;
    }
    
    @PostConstruct
    public void init() {
        view.setAnimationDuration( 800 );
    }

    @Override
    public GlyphMiniPalette setCloseCallback(final CloseCallback callback) {
        view.setCloseCallback( callback );
        return this;
    }

    @Override
    public GlyphMiniPalette setItemHoverCallback(final ItemHoverCallback callback) {
        view.setItemHoverCallback( callback );
        return this;
    }

    @Override
    public GlyphMiniPalette setItemOutCallback(final ItemOutCallback callback) {
        view.setItemOutCallback( callback );
        return this;
    }

    @Override
    public GlyphMiniPalette setItemMouseDownCallback(final ItemMouseDownCallback callback) {
        view.setItemMouseDownCallback( callback );
        return this;
    }

    @Override
    public GlyphMiniPalette setItemClickCallback(final ItemClickCallback callback) {
        view.setItemClickCallback( callback );
        return this;
    }

    @Override
    public GlyphMiniPalette setX(final int x) {
        view.setX( x );
        return this;
    }

    @Override
    public GlyphMiniPalette setY(final int y) {
        view.setY( y );
        return this;
    }

    @Override
    public GlyphMiniPalette setIconSize(final int iconSize) {
        view.setIconSize( iconSize );
        return this;
    }

    @Override
    public GlyphMiniPalette setPadding(final int padding) {
        view.setPadding( padding );
        return this;
    }

    @Override
    public GlyphMiniPalette show(final Layer layer, final ShapeGlyph[] glyphs) {
        
        if ( null != glyphs && glyphs.length > 0 ) {

            final IPrimitive<?>[] items = new IPrimitive[glyphs.length];
            for (int c = 0; c < glyphs.length; c++) {
                items[c] = glyphs[c].getGroup();
            }

            view.show( layer, items );
            
        } 
        
        return this;
    }

    @Override
    public GlyphMiniPalette clear() {
        view.clear();
        return this;
    }
}
