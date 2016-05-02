package org.wirez.core.client.canvas.controls.toolbox.command.node;

import com.ait.lienzo.client.core.shape.Shape;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.LogConfiguration;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.definition.Definition;
import org.wirez.core.api.lookup.util.CommonLookups;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.AbstractCanvas;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.controls.toolbox.command.Context;
import org.wirez.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.wirez.core.client.components.drag.CanvasDragProxy;
import org.wirez.core.client.components.drag.DragProxy;
import org.wirez.core.client.components.glyph.GlyphMiniPalette;
import org.wirez.core.client.components.glyph.GlyphTooltip;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.shape.view.ShapeGlyph;
import org.wirez.core.client.util.SVGUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class NewNodeCommand implements ToolboxCommand {

    private static Logger LOGGER = Logger.getLogger(NewNodeCommand.class.getName());
    private static final int PADDING = 10;
    private static final int ICON_SIZE = 16;
    
    private final com.ait.lienzo.client.core.shape.Shape<?> icon;

    CommonLookups commonLookups;
    ShapeManager shapeManager;
    GlyphTooltip glyphTooltip;
    GlyphMiniPalette glyphMiniPalette;
    CanvasDragProxy canvasDragProxy;
    
    private String[] definitionIds;
    private ShapeFactory<?, ?, ?>[] factories;
    private AbstractCanvasHandler canvasHandler;
    private org.wirez.core.client.shape.Shape shape;

    @Inject
    public NewNodeCommand(final CommonLookups commonLookups,
                          final ShapeManager shapeManager,
                          final GlyphTooltip glyphTooltip,
                          final GlyphMiniPalette glyphMiniPalette,
                          final CanvasDragProxy canvasDragProxy) {
        this.commonLookups = commonLookups;
        this.shapeManager = shapeManager;
        this.glyphTooltip = glyphTooltip;
        this.glyphMiniPalette = glyphMiniPalette;
        this.canvasDragProxy = canvasDragProxy;
        this.icon = SVGUtils.createSVGIcon(SVGUtils.getAddIcon());
    }
    
    protected abstract String getDefinitionSetIdentifier();

    protected abstract String getEdgeIdentifier();
    
    @PostConstruct
    public void init() {
        glyphMiniPalette.setItemOutCallback(NewNodeCommand.this::onItemOut);
        glyphMiniPalette.setItemHoverCallback(NewNodeCommand.this::onItemHover);
        glyphMiniPalette.setItemClickCallback(NewNodeCommand.this::onItemClick);
        glyphMiniPalette.setItemMouseDownCallback(NewNodeCommand.this::onItemMouseDown);
        glyphMiniPalette.setPadding( PADDING );
        glyphMiniPalette.setIconSize( ICON_SIZE );
    }

    @Override
    public Shape<?> getIcon() {
        return icon;
    }

    @Override
    public String getTitle() {
        return "Creates a new node";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(final Context context, 
                        final Element element) {

        if ( null == getEdgeIdentifier() ) {
            throw new NullPointerException(" New node command requires an edge identifier.");
        }
        
        this.canvasHandler = context.getCanvasHandler();
        final AbstractCanvas canvas = canvasHandler.getCanvas();
        final org.wirez.core.client.shape.Shape shape = canvas.getShape( element.getUUID() );
        final Node<? extends Definition<Object>, ? extends Edge> node = (Node<? extends Definition<Object>, ? extends Edge>) element;
        
        
        final Set<String> allowedDefinitions = commonLookups.getAllowedDefinitions( "org.wirez.bpmn.api.BPMNDefinitionSet", 
                canvasHandler.getDiagram().getGraph(), node,  "org.wirez.bpmn.api.SequenceFlow", 0, 10);
        
        log( Level.FINE, "Allowed Definitions -> " + allowedDefinitions );
        
        if ( null != allowedDefinitions && !allowedDefinitions.isEmpty() ) {

            this.definitionIds = new String[ allowedDefinitions.size() ];
            this.factories = new ShapeFactory<?, ? ,?>[ allowedDefinitions.size() ];
            
            final int cx = (int) context.getX();
            final int cy = (int) context.getY();
            final List<ShapeGlyph> glyphs = new LinkedList<>();
            int counter = 0;
            for ( final String allowedDefId : allowedDefinitions ) {
                final ShapeFactory<?, ? ,?> factory = shapeManager.getFactory( allowedDefId );
                final ShapeGlyph glyph = factory.getGlyphFactory().build(50, 50);

                this.definitionIds[counter] = allowedDefId;
                this.factories[counter] = factory;
                glyphs.add( glyph );
                counter++;
            }
            
            glyphMiniPalette
                    .setX( cx )
                    .setY( cy )
                    .show( canvas.getLayer(), glyphs.toArray( new ShapeGlyph[ glyphs.size() ] ) );

        }
        
    }
    
    public void clear() {
        this.definitionIds = null;
        this.factories = null;
        glyphMiniPalette.clear();
    }

    void onItemHover(final int index,
                     final double x,
                     final double y) {

        final ShapeFactory<?, ?, ?> factory = factories[ index ];
        final ShapeGlyph glyph = factory.getGlyphFactory().build (100, 100 );
        final double px = x + PADDING + ICON_SIZE;
        final double py = y + ( ( PADDING + ICON_SIZE ) * ( index + 1 ) );
        glyphTooltip.show( glyph, factory.getDescription(), px, py );
        
    }

    void onItemOut(final int index) {

        glyphTooltip.hide();
        
    }
    
    void onItemClick(final int index,
                         final int x,
                         final int y) {

        // TODO
        
        GWT.log("Click - [index=" + index + ", x=" + x + ", y=" + y + "]");
        
    }
    
    void onItemMouseDown(final int index,
                        final int x,
                        final int y) {

        GWT.log("MouseDown - [index=" + index + ", x=" + x + ", y=" + y + "]");

        final String defId = definitionIds[ index ];
        final ShapeFactory<?, ?, ?> factory = factories[ index ];
        
        canvasDragProxy.create(canvasHandler.getCanvas().getLayer(), x, y, new CanvasDragProxy.CanvasProxyItem() {
           
            @Override
            public String getDefinitionSetId() {
                return NewNodeCommand.this.getDefinitionSetIdentifier();
            }

            @Override
            public String getDefinitionId() {
                return defId;
            }

            @Override
            public ShapeFactory<?, ?, ?> getShapeFactory() {
                return factory;
            }

            @Override
            public AbstractCanvasHandler getCanvasHandler() {
                return canvasHandler;
            }
            
        }, new DragProxy.Callback() {
            
            @Override
            public void onMove(final int x, final int y) {
                
            }

            @Override
            public void onComplete(final int x, final int y) {

            }
            
        });
        
        
    }

    private void log(final Level level, final String message) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log(level, message);
        }
    }
    
}
