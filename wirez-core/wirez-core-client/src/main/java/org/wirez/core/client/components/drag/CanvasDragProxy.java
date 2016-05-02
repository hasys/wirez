package org.wirez.core.client.components.drag;

import com.google.gwt.core.client.GWT;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.definition.Definition;
import org.wirez.core.api.graph.util.GraphBoundsIndexer;
import org.wirez.core.api.lookup.LookupManager;
import org.wirez.core.api.lookup.definition.DefinitionLookupManager;
import org.wirez.core.api.lookup.definition.DefinitionLookupRequest;
import org.wirez.core.api.lookup.definition.DefinitionLookupRequestImpl;
import org.wirez.core.api.lookup.definition.DefinitionRepresentation;
import org.wirez.core.api.rule.RuleViolation;
import org.wirez.core.api.rule.RuleViolations;
import org.wirez.core.api.rule.model.ModelContainmentRuleManager;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.Layer;
import org.wirez.core.client.canvas.util.CanvasHighlight;
import org.wirez.core.client.components.glyph.GlyphDragProxy;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.shape.view.ShapeGlyph;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Set;

@Dependent
public class CanvasDragProxy implements DragProxy<CanvasDragProxy.CanvasProxyItem> {
    
    public interface CanvasProxyItem {

        String getDefinitionSetId();
        
        String getDefinitionId();
        
        ShapeFactory<?, ? ,?> getShapeFactory();
        
        AbstractCanvasHandler getCanvasHandler();
    }

    ClientDefinitionManager clientDefinitionManager;
    DefinitionLookupManager definitionLookupManager;
    GraphBoundsIndexer graphBoundsIndexer;
    ModelContainmentRuleManager containmentRuleManager;
    ShapeManager shapeManager;
    GlyphDragProxy glyphDragProxy;

    private CanvasHighlight canvasHighlight;

    @Inject
    public CanvasDragProxy(final ClientDefinitionManager clientDefinitionManager, 
                           final DefinitionLookupManager definitionLookupManager, 
                           final GraphBoundsIndexer graphBoundsIndexer,
                           final ModelContainmentRuleManager containmentRuleManager, 
                           final ShapeManager shapeManager, 
                           final GlyphDragProxy glyphDragProxy) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.definitionLookupManager = definitionLookupManager;
        this.graphBoundsIndexer = graphBoundsIndexer;
        this.containmentRuleManager = containmentRuleManager;
        this.shapeManager = shapeManager;
        this.glyphDragProxy = glyphDragProxy;
    }

    @Override
    public void create(final Layer layer, 
                       final int x, 
                       final int y, 
                       final CanvasProxyItem item, 
                       final Callback callback) {

        this.canvasHighlight = new CanvasHighlight( item.getCanvasHandler() );
        this.graphBoundsIndexer.forGraph( item.getCanvasHandler().getDiagram().getGraph() );
        
        final ShapeFactory<?, ? ,?> factory = item.getShapeFactory();

        final DefinitionLookupRequest request = new DefinitionLookupRequestImpl.Builder()
                .definitionSetId( item.getDefinitionSetId() )
                .id( item.getDefinitionId() )
                .build();

        final LookupManager.LookupResponse<DefinitionRepresentation> response = 
                definitionLookupManager.lookup( request );
        
        final Set<String> labels = response.getResults().get( 0 ).getLabels();

        final ShapeGlyph glyph = factory.getGlyphFactory().build (100, 100 );
        
        glyphDragProxy.create(layer, x, y, glyph, new DragProxy.Callback() {
            
            @Override
            public void onMove(final int x,
                               final int y) {

                final Node<? extends Definition<?>, Edge> parent = graphBoundsIndexer.get(x, y);

                boolean isValid = false;
                
                if ( null != parent ) {

                    final Object parentDefinition = parent.getContent().getDefinition();
                    final DefinitionAdapter<Object> parentAdapter= clientDefinitionManager.getDefinitionAdapter( parentDefinition.getClass() );
                    final String parentId = parentAdapter.getId( parentDefinition );
                    final RuleViolations violations = containmentRuleManager.evaluate( parentId, labels );
                    isValid = isValid( violations );
                }

                if ( isValid ) {
                    
                    canvasHighlight.highLight( parent );
                    
                } else {

                    canvasHighlight.unhighLight();
                    
                }

            }

            @Override
            public void onComplete(final int x,
                                   final int y) {

                final Node<? extends Definition<?>, Edge> parent = graphBoundsIndexer.get(x, y);

                GWT.log("CanvasDragProxy - onComplete for uuid=" 
                        + ( parent != null ? parent.getUUID() : "no-parent" ) );

                // TODO

            }
        });
        
    }

    private boolean isValid( final RuleViolations violations ) {
        return !violations.violations(RuleViolation.Type.ERROR).iterator().hasNext();
    }
    
}
