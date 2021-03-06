package org.wirez.client.widgets.session.toolbar.command;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.wirez.client.widgets.session.toolbar.ToolbarCommandCallback;
import org.wirez.client.widgets.session.toolbar.event.DisableToolbarCommandEvent;
import org.wirez.client.widgets.session.toolbar.event.EnableToolbarCommandEvent;
import org.wirez.core.command.CommandResult;
import org.wirez.core.client.canvas.command.CanvasViolation;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.session.impl.DefaultCanvasFullSession;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class ClearCommand extends AbstractToolbarCommand<DefaultCanvasFullSession> {

    CanvasCommandFactory canvasCommandFactory;

    @Inject
    public ClearCommand(final Event<EnableToolbarCommandEvent> enableToolbarCommandEvent,
                        final Event<DisableToolbarCommandEvent> disableToolbarCommandEvent,
                        final CanvasCommandFactory canvasCommandFactory) {
        super( enableToolbarCommandEvent, disableToolbarCommandEvent );
        this.canvasCommandFactory = canvasCommandFactory;
    }

    @Override
    public IconType getIcon() {
        return IconType.BOLT;
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getTooltip() {
        return "Clear the diagram";
    }

    @Override
    public <T> void execute(final ToolbarCommandCallback<T> callback) {

        executeWithConfirm( () -> {

            // Execute the clear canvas command.
            final CommandResult<CanvasViolation> result = session.getCanvasCommandManager().execute( session.getCanvasHandler(),
                    canvasCommandFactory.CLEAR_CANVAS() );

            if ( null != callback ) {
                callback.onSuccess((T) result);
            }

        } );
        
    }
    
}
