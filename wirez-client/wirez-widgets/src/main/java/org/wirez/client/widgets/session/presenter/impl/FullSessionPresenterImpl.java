package org.wirez.client.widgets.session.presenter.impl;

import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.wirez.client.widgets.session.toolbar.AbstractToolbar;
import org.wirez.client.widgets.session.toolbar.command.*;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.canvas.event.processing.CanvasProcessingCompletedEvent;
import org.wirez.core.client.canvas.event.processing.CanvasProcessingStartedEvent;
import org.wirez.core.client.service.ClientDiagramServices;
import org.wirez.core.client.service.ClientFactoryServices;
import org.wirez.core.client.session.impl.DefaultCanvasFullSession;
import org.wirez.core.client.session.impl.DefaultCanvasSessionManager;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Dependent
public class FullSessionPresenterImpl extends AbstractFullSessionPresenter<DefaultCanvasFullSession> implements DefaultFullSessionPresenter {

    @Inject
    public FullSessionPresenterImpl(final DefaultCanvasSessionManager canvasSessionManager,
                                    final ClientDefinitionManager clientDefinitionManager,
                                    final ClientFactoryServices clientFactoryServices,
                                    final AbstractToolbar<DefaultCanvasFullSession> toolbar,
                                    final ClearSelectionCommand clearSelectionCommand,
                                    final ClearCommand clearCommand,
                                    final DeleteSelectionCommand deleteSelectionCommand,
                                    final SaveCommand saveCommand,
                                    final UndoCommand undoCommand,
                                    final VisitGraphCommand visitGraphCommand,
                                    final CanvasCommandFactory commandFactory,
                                    final ClientDiagramServices clientDiagramServices,
                                    final ErrorPopupPresenter errorPopupPresenter,
                                    final Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent,
                                    final Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent,
                                    final View view) {
        super( canvasSessionManager, clientDefinitionManager, clientFactoryServices,
                commandFactory, clientDiagramServices, toolbar, clearSelectionCommand, clearCommand, 
                deleteSelectionCommand, saveCommand, undoCommand, visitGraphCommand, errorPopupPresenter,
                canvasProcessingStartedEvent, canvasProcessingCompletedEvent, view );
    }
    
}
