package org.wirez.bpmn.client.controls.command;

import org.wirez.bpmn.api.SequenceFlow;
import org.wirez.core.api.definition.adapter.binding.BindableAdapterUtils;
import org.wirez.core.api.graph.util.GraphBoundsIndexer;
import org.wirez.core.api.lookup.util.CommonLookups;
import org.wirez.core.client.canvas.controls.toolbox.command.connector.NewConnectorCommand;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class NewSequenceFlowCommand extends NewConnectorCommand {
    
    private static final String EDGE_ID = BindableAdapterUtils.getDefinitionId( SequenceFlow.class );

    @Inject
    public NewSequenceFlowCommand(final GraphBoundsIndexer graphBoundsIndexer, 
                                  final CommonLookups commonLookups, 
                                  final Callback callback, 
                                  final View view) {
        super(graphBoundsIndexer, commonLookups, callback, view);
    }

    @Override
    protected String getEdgeIdentifier() {
        return EDGE_ID;
    }
    
}
