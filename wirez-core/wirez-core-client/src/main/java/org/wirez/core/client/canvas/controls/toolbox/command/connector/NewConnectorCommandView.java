package org.wirez.core.client.canvas.controls.toolbox.command.connector;

import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.ColorName;
import org.wirez.core.client.animation.ShapeDeSelectionAnimation;
import org.wirez.core.client.animation.ShapeSelectionAnimation;
import org.wirez.core.client.canvas.Canvas;
import org.wirez.core.client.canvas.lienzo.LienzoLayer;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.view.event.*;

import javax.enterprise.context.Dependent;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class NewConnectorCommandView implements NewConnectorCommand.View {

    private static Logger LOGGER = Logger.getLogger("org.wirez.core.client.control.toolbox.command.AddConnectionCommandView");
    private NewConnectorCommand presenter;

    private Canvas canvas;
    private IPrimitive<?> connector;
    private Point2D lineStart;
    private MouseMoveHandler mouseMoveHandler;
    private MouseClickHandler mouseClickHandler;

    @Override
    public NewConnectorCommand.View init(final NewConnectorCommand presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public NewConnectorCommand.View show(final Canvas canvas, final double x, final double y) {
        this.canvas = canvas;
        doShow(x, y);
        return this;
    }

    @Override
    public NewConnectorCommand.View highlight(final Canvas canvas, final Shape shape) {
        new ShapeSelectionAnimation(shape)
                .setCanvas(canvas)
                .setDuration(200)
                .run();
        return this;
    }

    @Override
    public NewConnectorCommand.View unhighlight(final Canvas canvas, final Shape shape) {
        new ShapeDeSelectionAnimation(shape, 0, 0, ColorName.BLACK)
                .setCanvas(canvas)
                .setDuration(200)
                .run();

        return this;
    }


    @Override
    public NewConnectorCommand.View clear() {
        final LienzoLayer layer = (LienzoLayer) canvas.getLayer();
        if ( null != this.mouseClickHandler ) {
            layer.removeHandler(this.mouseClickHandler);
        }
        if ( null != this.mouseMoveHandler ) {
            layer.removeHandler(this.mouseMoveHandler);
        }
        if ( null != connector ) {
            connector.removeFromParent();
        }
        connector = null;
        lineStart = null;
        return this;
    }

    private void doShow(double x, double y) {
        
        final LienzoLayer layer = (LienzoLayer) canvas.getLayer();
        
        lineStart = new Point2D(x, y);
        // connector = createLine(x, y,  x + 1, y + 1);
        connector = createArrow(x, y,  x + 1, y + 1);
        layer.getLienzoLayer().add(connector);

        
        this.mouseMoveHandler = new MouseMoveHandler() {
            @Override
            public void handle(final MouseMoveEvent event) {
                final int x = (int) event.getMouseX();
                final int y = (int) event.getMouseY();
                // updateLineTo((OrthogonalPolyLine) connector, x, y);
                updateArrowTo((Arrow) connector, x, y);
                presenter.onMouseMove(x, y);
                LOGGER.log(Level.INFO, "Mouse at [" + x + ", " + y+ "]");
                layer.draw();
            }
        };
        
        layer.addHandler(ViewEventType.MOUSE_MOVE, this.mouseMoveHandler);

        this.mouseClickHandler = new MouseClickHandler() {

            @Override
            public void handle(final MouseClickEvent event) {
                final int x = (int) event.getMouseX();
                final int y = (int) event.getMouseY();
                presenter.onMouseClick(x, y);
            }
        };
        
        layer.addHandler(ViewEventType.MOUSE_CLICK, this.mouseClickHandler);
        
        layer.draw();
    }

    private OrthogonalPolyLine createLineEnd2End(final double x0, final double y0, final double x1, final double y1)
    {
        return createLine(x0, y0, (x0 + ((x1 - x0) / 2)), (y0 + ((y1 - y0) / 2)), x1, y1);
    }

    private OrthogonalPolyLine createLine(final double... points)
    {
        return new OrthogonalPolyLine(Point2DArray.fromArrayOfDouble(points)).setCornerRadius(5).setDraggable(true);
    }

    private void updateLineTo(final OrthogonalPolyLine _line, final double x, final double y)
    {
        Point2D p = new Point2D(x, y);
        _line.setPoint2DArray(new Point2DArray(lineStart, p));
    }
    
    private Arrow createArrow(final double x0, final double y0, final double x1, final double y1 ) {
        return new Arrow(new Point2D(x0, y0), new Point2D(x1, y1), 5d, 25d, 45d, 0d, ArrowType.AT_END)
                .setStrokeColor(ColorName.BLACK)
                .setFillColor(ColorName.BLACK);
    }

    private void updateArrowTo(final Arrow _arrow, final double x, final double y)
    {
        Point2D p = new Point2D(x, y);
        _arrow.setEnd(p);
    }
}
