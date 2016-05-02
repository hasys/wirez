package org.wirez.lienzo.palette;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.ColorName;
import org.wirez.lienzo.Decorator;
import org.wirez.lienzo.grid.Grid;

import java.util.Iterator;

public abstract class AbstractMiniPalette<T> extends Group {
    
    public interface Callback {
    
        void onItemHover(int index, double x, double y);

        void onItemOut(int index);

        void onItemMouseDown(int index, int x, int y);

        void onItemClick(int index, int x, int y);
        
    }
    
    protected int iconSize;
    protected int padding;
    protected int x;
    protected int y;
    protected Callback callback;

    public AbstractMiniPalette() {
        
    }

    public T setItemCallback(final Callback callback) {
        this.callback = callback;
        return (T) this;
    }

    public T setIconSize(final int iconSize) {
        this.iconSize = iconSize;
        return (T) this;
    }

    public T setPadding(final int padding) {
        this.padding = padding;
        return (T) this;
    }

    public T setX(final int x) {
        this.x = x;
        return (T) this;
    }

    public T setY(final int y) {
        this.y = y;
        return (T) this;
    }

    public T build(final IPrimitive<?>... items) {
        
        clear();

        final Grid grid = new Grid( padding, iconSize, items.length, 1);

        final Iterator<Grid.Point> pointIterator = grid.iterator();

        addPaletteDecorator( grid );

        for ( int c = 0; c < items.length; c++ ) {
            final Grid.Point point = pointIterator.next();
            final IPrimitive<?> item = items[c];
            final int index = c;
            
            final Decorator itemDecorator = new Decorator(new Decorator.ItemCallback() {
                
                @Override
                public void onShow(final double x, 
                                   final double y) {
                    
                    doShowItem( index, x, y );
                    
                }

                @Override
                public void onHide() {

                   doItemOut( index );

                }
            });

            final double px = x + point.getX();
            final double py = y + point.getY();

            Geometry.setScaleToFit(item, iconSize, iconSize);
            final IPrimitive<?> i = itemDecorator.build( item, toDouble(iconSize), toDouble(iconSize) )
                    .setX( px )
                    .setY( py );
            
            this.add( i );

            i.addNodeMouseDownHandler(event -> onItemMouseDown( index, event.getX(), event.getY()) );
            
            i.addNodeMouseClickHandler(event -> onItemClick( index, event.getX(), event.getY()) );
            
        }
                
        return (T) this;
    }

    public T clear() {
        this.removeAll();
        return (T) this;
    }

    protected void doShowItem( final int index,
                     final double x,
                     final double y ) {

        if ( null != callback ) {
            callback.onItemHover( index, x, y );
        }
        
    }

    protected void doItemOut( final int index ) {

        if ( null != callback ) {
            callback.onItemOut( index );
        }
        
    }

    protected void onItemMouseDown(final int index, int x, int y) {

        if ( null != callback ) {
            callback.onItemMouseDown( index, x, y );
        }
        
    }

    protected void onItemClick(final int index, final int x, final int y) {

        if ( null != callback ) {
            callback.onItemClick( index, x, y );
        }
        
    }

    protected Rectangle addPaletteDecorator( final Grid grid ) {
        
        final int[] dSize = getDecoratorSize( grid );
        final double p = padding / 2;
        final Rectangle decorator = new Rectangle( dSize[0] + p , dSize[1] + p )
                .setCornerRadius(5)
                .setFillColor(ColorName.LIGHTGREY)
                .setFillAlpha(0.2)
                .setStrokeWidth(2)
                .setStrokeColor(ColorName.GREY)
                .setStrokeAlpha(0.2)
                .setX( x + p )
                .setY( y + p );
        
        this.add( decorator.moveToBottom() );
        
        return decorator;
    }

    protected int[] getDecoratorSize( final Grid grid ) {
    
        return new int[] { grid.getCols() * ( iconSize + padding ), grid.getRows() * ( iconSize + padding ) };
        
    }
    
    private double toDouble( final int i ) {
        return (double) i;
    }
    
}
