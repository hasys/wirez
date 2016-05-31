package org.wirez.lienzo.grid;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.wirez.lienzo.grid.Grid.Point;

import static org.junit.Assert.*;

public class GridTest {

    private final static int PADDING = 10;
    private final static int ICON_SIZE = 20;
    private final static int ROWS_COUNT = 30;
    private final static int COLUMNS_COUNT = 40;

    Grid grid = new Grid(PADDING, ICON_SIZE, ROWS_COUNT, COLUMNS_COUNT);

    @Test
    public void testFindSimplePosition() {
        Point p = grid.findPosition(4, 5);
        assertEquals(160, p.getX());
        assertEquals(130, p.getY());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroColumnsCountGrid() {
        new Grid(1, 1, 2, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroRowsCountGrid() {
        new Grid(1, 1, 0, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativePadding() {
        new Grid(-1, 1, 3, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeIconSize() {
        new Grid(1, -1, 3, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindSimplePositionRowIsTooBig() {
        grid.findPosition(10000, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindSimplePositionColIsTooBig() {
        grid.findPosition(5, 10000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindSimplePositionNegativeRow() {
        grid.findPosition(-1, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindSimplePositionNegativeCol() {
        grid.findPosition(5, -1);
    }

    @Test
    public void testGetWidth() {
        assertEquals(1210, grid.getWidth());
    }

    @Test
    public void testGetHeight() {
        assertEquals(910, grid.getHeight());
    }

    @Test
    public void testSize() {
        assertEquals(1200, grid.size());
    }

    @Test
    public void testIterator() {
        Iterator iterator = grid.iterator();
        int cellCount = 0;
        while (iterator.hasNext()) {
            iterator.next();
            cellCount++;
        }
        assertEquals(grid.size(), cellCount);
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorOverflow() {
        Iterator iterator = grid.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemoveNotSupported() {
        grid.iterator().remove();
    }
}
