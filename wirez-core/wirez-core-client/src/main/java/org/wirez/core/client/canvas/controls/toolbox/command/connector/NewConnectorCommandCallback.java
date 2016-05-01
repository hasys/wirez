
package org.wirez.core.client.canvas.controls.toolbox.command.connector;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.logging.client.LogConfiguration;
import org.wirez.core.api.command.CommandResult;
import org.wirez.core.api.command.CommandUtils;
import org.wirez.core.api.command.batch.BatchCommandResult;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.command.factory.GraphCommandFactory;
import org.wirez.core.api.graph.content.view.ViewConnector;
import org.wirez.core.api.util.UUID;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.Canvas;
import org.wirez.core.client.canvas.command.CanvasCommandManager;
import org.wirez.core.client.canvas.command.CanvasViolation;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.canvas.controls.toolbox.command.Context;
import org.wirez.core.client.service.ClientFactoryServices;
import org.wirez.core.client.service.ClientRuntimeError;
import org.wirez.core.client.service.ServiceCallback;
import org.wirez.core.client.session.command.Session;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.shape.impl.AbstractShape;
import org.wirez.core.client.util.ShapeUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Magnets.
@Dependent
public class NewConnectorCommandCallback implements NewConnectorCommand.Callback {

    private static Logger LOGGER = Logger.getLogger(NewConnectorCommandCallback.class.getName());

    protected ClientDefinitionManager clientDefinitionManager;
    protected CanvasCommandFactory commandFactory;
    protected CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    protected GraphCommandFactory graphCommandFactory;
    protected ClientFactoryServices clientFactoryServices;
    protected ShapeManager shapeManager;

    protected Element source;
    protected Edge<ViewConnector<?>, Node> edge;

    @Inject
    public NewConnectorCommandCallback(final ClientDefinitionManager clientDefinitionManager,
                                       final CanvasCommandFactory commandFactory,
                                       final @Session  CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                       final GraphCommandFactory graphCommandFactory,
                                       final ClientFactoryServices clientFactoryServices,
                                       final ShapeManager shapeManager) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.graphCommandFactory = graphCommandFactory;
        this.clientFactoryServices = clientFactoryServices;
        this.shapeManager = shapeManager;
    }

    @Override
    public void init(final Element element, final String edgeId ) {
        
        if ( null == edgeId ) {
            throw new NullPointerException(" New connector command requires an edge identifier.");
        }
        
        clientFactoryServices.newElement(UUID.uuid(), edgeId, new ServiceCallback<Element>() {
            @Override
            public void onSuccess(final Element item) {
                NewConnectorCommandCallback.this.source = element;
                NewConnectorCommandCallback.this.edge = (Edge<ViewConnector<?>, Node>) item;
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                handleError( error.toString() );
            }
        });
        
    }

    @Override
    public boolean isAllowed(final Context context, final Node target) {

        final AbstractCanvasHandler<?, ?> wch = context.getCanvasHandler();
        
        final CommandResult<CanvasViolation> cr1 = canvasCommandManager.allow( wch, commandFactory.SET_SOURCE_NODE( (Node) source, edge, 0) ); 
        final boolean allowsSourceConn = isAllowed( cr1 );

        final CommandResult<CanvasViolation> cr2 = canvasCommandManager.allow( wch, commandFactory.SET_TARGET_NODE( target, edge, 0) );
        final boolean allowsTargetConn = isAllowed( cr2 );
                
        final boolean isAllowed = allowsSourceConn & allowsTargetConn;
        
        log(Level.FINE, "Connection allowed from [" + source.getUUID() + "] to [" + target.getUUID() + "] = [" 
                + ( isAllowed ? "true" : "false" ) + "]");
        
        return isAllowed;
        
    }

    @Override
    public void accept(final Context context, final Node target) {
        
        final AbstractCanvasHandler<?, ?> wch = context.getCanvasHandler();
        final Canvas canvas = context.getCanvasHandler().getCanvas();
        
        final AbstractShape sourceShape = (AbstractShape) canvas.getShape(source.getUUID());
        final AbstractShape targetShape = (AbstractShape) canvas.getShape(target.getUUID());
        final int[] magnetIndexes = ShapeUtils.getDefaultMagnetsIndex( (WiresShape) sourceShape.getShapeView(),
                (WiresShape) targetShape.getShapeView());
        
        final Object edgeDef = edge.getContent().getDefinition();
        final DefinitionAdapter<Object> adapter = clientDefinitionManager.getDefinitionAdapter( edgeDef.getClass() );
        final String edgeDefId = adapter.getId( edgeDef );
        final ShapeFactory factory = shapeManager.getFactory( edgeDefId );

        final BatchCommandResult<CanvasViolation> results =
                canvasCommandManager
                .batch( commandFactory.ADD_EDGE( (Node) source, edge, factory) )
                .batch( commandFactory.SET_SOURCE_NODE( (Node) source, edge, magnetIndexes[0]) )
                .batch( commandFactory.SET_TARGET_NODE( target, edge, magnetIndexes[1]) )
                .executeBatch( wch );
        
        if (CommandUtils.isError( results ) ) {
            handleError( results.toString() );
        }

        log(Level.FINE, "Connection performed from [" + source.getUUID() + "] to [" + target.getUUID() + "]");
    }
    
    private void handleError( final String message ) {
        log( Level.SEVERE, message );
    }

    private void log(final Level level, final String message) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log(level, message);
        }
    }
    
    private boolean isAllowed(CommandResult<CanvasViolation> result ) {
        return !CommandResult.Type.ERROR.equals( result.getType() );
    }
    
}
 