package org.wirez.bpmn.client.controls;

import org.wirez.bpmn.api.BPMNDefinition;
import org.wirez.bpmn.api.ParallelGateway;
import org.wirez.bpmn.api.StartNoneEvent;
import org.wirez.bpmn.api.Task;
import org.wirez.bpmn.client.controls.command.SequenceFlowCommandCallback;
import org.wirez.core.client.canvas.controls.toolbox.AbstractToolboxControlProvider;
import org.wirez.core.client.canvas.controls.toolbox.command.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.List;

@Dependent
public class BPMNToolboxControlProvider extends AbstractToolboxControlProvider<BPMNDefinition> {

    SequenceFlowCommandCallback sequenceFlowCommandCallback;
    
    @Inject
    public BPMNToolboxControlProvider(final NameToolboxCommand nameToolboxCommand, 
                                      final RemoveToolboxCommand removeToolboxCommand, 
                                      final MoveUpCommand moveUpCommand, 
                                      final MoveDownCommand moveDownCommand, 
                                      final AddConnectionCommand addConnectionCommand,
                                      final AddNodeCommand addNodeCommand,
                                      final SequenceFlowCommandCallback sequenceFlowCommandCallback) {
        super(nameToolboxCommand, removeToolboxCommand, moveUpCommand, moveDownCommand, addConnectionCommand, addNodeCommand);
        this.sequenceFlowCommandCallback = sequenceFlowCommandCallback;
    }

    @PostConstruct
    public void init() {
        addConnectionCommand.setCallback(sequenceFlowCommandCallback);
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
