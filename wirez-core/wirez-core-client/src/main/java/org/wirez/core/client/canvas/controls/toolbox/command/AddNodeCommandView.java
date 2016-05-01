package org.wirez.core.client.canvas.controls.toolbox.command;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.util.Geometry;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.view.ShapeGlyph;
import org.wirez.lienzo.toolbox.grid.Grid;

import javax.enterprise.context.Dependent;
import java.util.Iterator;

@Dependent
public class AddNodeCommandView implements AddNodeCommand.View{

    private AddNodeCommand presenter ;
    
    @Override
    public AddNodeCommand.View init(final AddNodeCommand presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public AddNodeCommand.View showPalette(final Shape shape, 
                                           final int x, 
                                           final int y,
                                           final ShapeGlyph... glyphs) {
        final WiresShape wiresShape = (WiresShape) shape.getShapeView();
        showMiniPalette( wiresShape, x, y, glyphs );
        return this;
    }

    @Override
    public AddNodeCommand.View clear() {
        // TODO
        return this;
    }
    
    private void showMiniPalette(final WiresShape shape, 
                                 final int x, 
                                 final int y,
                                 final ShapeGlyph... glyphs) {
        
        final Grid grid = new Grid(2, 16, glyphs.length, 1);

        final WiresManager manager = WiresManager.get( shape.getWiresLayer().getLayer() );
        final WiresShape miniPallete = manager.createShape(new MultiPath().rect(x - 1, y - 1, grid.getWidth() + 1, grid.getHeight() + 1));
        manager.createMagnets(shape);
        final Group group = new Group();
        Iterator<Grid.Point> pointIterator = grid.iterator();

        Grid.Point point = null;

        for ( int c = 0; c < glyphs.length; c++ ) {
            point = pointIterator.next();
            final Group gGroup = glyphs[c].getGroup();
            
            Geometry.setScaleToFit(gGroup, 16, 16);
            gGroup.setX(x + point.getX());
            gGroup.setY(y + point.getY());
            group.add(gGroup);
        }
        
        manager.getSelectionManager().setChangeInProgress(true);
        manager.createMagnets(miniPallete);
        miniPallete.getGroup().add(group);
        
    }
    
}
