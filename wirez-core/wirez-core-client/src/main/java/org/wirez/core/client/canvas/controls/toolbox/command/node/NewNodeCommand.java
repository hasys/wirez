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
import org.wirez.core.client.components.glyph.GlyphTooltip;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.shape.view.ShapeGlyph;
import org.wirez.core.client.util.SVGUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class NewNodeCommand implements ToolboxCommand {

    private static Logger LOGGER = Logger.getLogger(NewNodeCommand.class.getName());
    private static final int PADDING = 10;
    private static final int ICON_SIZE = 10;
    private static final int TIMEOUT = 3000;
    
    public interface View {

        View init(NewNodeCommand presenter);
        
        View setPadding(int padding);
        
        View setIconSize(int iconSize);
        
        View setTimeout(int timeout);

        View showPalette(org.wirez.core.client.shape.Shape shape, int x, int y, ShapeGlyph... glyphs);

        View clear();

    }
    
    private final com.ait.lienzo.client.core.shape.Shape<?> icon;

    CommonLookups commonLookups;
    ShapeManager shapeManager;
    GlyphTooltip glyphTooltip;
    View view;
    
    private String edgeId;
    private String[] definitionIds;
    private ShapeFactory<?, ?, ?>[] factories;

    @Inject
    public NewNodeCommand(final CommonLookups commonLookups,
                          final ShapeManager shapeManager,
                          final GlyphTooltip glyphTooltip,
                          final View view) {
        this.commonLookups = commonLookups;
        this.shapeManager = shapeManager;
        this.glyphTooltip = glyphTooltip;
        this.view = view;
        this.icon = SVGUtils.createSVGIcon(SVGUtils.getAddIcon());
    }
    
    @PostConstruct
    public void init() {
        view.init( this );
        view.setPadding( PADDING );
        view.setIconSize( ICON_SIZE );
        view.setTimeout( TIMEOUT );
    }

    @Override
    public Shape<?> getIcon() {
        return icon;
    }

    @Override
    public String getTitle() {
        return "Creates a new node";
    }

    public NewNodeCommand setEdgeIdentifier(final String edgeId) {
        this.edgeId = edgeId;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(final Context context, 
                        final Element element) {

        if ( null == edgeId ) {
            throw new NullPointerException(" New node command requires an edge identifier.");
        }
        
        final AbstractCanvasHandler canvasHandler = context.getCanvasHandler();
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

            view.showPalette( shape, cx, cy, glyphs.toArray( new ShapeGlyph[ glyphs.size() ] ) );

        }
        
    }
    
    public void clear() {
        this.definitionIds = null;
        this.factories = null;
        view.clear();
    }

    void onItemHover(final int index,
                     final double x,
                     final double y) {

        final ShapeFactory<?, ?, ?> factory = factories[ index ];
        final ShapeGlyph glyph = factory.getGlyphFactory().build (100, 100 );
        final double px = x + PADDING;
        final double py = y - ( PADDING * ( index + 1 ) );
        glyphTooltip.show( glyph, factory.getDescription(), px, py );
        
    }

    void onItemOut(final int index) {

        glyphTooltip.hide();
        
    }
    
    void onDragProxyMove(final int index,
                         final int x,
                         final int y) {

        GWT.log("ProxyMove - [index=" + index + ", x=" + x + ", y=" + y + "]");
        
    }
    
    void onDragProxyEnd(final int index,
                        final int x,
                        final int y) {

        GWT.log("ProxyEnd - [index=" + index + ", x=" + x + ", y=" + y + "]");
        
    }
    
    private void log(final Level level, final String message) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log(level, message);
        }
    }
    
}
