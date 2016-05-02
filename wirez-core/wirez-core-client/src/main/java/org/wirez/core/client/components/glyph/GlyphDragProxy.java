package org.wirez.core.client.components.glyph;

import com.ait.lienzo.client.core.shape.IPrimitive;
import org.wirez.core.client.canvas.Layer;
import org.wirez.core.client.components.drag.DragProxy;
import org.wirez.core.client.shape.view.ShapeGlyph;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class GlyphDragProxy implements DragProxy<ShapeGlyph> {
    
    public interface View extends DragProxy<IPrimitive<?>> {
    }
    
    View view;

    @Inject
    public GlyphDragProxy(final View view) {
        this.view = view;
    }

    @Override
    public void create(final Layer layer, 
                       final int x,
                       final int y,
                       final ShapeGlyph item,
                       final Callback callback) {
        
        view.create( layer, x, y, item.getGroup(), callback );
        
    }
    
}
