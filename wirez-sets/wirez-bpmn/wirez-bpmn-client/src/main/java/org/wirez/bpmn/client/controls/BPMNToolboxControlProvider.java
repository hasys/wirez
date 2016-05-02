package org.wirez.bpmn.client.controls;

import org.wirez.bpmn.api.BPMNDefinition;
import org.wirez.bpmn.api.ParallelGateway;
import org.wirez.bpmn.api.StartNoneEvent;
import org.wirez.bpmn.api.Task;
import org.wirez.bpmn.client.controls.command.NewSeqFlowNodeCommand;
import org.wirez.bpmn.client.controls.command.NewSequenceFlowCommand;
import org.wirez.core.client.canvas.controls.toolbox.AbstractToolboxControlProvider;
import org.wirez.core.client.canvas.controls.toolbox.command.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class BPMNToolboxControlProvider extends AbstractToolboxControlProvider<BPMNDefinition> {

    NewSequenceFlowCommand newSequenceFlowCommand;
    NewSeqFlowNodeCommand newSeqFlowNodeCommand;
    
    @Inject
    public BPMNToolboxControlProvider(final NameToolboxCommand nameToolboxCommand,
                                      final RemoveToolboxCommand removeToolboxCommand,
                                      final MoveUpCommand moveUpCommand,
                                      final MoveDownCommand moveDownCommand,
                                      final NewSequenceFlowCommand newSequenceFlowCommand,
                                      final NewSeqFlowNodeCommand newSeqFlowNodeCommand) {
        super(nameToolboxCommand, removeToolboxCommand, moveUpCommand, moveDownCommand );
        this.newSequenceFlowCommand = newSequenceFlowCommand;
        this.newSeqFlowNodeCommand = newSeqFlowNodeCommand;
    }

    @Override
    public List<ToolboxCommand> getCommands(final BPMNDefinition item) {

        final List<ToolboxCommand> commands =  super.getCommands(item);
        
        // If the definition support adding in/out edges, add the control on the toolbox for this item.
        if ( supportsAddConnectionCommand( item ) ) {
            commands.add( newSequenceFlowCommand );
            commands.add( newSeqFlowNodeCommand );
        }
        
        return commands;
        
    }

    protected boolean supportsAddConnectionCommand(final BPMNDefinition definition) {
        return ( definition instanceof StartNoneEvent
                || definition instanceof Task
                || definition instanceof ParallelGateway);
    }
    
}
