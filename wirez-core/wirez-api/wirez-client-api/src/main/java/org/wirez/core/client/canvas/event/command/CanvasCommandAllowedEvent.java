package org.wirez.core.client.canvas.event.command;

import org.wirez.core.client.canvas.CanvasHandler;
import org.wirez.core.client.canvas.command.CanvasViolation;
import org.wirez.core.command.Command;
import org.wirez.core.command.CommandResult;

public final class CanvasCommandAllowedEvent<H extends CanvasHandler> extends AbstractCanvasCommandEvent<H> {
    
    public CanvasCommandAllowedEvent( final H canvasHandler, 
                                      final Command<H, CanvasViolation> command, 
                                      final CommandResult<CanvasViolation> violation) {
        super( canvasHandler, command, violation );
    }
    
}
