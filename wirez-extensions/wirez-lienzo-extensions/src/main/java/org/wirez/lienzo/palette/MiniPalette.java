package org.wirez.lienzo.palette;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.util.Geometry;
import org.wirez.lienzo.grid.Grid;

import java.util.Iterator;

public class MiniPalette extends Group {
    
    private int iconSize;
    private int padding;

    public MiniPalette setIconSize(final int iconSize) {
        this.iconSize = iconSize;
        return this;
    }

    public MiniPalette setPadding(final int padding) {
        this.padding = padding;
        return this;
    }

    public MiniPalette setX(final int x) {
        this.setX( new Integer( x ).doubleValue() );
        return this;
    }

    public MiniPalette setY(final int y) {
        this.setY( new Integer( y ).doubleValue() );
        return this;
    }

    public MiniPalette build(final IPrimitive<?>... items) {
        
        clear();

        final Grid grid = new Grid( padding, iconSize, items.length, 1);

        final Iterator<Grid.Point> pointIterator = grid.iterator();

        for ( int c = 0; c < items.length; c++ ) {
            final Grid.Point point = pointIterator.next();
            final IPrimitive<?> item = items[c];

            Geometry.setScaleToFit(item, 16, 16);
            item.setX(getX() + point.getX());
            item.setY(getY() + point.getY());
            this.add(item);
        }

        return this;
    }

    public MiniPalette clear() {
        this.removeAll();
        return this;
    }
    
}
