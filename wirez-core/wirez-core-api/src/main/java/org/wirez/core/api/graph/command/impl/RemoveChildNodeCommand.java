package org.wirez.core.api.graph.command.impl;

import org.uberfire.commons.validation.PortablePreconditions;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.command.GraphCommandFactory;
import org.wirez.core.api.graph.impl.DefaultGraph;

public class RemoveChildNodeCommand extends AbstractCompositeCommand {

    private DefaultGraph target;
    private Node parent;
    private Node candidate;
    
    public RemoveChildNodeCommand(final GraphCommandFactory commandFactory,
                                  final DefaultGraph target,
                                  final Node parent,
                                  final Node candidate) {
        super(commandFactory);
        this.target = PortablePreconditions.checkNotNull( "target",
                target );
        this.parent = PortablePreconditions.checkNotNull( "parent",
                parent );
        this.candidate = PortablePreconditions.checkNotNull( "candidate",
                candidate );
        initCommands();
    }
    
    private void initCommands() {
        this.addCommand( commandFactory.removeParentCommand( parent, candidate) )
            .addCommand( commandFactory.deleteNodeCommand( target, candidate ) );
    }

    @Override
    public String toString() {
        return "RemoveChildNodeCommand [parent=" + parent.getUUID() + ", candidate=" + candidate.getUUID() + "]";
    }
}