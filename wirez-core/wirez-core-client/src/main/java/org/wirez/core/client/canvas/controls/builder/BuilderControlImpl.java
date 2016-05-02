package org.wirez.core.client.canvas.controls.builder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.LogConfiguration;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.view.View;
import org.wirez.core.api.graph.util.GraphBoundsIndexer;
import org.wirez.core.api.graph.util.GraphUtils;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.CanvasHandler;
import org.wirez.core.client.canvas.command.CanvasCommandManager;
import org.wirez.core.client.canvas.command.CanvasViolation;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.wirez.core.client.canvas.controls.event.BuildCanvasShapeEvent;
import org.wirez.core.client.canvas.event.CanvasProcessingCompletedEvent;
import org.wirez.core.client.canvas.event.CanvasProcessingStartedEvent;
import org.wirez.core.client.service.ClientFactoryServices;
import org.wirez.core.client.service.ClientRuntimeError;
import org.wirez.core.client.service.ServiceCallback;
import org.wirez.core.client.session.command.Session;
import org.wirez.core.client.shape.factory.ShapeFactory;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class BuilderControlImpl extends AbstractCanvasHandlerControl 
        implements BuilderControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(BuilderControlImpl.class.getName());
    
    ClientDefinitionManager clientDefinitionManager;
    ClientFactoryServices clientFactoryServices;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    CanvasCommandFactory canvasCommandFactory;
    Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent;
    Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent;
    GraphBoundsIndexer graphBoundsIndexer;


    @Inject
    public BuilderControlImpl(final ClientDefinitionManager clientDefinitionManager,
                              final ClientFactoryServices clientFactoryServices,
                              final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                              final CanvasCommandFactory canvasCommandFactory,
                              final GraphBoundsIndexer graphBoundsIndexer,
                              final Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent,
                              final Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.canvasCommandManager = canvasCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.graphBoundsIndexer = graphBoundsIndexer;
        this.canvasProcessingStartedEvent = canvasProcessingStartedEvent;
        this.canvasProcessingCompletedEvent = canvasProcessingCompletedEvent;
    }

    protected void onBuildCanvasShape(@Observes BuildCanvasShapeEvent buildCanvasShapeEvent) {
        checkNotNull("buildCanvasShapeEvent", buildCanvasShapeEvent);
        
        if ( isEnabled() ) {
            final CanvasHandler context = buildCanvasShapeEvent.getCanvasHandler();

            if ( null != context && context.equals( canvasHandler ) ) {
                final ShapeFactory factory = buildCanvasShapeEvent.getShapeFactory();
                final Object definition = buildCanvasShapeEvent.getDefinition();
                final double x = buildCanvasShapeEvent.getX();
                final double y = buildCanvasShapeEvent.getY();
                final double cx = canvasHandler.getCanvas().getAbsoluteX();
                final double cy = canvasHandler.getCanvas().getAbsoluteY();
                build(definition, factory, x - cx, y - cy);
                canvasHandler.getCanvas().draw();
            }
        }
        
    }

    public void build( final Object definition, 
                       final ShapeFactory factory,
                       final double _x, 
                       final double _y ) {

        if ( !isEnabled() ) {
            return;
        }

        // Notify processing starts.
        fireProcessingStarted();
        
        final DefinitionAdapter definitionAdapter = clientDefinitionManager.getDefinitionAdapter( definition. getClass() );
        final String defId = definitionAdapter.getId( definition );
        clientFactoryServices.newElement(org.wirez.core.api.util.UUID.uuid(), defId, new ServiceCallback<Element>() {
            @Override
            public void onSuccess(final Element element) {
                double x = _x > -1 ? _x : 100d;
                double y = _y > -1 ? _y : 100d;

                Node<View<?>, Edge> parent = null;
                if ( _x > -1 && _y > -1) {

                    parent = graphBoundsIndexer
                            .forGraph( canvasHandler.getDiagram().getGraph() )
                            .get(_x, _y);

                    /*final LienzoLayer lienzoLayer = (LienzoLayer) canvasHandler.getCanvas().getLayer();
                    final String viewUUID = LienzoLayerUtils.getUUID_At( lienzoLayer, _x, _y );
                    GWT.log(" Parent is " + viewUUID);*/
                    
                    if ( null != parent) {
                        final Double[] parentCoords = GraphUtils.getPosition(parent.getContent());
                        x = _x - parentCoords[0];
                        y = _y - parentCoords[1];
                    }
                }

                org.wirez.core.api.command.Command<AbstractCanvasHandler, CanvasViolation> command = null;
                if ( element instanceof Node) {

                    if ( null != parent ) {
                        command = canvasCommandFactory.ADD_CHILD_NODE( parent, (Node) element, factory );
                    } else {
                        command = canvasCommandFactory.ADD_NODE((Node) element, factory);
                    }

                } else if ( element instanceof Edge && null != parent ) {
                    command = canvasCommandFactory.ADD_EDGE( parent, (Edge) element, factory );
                } else {
                    throw new RuntimeException("Unrecognized element type for " + element);
                }

                // Execute both add element and move commands in batch, so undo will be done in batch as well.
                org.wirez.core.api.command.Command<AbstractCanvasHandler, CanvasViolation> moveCanvasElementCommand =
                        canvasCommandFactory.UPDATE_POSITION(element, x ,y);

                // TODO: Use no rules.
                // canvasHandler.getCommandManager().execute( emptyRuleManager, command, moveCanvasElementCommand);
                canvasCommandManager
                        .batch( command)
                        .batch( moveCanvasElementCommand )
                        .executeBatch( canvasHandler );

                // Notify processing ends.
                fireProcessingCompleted();
            }

            @Override
            public void onError(final ClientRuntimeError error) {
               showError( error );
            }
        });

    }

    protected void fireProcessingStarted() {
        canvasProcessingStartedEvent.fire( new CanvasProcessingStartedEvent( canvasHandler ) );
    }

    protected void fireProcessingCompleted() {
        canvasProcessingCompletedEvent.fire( new CanvasProcessingCompletedEvent( canvasHandler ) );
    }
    
    private void showError( final ClientRuntimeError error ) {
        fireProcessingCompleted();
        final String message = error.getThrowable() != null ?
                error.getThrowable().getMessage() : error.getMessage();
        log( Level.SEVERE, message);
    }

    private void log(final Level level, final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level, message);
        }
    }
    
}
