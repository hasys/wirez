package org.wirez.client.widgets.session.presenter.impl;

import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.wirez.client.widgets.session.toolbar.AbstractToolbar;
import org.wirez.client.widgets.session.toolbar.command.ClearSelectionCommand;
import org.wirez.client.widgets.session.toolbar.command.VisitGraphCommand;
import org.wirez.core.client.canvas.event.processing.CanvasProcessingCompletedEvent;
import org.wirez.core.client.canvas.event.processing.CanvasProcessingStartedEvent;
import org.wirez.core.client.service.ClientDiagramServices;
import org.wirez.core.client.session.impl.DefaultCanvasReadOnlySession;
import org.wirez.core.client.session.impl.DefaultCanvasSessionManager;

import javax.enterprise.event.Event;

 // TODO: @Dependent - As there is no bean injection for AbstractToolbar<DefaultCanvasReadOnlySession> yet, this class
// is abstract and not eligible by the bean manager for now.
public abstract class ReadOnlySessionPresenterImpl extends AbstractReadOnlySessionPresenter<DefaultCanvasReadOnlySession>
 implements DefaultReadOnlySessionPresenter {

    public ReadOnlySessionPresenterImpl(final DefaultCanvasSessionManager canvasSessionManager,
                                        final ClientDiagramServices clientDiagramServices,
                                        final AbstractToolbar<DefaultCanvasReadOnlySession> toolbar,
                                        final ClearSelectionCommand clearSelectionCommand,
                                        final VisitGraphCommand visitGraphCommand,
                                        final ErrorPopupPresenter errorPopupPresenter,
                                        final Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent,
                                        final Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent,
                                        final View view) {
        super( canvasSessionManager, clientDiagramServices, toolbar, clearSelectionCommand, visitGraphCommand,
                errorPopupPresenter, canvasProcessingStartedEvent, canvasProcessingCompletedEvent, view );
    }
    
}
