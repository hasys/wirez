package org.wirez.client.widgets.session.presenter.impl;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mvp.Command;
import org.wirez.client.widgets.session.presenter.FullSessionPresenter;
import org.wirez.client.widgets.session.toolbar.AbstractToolbar;
import org.wirez.client.widgets.session.toolbar.ToolbarCommand;
import org.wirez.client.widgets.session.toolbar.ToolbarCommandCallback;
import org.wirez.client.widgets.session.toolbar.command.*;
import org.wirez.core.diagram.Diagram;
import org.wirez.core.diagram.Settings;
import org.wirez.core.diagram.SettingsImpl;
import org.wirez.core.graph.Graph;
import org.wirez.core.util.UUID;
import org.wirez.core.util.WirezLogger;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.canvas.AbstractCanvas;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.canvas.event.processing.CanvasProcessingCompletedEvent;
import org.wirez.core.client.canvas.event.processing.CanvasProcessingStartedEvent;
import org.wirez.core.client.service.ClientDiagramServices;
import org.wirez.core.client.service.ClientFactoryServices;
import org.wirez.core.client.service.ClientRuntimeError;
import org.wirez.core.client.service.ServiceCallback;
import org.wirez.core.client.session.CanvasFullSession;
import org.wirez.core.client.session.impl.DefaultCanvasSessionManager;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractFullSessionPresenter<S extends CanvasFullSession<AbstractCanvas, AbstractCanvasHandler>> 
        extends AbstractReadOnlySessionPresenter<S>
        implements FullSessionPresenter<AbstractCanvas, AbstractCanvasHandler, S> {

    protected ClientDefinitionManager clientDefinitionManager;
    protected ClientFactoryServices clientFactoryServices;
    protected CanvasCommandFactory commandFactory;
    protected ClearCommand clearCommand;
    protected DeleteSelectionCommand deleteSelectionCommand;
    protected SaveCommand saveCommand;
    protected UndoCommand undoCommand;

    private static Logger LOGGER = Logger.getLogger(FullSessionPresenter.class.getName());
    
    @Inject
    public AbstractFullSessionPresenter(final DefaultCanvasSessionManager canvasSessionManager,
                                        final ClientDefinitionManager clientDefinitionManager,
                                        final ClientFactoryServices clientFactoryServices,
                                        final CanvasCommandFactory commandFactory,
                                        final ClientDiagramServices clientDiagramServices,
                                        final AbstractToolbar<S> toolbar,
                                        final ClearSelectionCommand clearSelectionCommand,
                                        final ClearCommand clearCommand,
                                        final DeleteSelectionCommand deleteSelectionCommand,
                                        final SaveCommand saveCommand,
                                        final UndoCommand undoCommand,
                                        final VisitGraphCommand visitGraphCommand,
                                        final ErrorPopupPresenter errorPopupPresenter,
                                        final Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent,
                                        final Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent,
                                        final View view) {
        super(canvasSessionManager, clientDiagramServices, toolbar, clearSelectionCommand, visitGraphCommand,
                errorPopupPresenter, canvasProcessingStartedEvent, canvasProcessingCompletedEvent, view);
        this.commandFactory = commandFactory;
        this.clientDefinitionManager = clientDefinitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.clearCommand = clearCommand;
        this.deleteSelectionCommand = deleteSelectionCommand;
        this.saveCommand = saveCommand;
        this.undoCommand = undoCommand;
    }

    @Override
    public void doInitialize(final S session, 
                           final int width, 
                           final int height) {
        super.doInitialize(session, width, height);

        // Enable canvas controls.
        final AbstractCanvasHandler canvasHandler = getCanvasHandler();
        enableControl( session.getConnectionAcceptorControl(), canvasHandler );
        enableControl( session.getContainmentAcceptorControl(), canvasHandler );
        // TODO: enableControl( session.getDockingAcceptorControl(), canvasHandler );
        enableControl( session.getDragControl(), canvasHandler );
        enableControl( session.getToolboxControl(), canvasHandler );
        enableControl( session.getBuilderControl(), canvasHandler );
        
    }
    
    @Override
    protected void setToolbarCommands() {
        super.setToolbarCommands();

        // Toolbar commands for canvas controls.
        super.addToolbarCommand( (ToolbarCommand<S>) clearCommand );
        super.addToolbarCommand( (ToolbarCommand<S>) deleteSelectionCommand );
        super.addToolbarCommand( (ToolbarCommand<S>) saveCommand );
        super.addToolbarCommand( (ToolbarCommand<S>) undoCommand );
    }

    @Override
    public void newDiagram(final String uuid, 
                           final String title, 
                           final String definitionSetId, 
                           final String shapeSetId, 
                           final Command callback) {

        clientFactoryServices.newGraph(UUID.uuid(), definitionSetId, new ServiceCallback<Graph>() {
            @Override
            public void onSuccess(final Graph graph) {
                final Settings diagramSettings = new SettingsImpl( title, definitionSetId, shapeSetId );
                Diagram<Graph, Settings> diagram = clientFactoryServices.newDiagram( UUID.uuid(), graph, diagramSettings );
                open(diagram, callback);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                showError(error);
                callback.execute();
            }
        });
        
    }

    @Override
    public void save(final Command callback) {

        saveCommand.execute( new ToolbarCommandCallback<Diagram>() {
            
            @Override
            public void onSuccess(final Diagram result) {
                Window.alert("Diagram saved successfully [UUID=" + result.getUUID() + "]");
                callback.execute();
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                showError(error);
                callback.execute();
            }
        });
        
        
    }

    @Override
    public void clear() {

        clearCommand.execute();
        
    }

    @Override
    public void undo() {

        undoCommand.execute();
        
    }

    @Override
    public void deleteSelected() {

        deleteSelectionCommand.execute();

    }

    @Override
    protected void disposeSession() {
        super.disposeSession();
      
        if ( null != clearCommand ) {
            this.clearCommand.destroy();
        }

        if ( null != deleteSelectionCommand ) {
            this.deleteSelectionCommand.destroy();
        }

        if ( null != saveCommand ) {
            this.saveCommand.destroy();
        }

        if ( null != undoCommand ) {
            this.undoCommand.destroy();
        }
        
        this.clientDefinitionManager = null;
        this.clientFactoryServices = null;
        this.commandFactory = null;
        this.clearCommand = null;
        this.deleteSelectionCommand = null;
        this.saveCommand = null;
        this.undoCommand = null;
        
    }

    @Override
    protected void pauseSession() {
    }

    /*
        PUBLIC UTILITY METHODS FOR CODING & TESTING
     */

    public void visitGraph() {

        visitGraphCommand.execute();
        
    }

    public void resumeGraph() {
        WirezLogger.resume( getCanvasHandler().getDiagram().getGraph());
    }

    public void logGraph() {
        WirezLogger.log( getCanvasHandler().getDiagram().getGraph());
    }

    @Override
    protected void showError(String message) {
        log( Level.SEVERE, message);
        super.showError(message);
    }

    private void log(final Level level, final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level, message);
        }
    }
    
}
