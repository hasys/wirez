package org.wirez.core.client.canvas.controls.toolbox.command.connector;

import com.google.gwt.core.client.GWT;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Graph;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.definition.Definition;
import org.wirez.core.api.graph.processing.util.GraphBoundsIndexer;
import org.wirez.core.api.lookup.util.CommonLookups;
import org.wirez.core.client.canvas.Canvas;
import org.wirez.core.client.canvas.ShapeState;
import org.wirez.core.client.canvas.controls.toolbox.command.Context;
import org.wirez.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.wirez.core.client.shape.HasDecorators;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.view.HasCanvasState;
import org.wirez.core.client.util.SVGUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Set;

@Dependent
public class NewConnectorCommand implements ToolboxCommand {

    private final com.ait.lienzo.client.core.shape.Shape<?> icon;
    
    public interface Callback {
        
        void init(Element source, String edgeId);
        
        boolean isAllowed(Context context, Node target);
        
        void accept(Context context, Node target);
        
    }
    
    public interface View {
        
        View init(NewConnectorCommand presenter);
        
        View show(Canvas canvas, double x, double y);

        View highlight(Canvas canvas, Shape shape);

        View unhighlight(Canvas canvas, Shape shape);
                
        View clear();
        
    }

    CommonLookups commonLookups;
    Callback callback;
    View view;

    private String edgeId;
    
    @Inject
    public NewConnectorCommand(final CommonLookups commonLookups,
                               final Callback callback,
                               final View view) {
        this.commonLookups = commonLookups;
        this.callback = callback;
        this.view = view;
        this.icon = SVGUtils.createSVGIcon(SVGUtils.getConnectorIcon());
    }
    
    @PostConstruct
    public void  init() {
        view.init(this);
    }

    private GraphBoundsIndexer boundsIndexer;
    private Context context;
    private Element element;
    private Shape shape;


    @Override
    public com.ait.lienzo.client.core.shape.Shape<?> getIcon() {
        return icon;
    }

    @Override
    public String getTitle() {
        return "Creates a new connector";
    }

    public NewConnectorCommand setEdgeIdentifier(final String edgeId ) {
        this.edgeId = edgeId;
        return this;
    }

    @Override
    public void execute(final Context context, final Element element) {
        this.element = element;
        this.context = context;
        this.boundsIndexer = new GraphBoundsIndexer(context.getCanvasHandler().getDiagram().getGraph());
        onCallbackInit( context, element );
        view.show(context.getCanvasHandler().getCanvas(),
                context.getX(), context.getY());
    }
    
    private void clear() {
        unhighLightShape();
        view.clear();
        context = null;
        element = null;
        shape = null;
    }
    
    private void highLightShape(final Node node) {
        if ( null != this.shape && !node.getUUID().equals(shape.getUUID()) ) {
            unhighLightShape();
        }
        if ( null == this.shape ) {
            final String uuid = node.getUUID();
            final Shape shape = getShape(uuid);
            if ( null != shape ) {
                this.shape = shape;

                if (shape.getShapeView() instanceof HasCanvasState) {
                    final HasCanvasState canvasStateMutation = (HasCanvasState) shape.getShapeView();
                    canvasStateMutation.applyState(ShapeState.HIGHLIGHT);
                } else if (shape.getShapeView() instanceof HasDecorators) {
                    view.highlight(context.getCanvasHandler().getCanvas(), shape);
                    
                }
                
            }
        }
    }

    private void unhighLightShape() {
        if ( null != this.shape ) {

            if (shape instanceof HasCanvasState) {
                final HasCanvasState canvasStateMutation = (HasCanvasState) shape.getShapeView();
                canvasStateMutation.applyState(ShapeState.UNHIGHLIGHT);
            } else if (shape.getShapeView() instanceof HasDecorators) {
                view.unhighlight(context.getCanvasHandler().getCanvas(), shape);
            }

            this.shape = null;
            
        }
    }

   
    private Shape getShape(final String uuid) {
        return context.getCanvasHandler().getCanvas().getShape(uuid);
    }
    
    void onMouseMove(final double x, final double y) {
        final Node node = boundsIndexer.getNodeAt(x, y);
        if ( null != node 
                && !node.getUUID().equals(element.getUUID())
                && callback.isAllowed( context, node ) ) {
            highLightShape(node);
        } else {
            unhighLightShape();
        }
    }

    void onMouseClick(final double x, final double y) {
        if ( null != callback  ) {
            final Node node = boundsIndexer.getNodeAt(x, y);
            if ( null != node && !node.getUUID().equals(element.getUUID()) ) {
                callback.accept(context,node);
            }
        }
        clear();
    }

    // TODO: Use pagination results on mini-palette.
    private void onCallbackInit( final Context context, final Element element ) {

        if ( null == edgeId ) {
            throw new NullPointerException(" New connector command requires an edge identifier.");
        }
        
        callback.init(element, edgeId);
        
        final String defSetId = context.getCanvasHandler().getDiagram().getSettings().getDefinitionSetId();
        final Node<? extends Definition<Object>, ? extends Edge> sourceNode = 
                (Node<? extends Definition<Object>, ? extends Edge>) element;

        final Set<String> allowedConnectors =
                commonLookups.getAllowedConnectors( defSetId,
                        sourceNode, 
                        0,
                        10);
        
        GWT.log( "************* Allowed connectors = " + allowedConnectors.toString() );
        
        final Graph graph = context.getCanvasHandler().getDiagram().getGraph();

        final Set<String> allowedDefinitions=
                commonLookups.getAllowedDefinitions( defSetId, graph, sourceNode, "org.wirez.bpmn.api.SequenceFlow", 0 ,50);

        GWT.log( "************* Allowed definitions for a SequenceFlow = " + allowedDefinitions.toString() );
        
    }
    
}
