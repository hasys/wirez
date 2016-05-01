package org.wirez.bpmn.client.controls;

import org.wirez.bpmn.api.*;
import org.wirez.core.api.definition.adapter.binding.BindableAdapterUtils;
import org.wirez.core.client.canvas.controls.toolbox.AbstractToolboxControlProvider;
import org.wirez.core.client.canvas.controls.toolbox.command.*;
import org.wirez.core.client.canvas.controls.toolbox.command.connector.NewConnectorCommand;
import org.wirez.core.client.canvas.controls.toolbox.command.node.NewNodeCommand;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class BPMNToolboxControlProvider extends AbstractToolboxControlProvider<BPMNDefinition> {
    
    @Inject
    public BPMNToolboxControlProvider(final NameToolboxCommand nameToolboxCommand, 
                                      final RemoveToolboxCommand removeToolboxCommand, 
                                      final MoveUpCommand moveUpCommand, 
                                      final MoveDownCommand moveDownCommand, 
                                      final NewConnectorCommand addConnectionCommand,
                                      final NewNodeCommand addNodeCommand) {
        super(nameToolboxCommand, removeToolboxCommand, moveUpCommand, moveDownCommand, addConnectionCommand, addNodeCommand);
    }

    @PostConstruct
    public void init() {
        final String seqFlowId = BindableAdapterUtils.getDefinitionId( SequenceFlow.class );
        addConnectionCommand.setEdgeIdentifier( seqFlowId );
        addNodeCommand.setEdgeIdentifier( seqFlowId );
    }
    
    @Override
    public List<ToolboxCommand> getCommands(final BPMNDefinition item) {

        final List<ToolboxCommand> commands =  super.getCommands(item);
        
        // If the definition support adding in/out edges, add the control on the toolbox for this item.
        if ( supportsAddConnectionCommand( item ) ) {
            commands.add( addConnectionCommand );
            commands.add( addNodeCommand );
        }
        
        return commands;
        
    }

    protected boolean supportsAddConnectionCommand(final BPMNDefinition definition) {
        return ( definition instanceof StartNoneEvent
                || definition instanceof Task
                || definition instanceof ParallelGateway);
    }
    
}
