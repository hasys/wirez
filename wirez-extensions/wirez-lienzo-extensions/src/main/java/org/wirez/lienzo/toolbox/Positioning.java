package org.wirez.lienzo.toolbox;

import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.core.util.Geometry;
import com.ait.lienzo.shared.core.types.Direction;

public class Positioning {
    static Point2D anchorFor(BoundingBox boundingBox, Direction direction) {

        Point2DArray cardinals = Geometry.getCardinals(boundingBox);
        switch (direction) {
            case NORTH:
                return cardinals.get(1);
            case SOUTH:
                return cardinals.get(5);
            case EAST:
                return cardinals.get(3);
            case WEST:
                return cardinals.get(7);
            case NONE:
                return cardinals.get(0);
            case NORTH_EAST:
                return cardinals.get(2);
            case SOUTH_EAST:
                return cardinals.get(4);
            case SOUTH_WEST:
                return cardinals.get(6);
            case NORTH_WEST:
                return cardinals.get(8);
        }
        throw new RuntimeException("invalid direction");
    }
}
