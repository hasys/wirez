package org.wirez.lienzo.palette;

import java.util.Iterator;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.wirez.lienzo.Decorator;
import org.wirez.lienzo.Decorator.ItemCallback;
import org.wirez.lienzo.grid.Grid;
import org.wirez.lienzo.grid.Grid.Point;
import org.wirez.lienzo.palette.AbstractMiniPalette.Callback;

import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class MiniPaletteTest {

    @Mock
    private Callback callback;
    @Mock
    private IPrimitive firstPrimitive;
    @Mock
    private IPrimitive secondPrimitive;
    @Mock
    private Decorator decorator;
    @Mock
    private Group group;
    @Mock
    private Rectangle rectangle;

    private static final int ICON_SIZE = 10;
    private static final int PADDING = 5;
    private static final int X = 11;
    private static final int Y = 12;

    private IPrimitive[] arrayOfPrimitives;
    private Iterator<Point> pointIterator;
    private MiniPalette miniPalette;
    private Grid grid;

    @Before
    public void setUp() {
        arrayOfPrimitives = new IPrimitive[] {firstPrimitive, secondPrimitive};

        miniPalette = new MiniPalette()
                .setIconSize(ICON_SIZE)
                .setItemCallback(callback)
                .setPadding(PADDING)
                .setX(X)
                .setY(Y);
        grid = spy(miniPalette.createGrid(arrayOfPrimitives));
        pointIterator = spy(grid.iterator());
        doReturn(pointIterator).when(grid).iterator();
        miniPalette = spy(miniPalette);

        doNothing().when(miniPalette).scaleToFit(anyObject());
        doReturn(miniPalette).when(miniPalette).add(anyObject());
        doReturn(decorator).when(miniPalette).createDecorator(anyInt());
        doReturn(grid).when(miniPalette).createGrid(any());

        doReturn(decorator).when(decorator).build(anyObject(), anyDouble(), anyDouble());
        doReturn(decorator).when(decorator).setX(anyDouble());
        doReturn(decorator).when(decorator).setY(anyDouble());
        doReturn(group).when(decorator).add(anyObject());
    }

    @Test
    public void testBuild() {
        miniPalette.build(firstPrimitive, secondPrimitive);

        verify(miniPalette).clear();
        verify(miniPalette).createGrid(arrayOfPrimitives);
        verify(miniPalette).addPaletteDecorator(grid);
        verify(miniPalette, times(2)).add(decorator);

        verify(pointIterator, times(2)).next();

        verify(decorator).build(firstPrimitive, (double)ICON_SIZE, (double)ICON_SIZE);
        verify(decorator).build(secondPrimitive, (double)ICON_SIZE, (double)ICON_SIZE);
        verify(decorator, times(2)).setX(anyDouble());
        verify(decorator, times(2)).setY(anyDouble());
        verify(decorator, times(2)).addNodeMouseDownHandler(anyObject());
        verify(decorator, times(2)).addNodeMouseClickHandler(anyObject());

    }

    @Test
    public void testCreateDecoratorCallback() {
        arrayOfPrimitives = new IPrimitive[] {firstPrimitive, secondPrimitive};

        miniPalette = spy(new MiniPalette()
                .setIconSize(ICON_SIZE)
                .setItemCallback(callback)
                .setPadding(PADDING)
                .setX(X)
                .setY(Y));

        ItemCallback callback = spy(miniPalette.createDecoratorCallback(1));
        callback.onShow(6.0, 4.0);
        verify(miniPalette).doShowItem(1, 6.0, 4.0);

        callback.onHide();
        verify(miniPalette).doItemOut(1);
    }

    @Test
    public void testClear() {
        miniPalette.clear();
        verify(miniPalette).removeAll();
    }

    @Test
    public void testNullCallback() {
        miniPalette.setItemCallback(null);
        miniPalette.doShowItem(1, 4, 3.2);
        miniPalette.doItemOut(1);
        miniPalette.onItemMouseDown(1, 4, 7);
        miniPalette.onItemClick(1, 2, 3);
    }

    @Test
    public void testCallback() {
        miniPalette.setItemCallback(callback);
        miniPalette.doShowItem(1, 4, 3.2);
        verify(callback).onItemHover(1, 4.0, 3.2);
        miniPalette.doItemOut(1);
        verify(callback).onItemOut(1);
        miniPalette.onItemMouseDown(1, 4, 7);
        verify(callback).onItemMouseDown(1, 4, 7);
        miniPalette.onItemClick(1, 2, 3);
        verify(miniPalette).onItemClick(1, 2, 3);
    }
}
