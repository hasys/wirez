package org.wirez.lienzo.toolbox;

import com.ait.lienzo.client.core.Attribute;
import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.event.AnimationFrameAttributesChangedBatcher;
import com.ait.lienzo.client.core.event.AttributesChangedEvent;
import com.ait.lienzo.client.core.event.AttributesChangedHandler;
import com.ait.lienzo.client.core.event.IAttributesChangedBatcher;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.tooling.common.api.flow.Flows;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.wirez.lienzo.grid.Grid;
import org.wirez.lienzo.toolbox.builder.AbstractBuilder;
import org.wirez.lienzo.toolbox.grid.GridToolbox;

import java.util.Iterator;
import java.util.List;

import static com.ait.lienzo.client.core.AttributeOp.any;

public class HoverToolbox implements GridToolbox {

    private static final Flows.BooleanOp XYWH_OP = any(Attribute.X, Attribute.Y, Attribute.WIDTH, Attribute.HEIGHT);

    private final WiresShape shape;
    private final Direction anchor;
    private final Direction towards;
    private final List<ToolboxButton> buttons;
    private final HandlerRegistrationManager handlerRegistrationManager = new HandlerRegistrationManager();
    private IAttributesChangedBatcher attributesChangedBatcher = new AnimationFrameAttributesChangedBatcher();

    private final HoverTimer hoverTimer = new HoverTimer(new HoverTimer.Actions() {
        @Override
        public void onMouseEnter() {
            HoverToolbox.this.show();
        }

        @Override
        public void onMouseExit() {
            HoverToolbox.this.hide();
        }

        @Override
        public boolean isReadyToHide() {
            return HoverToolbox.this.showing;
        }
    });
    private final Grid grid;
    private boolean showing;
    private Group group;

    private void initialize() {

        if (grid.size() < buttons.size()) {
            throw new IllegalStateException("added more buttons than the grid can contain");
        }

        this.group = new Group().setDraggable( false );
        
        Iterator<Grid.Point> gridIterator = grid.iterator();
        for (ToolboxButton button : buttons) {
            Grid.Point point = gridIterator.next();
            button.getShape().setX(point.getX()).setY(point.getY());
            button.getShape().getGroup().setAlpha(0);
            HandlerRegistration hr1 = button.getDecorator().addNodeMouseEnterHandler(hoverTimer);
            HandlerRegistration hr2 = button.getDecorator().addNodeMouseExitHandler(hoverTimer);
            handlerRegistrationManager.register(hr1);
            handlerRegistrationManager.register(hr2);
            group.add( button.getShape().getGroup() );
        }
        
        this.shape.getWiresLayer().getLayer().add( group );

        reposition();
        initAttributesChangedHandler();
        
        this.shape.getWiresLayer().getLayer().batch();
    }

    private void initAttributesChangedHandler()
    {
        shape.getGroup().setAttributesChangedBatcher(attributesChangedBatcher);

        final AttributesChangedHandler handler = new AttributesChangedHandler()
        {
            @Override
            public void onAttributesChanged(AttributesChangedEvent event)
            {
                if (event.evaluate(XYWH_OP))
                {
                    reposition();
                }
            }
        };

        // Attribute change handlers.
        shape.getGroup().addAttributesChangedHandler(Attribute.X, handler);
        shape.getGroup().addAttributesChangedHandler(Attribute.Y, handler);
        shape.getPath().addAttributesChangedHandler(Attribute.WIDTH, handler);
        shape.getPath().addAttributesChangedHandler(Attribute.HEIGHT, handler);
    }
    
    private void reposition() {
        final double gx = shape.getGroup().getAbsoluteLocation().getX();
        final double gy = shape.getGroup().getAbsoluteLocation().getY();
        final Point2D anchorPoint = Positioning.anchorFor(this.shape.getPath().getBoundingPoints().getBoundingBox(), this.anchor);
        final Grid.Point toolboxPosition = this.grid.findPosition(new Grid.Point((int) anchorPoint.getX(), (int) anchorPoint.getY()), this.towards);
        group.setX(gx + toolboxPosition.getX());
        group.setY(gy + toolboxPosition.getY());
    }
    
    @Override
    public void show() {
        
        if (!showing) {
            for (ToolboxButton button : buttons) {
                button.getShape().getGroup().animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)), 500, new AnimationCallback());
                button.getShape().getGroup().addNodeMouseEnterHandler(hoverTimer);
                button.getShape().getGroup().addNodeMouseExitHandler(hoverTimer);
            }

            showing = true;
        }
        
    }

    @Override
    public void remove() {

        attributesChangedBatcher.cancelAttributesChangedBatcher();
        handlerRegistrationManager.removeHandler();
        
        for (ToolboxButton button : buttons) {
            button.remove();
        }
      
    }

    @Override
    public void hide() {
        
        if (showing) {
            for (final ToolboxButton button : buttons) {
                button.getShape().getGroup().animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(0)), 500, new AnimationCallback() {
                    @Override
                    public void onClose(final IAnimation animation, 
                                        final IAnimationHandle handle) {
                    }
                });
            }
            
            showing = false;
        }
        
    }
    
    Layer getLayer() {
        return shape.getWiresLayer().getLayer();
    }

    private HoverToolbox(WiresShape shape, Direction anchor, Direction towards, int rows, int cols, List<ToolboxButton> buttons) {
        this.shape = shape;
        this.anchor = anchor;
        this.towards = towards;
        this.buttons = buttons;
        this.grid = new Grid(2, 16, rows, cols);
        initialize();
        registerHandlers();
    }

    private void registerHandlers() {
        this.shape.getPath().addNodeMouseEnterHandler(this.hoverTimer);
        this.shape.getPath().addNodeMouseExitHandler(this.hoverTimer);
    }

    public static class HoverToolboxBuilder extends AbstractBuilder {
        public HoverToolboxBuilder(WiresShape shape) {
            super(shape);
        }

        @Override
        public HoverToolbox register() {
            return new HoverToolbox(this.shape, this.anchor, this.towards, this.rows, this.cols, this.buttons);
        }
    }
}
