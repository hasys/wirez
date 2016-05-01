package org.wirez.core.client.canvas.controls.toolbox.command;

import com.ait.lienzo.client.core.shape.Shape;
import com.google.gwt.logging.client.LogConfiguration;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.definition.Definition;
import org.wirez.core.api.lookup.util.CommonLookups;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.AbstractCanvas;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
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
public class AddNodeCommand implements ToolboxCommand{

    private static Logger LOGGER = Logger.getLogger(AddNodeCommand.class.getName());
    
    public interface View {

        View init(AddNodeCommand presenter);

        View showPalette(org.wirez.core.client.shape.Shape shape, int x, int y, ShapeGlyph... glyphs);

        View clear();

    }
    
    private final com.ait.lienzo.client.core.shape.Shape<?> icon;

    CommonLookups commonLookups;
    ShapeManager shapeManager;
    View view;

    @Inject
    public AddNodeCommand(final CommonLookups commonLookups,
                          final ShapeManager shapeManager,
                          final View view) {
        this.commonLookups = commonLookups;
        this.shapeManager = shapeManager;
        this.view = view;
        this.icon = SVGUtils.createSVGIcon(SVGUtils.getCreateConnection());
    }
    
    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public Shape<?> getIcon() {
        return icon;
    }

    @Override
    public String getTitle() {
        return "Create a new node";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(final Context context, 
                        final Element element) {

        final AbstractCanvasHandler canvasHandler = context.getCanvasHandler();
        final AbstractCanvas canvas = canvasHandler.getCanvas();
        final org.wirez.core.client.shape.Shape shape = canvas.getShape( element.getUUID() );
        final Node<? extends Definition<Object>, ? extends Edge> node = (Node<? extends Definition<Object>, ? extends Edge>) element;
        
        
        final Set<String> allowedDefinitions = commonLookups.getAllowedDefinitions( "org.wirez.bpmn.api.BPMNDefinitionSet", 
                canvasHandler.getDiagram().getGraph(), node,  "org.wirez.bpmn.api.SequenceFlow", 0, 10);
        
        // log( Level.SEVERE, "Allowed Definitions -> " + allowedDefinitions );
        
        if ( null != allowedDefinitions && !allowedDefinitions.isEmpty() ) {

            final int cx = (int) context.getX();
            final int cy = (int) context.getY();
            final List<ShapeGlyph> glyphs = new LinkedList<>();

            for ( final String allowedDefId : allowedDefinitions ) {
                final ShapeFactory<?, ? ,?> factory = shapeManager.getFactory( allowedDefId );
                final ShapeGlyph glyph = factory.getGlyphFactory().build(50, 50);
                glyphs.add( glyph );
            }

            view.showPalette( shape, cx, cy, glyphs.toArray( new ShapeGlyph[ glyphs.size() ] ) );

        }
        
    }
    
    public void clear() {
        view.clear();
    }
    
    void onPaletteLostFocus() {
        view.clear();
    }
    
    private void log(final Level level, final String message) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log(level, message);
        }
    }
    
}
