package org.wirez.core.client.canvas.controls.toolbox;

import org.wirez.core.client.canvas.controls.toolbox.command.*;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractToolboxControlProvider<T> implements ToolboxControlProvider<T> {

    protected NameToolboxCommand nameToolboxCommand;
    protected RemoveToolboxCommand removeToolboxCommand;
    protected MoveUpCommand moveUpCommand;
    protected MoveDownCommand moveDownCommand;

    @Inject
    public AbstractToolboxControlProvider(final NameToolboxCommand nameToolboxCommand, 
                                          final RemoveToolboxCommand removeToolboxCommand, 
                                          final MoveUpCommand moveUpCommand, 
                                          final MoveDownCommand moveDownCommand) {
        this.nameToolboxCommand = nameToolboxCommand;
        this.removeToolboxCommand = removeToolboxCommand;
        this.moveUpCommand = moveUpCommand;
        this.moveDownCommand = moveDownCommand;
    }
    
    protected List<ToolboxCommand> defaultCommands() {
        final List<ToolboxCommand> commands = new LinkedList<>();
        commands.add( nameToolboxCommand );
        commands.add( removeToolboxCommand );
        commands.add( moveUpCommand );
        commands.add( moveDownCommand );
        return commands;
    }

    @Override
    public List<ToolboxCommand> getCommands(final T item) {
        return defaultCommands();
    }
    
}
