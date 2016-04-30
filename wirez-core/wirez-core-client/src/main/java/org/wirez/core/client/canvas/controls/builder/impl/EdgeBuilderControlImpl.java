package org.wirez.core.client.canvas.controls.builder.impl;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.wirez.core.api.command.CommandResult;
import org.wirez.core.api.command.CommandUtils;
import org.wirez.core.api.command.batch.BatchCommandResult;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.view.View;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.Canvas;
import org.wirez.core.client.canvas.command.CanvasCommandManager;
import org.wirez.core.client.canvas.command.CanvasViolation;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.wirez.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.wirez.core.client.canvas.controls.builder.request.EdgeBuildRequest;
import org.wirez.core.client.canvas.event.CanvasProcessingCompletedEvent;
import org.wirez.core.client.canvas.event.CanvasProcessingStartedEvent;
import org.wirez.core.client.session.command.Session;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.shape.impl.AbstractShape;
import org.wirez.core.client.util.ShapeUtils;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class EdgeBuilderControlImpl extends AbstractCanvasHandlerControl implements EdgeBuilderControl<AbstractCanvasHandler> {

    private static Logger LOGGER = Logger.getLogger(EdgeBuilderControlImpl.class.getName());

    ClientDefinitionManager clientDefinitionManager;
    ShapeManager shapeManager;
    CanvasCommandFactory commandFactory;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent;
    Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent;

    @Inject
    public EdgeBuilderControlImpl(final ClientDefinitionManager clientDefinitionManager, 
                                  final ShapeManager shapeManager, 
                                  final CanvasCommandFactory commandFactory, 
                                  final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager, 
                                  final Event<CanvasProcessingStartedEvent> canvasProcessingStartedEvent, 
                                  final Event<CanvasProcessingCompletedEvent> canvasProcessingCompletedEvent) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.shapeManager = shapeManager;
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.canvasProcessingStartedEvent = canvasProcessingStartedEvent;
        this.canvasProcessingCompletedEvent = canvasProcessingCompletedEvent;
    }

    @Override
    public boolean allows(final EdgeBuildRequest request) {

        final double x = request.getX();
        final double y = request.getY();
        final Edge<View<?>, Node> edge = request.getEdge();
        final AbstractCanvasHandler<?, ?> wch = canvasHandler;
        final Node<View<?>, Edge> inNode = request.getInNode();
        final Node<View<?>, Edge> outNode = request.getOutNode();

        fireProcessingStarted();
        
        boolean allowsSourceConn = true;
        if ( null != inNode ) {
            final CommandResult<CanvasViolation> cr1 = canvasCommandManager.allow( wch, commandFactory.SET_SOURCE_NODE((Node<? extends View<?>, Edge>) inNode, edge, 0) );
            allowsSourceConn = isAllowed( cr1 );
        }

        boolean allowsTargetConn = true;
        if ( null != outNode ) {
            final CommandResult<CanvasViolation> cr2 = canvasCommandManager.allow( wch, commandFactory.SET_TARGET_NODE((Node<? extends View<?>, Edge>) outNode, edge, 0) );
            allowsTargetConn = isAllowed( cr2 );
        }

        fireProcessingCompleted();
        
        return allowsSourceConn & allowsTargetConn;
        
    }

    @Override
    @SuppressWarnings("unchecked")
    public void build(final EdgeBuildRequest request) {

        final double x = request.getX();
        final double y = request.getY();
        final Edge<View<?>, Node> edge = request.getEdge();
        final AbstractCanvasHandler<?, ?> wch = canvasHandler;
        final Node<View<?>, Edge> inNode = request.getInNode();
        final Node<View<?>, Edge> outNode = request.getOutNode();
        final Canvas canvas = canvasHandler.getCanvas();

        if ( null == inNode ) {
            
            throw new RuntimeException(" An edge must be into the outgoing edges list from a node." );
            
        }

        fireProcessingStarted();
        
        final AbstractShape sourceShape = (AbstractShape) canvas.getShape(inNode.getUUID());
        final AbstractShape targetShape = outNode != null ? (AbstractShape) canvas.getShape(outNode.getUUID()) : null;

        int[] magnetIndexes = new int[] { 0, 0 };
        if ( outNode != null ) {
            
            magnetIndexes = ShapeUtils.getDefaultMagnetsIndex( (WiresShape) sourceShape.getShapeView(),
                    (WiresShape) targetShape.getShapeView());
            
        }

        final Object edgeDef = edge.getContent().getDefinition();
        final DefinitionAdapter<Object> adapter = clientDefinitionManager.getDefinitionAdapter( edgeDef.getClass() );
        final String edgeDefId = adapter.getId( edgeDef );
        final ShapeFactory factory = shapeManager.getFactory( edgeDefId );
        
        canvasCommandManager
                .batch( commandFactory.ADD_EDGE( inNode, edge, factory) )
                .batch( commandFactory.SET_SOURCE_NODE((Node<? extends View<?>, Edge>) inNode, edge, magnetIndexes[0]) );
        
        if ( null != outNode ) {
            
            canvasCommandManager.batch( commandFactory.SET_TARGET_NODE((Node<? extends View<?>, Edge>) outNode, edge, magnetIndexes[1]) );
            
        }

        final BatchCommandResult<CanvasViolation> results = canvasCommandManager.executeBatch( wch );

        if (CommandUtils.isError( results ) ) {
            
            LOGGER.log( Level.SEVERE, results.toString() );
            
        }

        fireProcessingCompleted();
        
    }

    protected void fireProcessingStarted() {
        canvasProcessingStartedEvent.fire( new CanvasProcessingStartedEvent( canvasHandler ) );
    }

    protected void fireProcessingCompleted() {
        canvasProcessingCompletedEvent.fire( new CanvasProcessingCompletedEvent( canvasHandler ) );
    }
    
    private boolean isAllowed(CommandResult<CanvasViolation> result ) {
        return !CommandResult.Type.ERROR.equals( result.getType() );
    }
    
}
