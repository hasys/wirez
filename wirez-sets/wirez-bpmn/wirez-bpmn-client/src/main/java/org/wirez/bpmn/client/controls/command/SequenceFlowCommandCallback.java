
package org.wirez.bpmn.client.controls.command;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.google.gwt.logging.client.LogConfiguration;
import org.wirez.bpmn.api.SequenceFlow;
import org.wirez.bpmn.api.factory.BPMNDefinitionFactory;
import org.wirez.core.api.DefinitionManager;
import org.wirez.core.api.command.CommandResult;
import org.wirez.core.api.command.batch.BatchCommandResult;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.command.factory.GraphCommandFactory;
import org.wirez.core.api.graph.content.view.ViewConnector;
import org.wirez.core.api.util.UUID;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.Canvas;
import org.wirez.core.client.canvas.command.CanvasCommandManager;
import org.wirez.core.client.canvas.command.CanvasViolation;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.canvas.controls.toolbox.command.NewConnectorCommand;
import org.wirez.core.client.canvas.controls.toolbox.command.Context;
import org.wirez.core.client.service.ClientFactoryServices;
import org.wirez.core.client.service.ClientRuntimeError;
import org.wirez.core.client.service.ServiceCallback;
import org.wirez.core.client.session.command.Session;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.shape.impl.AbstractShape;
import org.wirez.core.client.util.ShapeUtils;
import org.wirez.core.client.util.WirezClientLogger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class SequenceFlowCommandCallback implements NewConnectorCommand.Callback {

    private static Logger LOGGER = Logger.getLogger(SequenceFlowCommandCallback.class.getName());

    DefinitionManager definitionManager;
    CanvasCommandFactory commandFactory;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    GraphCommandFactory graphCommandFactory;
    ClientFactoryServices clientFactoryServices;
    ShapeManager shapeManager;
    BPMNDefinitionFactory bpmnDefinitionFactory;

    private Element source;
    private Edge<ViewConnector<SequenceFlow>, Node> edge;

    @Inject
    public SequenceFlowCommandCallback(final DefinitionManager definitionManager,
                                       final CanvasCommandFactory commandFactory,
                                       final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager,
                                       final GraphCommandFactory graphCommandFactory,
                                       final ClientFactoryServices clientFactoryServices,
                                       final ShapeManager shapeManager,
                                       final BPMNDefinitionFactory bpmnDefinitionFactory) {
        this.definitionManager = definitionManager;
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.graphCommandFactory = graphCommandFactory;
        this.clientFactoryServices = clientFactoryServices;
        this.shapeManager = shapeManager;
        this.bpmnDefinitionFactory = bpmnDefinitionFactory;
    }

    @Override
    public void init(final Element element) {
        final SequenceFlow sequenceFlow = bpmnDefinitionFactory.buildSequenceFlow();
        final String seqFlowId = definitionManager.getDefinitionAdapter( sequenceFlow.getClass() ).getId( sequenceFlow );
        clientFactoryServices.newElement(UUID.uuid(), seqFlowId, new ServiceCallback<Element>() {
            @Override
            public void onSuccess(final Element item) {
                SequenceFlowCommandCallback.this.source = element;
                SequenceFlowCommandCallback.this.edge = (Edge<ViewConnector<SequenceFlow>, Node>) item;
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                log(Level.SEVERE, WirezClientLogger.getErrorMessage(error));
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
        
        final Object seqFlow = edge.getContent().getDefinition();
        final DefinitionAdapter<Object> adapter = definitionManager.getDefinitionAdapter( seqFlow.getClass() );
        final String seqFlowId = adapter.getId( seqFlow );
        final ShapeFactory factory = shapeManager.getFactory( seqFlowId );

        final BatchCommandResult<CanvasViolation> results =
                canvasCommandManager
                .batch( commandFactory.ADD_EDGE( (Node) source, edge, factory) )
                .batch( commandFactory.SET_SOURCE_NODE( (Node) source, edge, magnetIndexes[0]) )
                .batch( commandFactory.SET_TARGET_NODE( target, edge, magnetIndexes[1]) )
                .executeBatch( wch );
        
        // TODO: Check results.

        log(Level.FINE, "Connection performed from [" + source.getUUID() + "] to [" + target.getUUID() + "]");
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
 