package org.wirez.bpmn.client.controls.command;

import org.wirez.bpmn.api.BPMNDefinitionSet;
import org.wirez.bpmn.api.SequenceFlow;
import org.wirez.core.api.definition.adapter.binding.BindableAdapterUtils;
import org.wirez.core.api.lookup.util.CommonLookups;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.controls.toolbox.command.node.NewNodeCommand;
import org.wirez.core.client.components.drag.CanvasDragProxy;
import org.wirez.core.client.components.glyph.GlyphMiniPalette;
import org.wirez.core.client.components.glyph.GlyphTooltip;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class NewSeqFlowNodeCommand extends NewNodeCommand{

    private static final String DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId( BPMNDefinitionSet.class );
    private static final String EDGE_ID = BindableAdapterUtils.getDefinitionId( SequenceFlow.class );

    @Inject
    public NewSeqFlowNodeCommand(final CommonLookups commonLookups, 
                                 final ShapeManager shapeManager, 
                                 final GlyphTooltip glyphTooltip, 
                                 final GlyphMiniPalette glyphMiniPalette, 
                                 final CanvasDragProxy canvasDragProxy) {
        super(commonLookups, shapeManager, glyphTooltip, glyphMiniPalette, canvasDragProxy);
    }

    @Override
    protected String getDefinitionSetIdentifier() {
        return DEF_SET_ID;
    }

    @Override
    protected String getEdgeIdentifier() {
        return EDGE_ID;
    }
    
}
