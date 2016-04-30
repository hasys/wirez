package org.wirez.core.client.canvas.controls.toolbox.command.connector;

import com.ait.lienzo.client.core.shape.Shape;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.view.View;
import org.wirez.core.api.graph.processing.index.bounds.GraphBoundsIndexer;
import org.wirez.core.api.util.UUID;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.controls.builder.EdgeBuilderControl;
import org.wirez.core.client.canvas.controls.builder.request.EdgeBuildRequest;
import org.wirez.core.client.canvas.controls.builder.request.EdgeBuildRequestImpl;
import org.wirez.core.client.canvas.controls.toolbox.command.AbstractToolboxCommand;
import org.wirez.core.client.canvas.controls.toolbox.command.Context;
import org.wirez.core.client.canvas.util.CanvasHighlight;
import org.wirez.core.client.components.drag.ConnectorDragProxyFactory;
import org.wirez.core.client.components.drag.DragProxyCallback;
import org.wirez.core.client.service.ClientFactoryServices;
import org.wirez.core.client.service.ClientRuntimeError;
import org.wirez.core.client.service.ServiceCallback;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.util.SVGUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class NewConnectorCommand extends AbstractToolboxCommand {

    private static Logger LOGGER = Logger.getLogger(NewConnectorCommand.class.getName());

    private final com.ait.lienzo.client.core.shape.Shape<?> icon;

    ClientDefinitionManager clientDefinitionManager;
    ClientFactoryServices clientFactoryServices;
    ShapeManager shapeManager;
    GraphBoundsIndexer graphBoundsIndexer;
    ConnectorDragProxyFactory<AbstractCanvasHandler> connectorDragProxyFactory;
    EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl;
    
    private CanvasHighlight canvasHighlight;

    public NewConnectorCommand(final ClientDefinitionManager clientDefinitionManager,
                               final ClientFactoryServices clientFactoryServices,
                               final ShapeManager shapeManager,
                               final GraphBoundsIndexer graphBoundsIndexer,
                               final ConnectorDragProxyFactory<AbstractCanvasHandler> connectorDragProxyFactory,
                               final EdgeBuilderControl<AbstractCanvasHandler> edgeBuilderControl) {
        this.clientDefinitionManager = clientDefinitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.shapeManager = shapeManager;
        this.graphBoundsIndexer = graphBoundsIndexer;
        this.connectorDragProxyFactory = connectorDragProxyFactory;
        this.edgeBuilderControl = edgeBuilderControl;
        this.icon = SVGUtils.createSVGIcon(SVGUtils.getConnectorIcon());
    }

    protected abstract String getEdgeIdentifier();
    
    @Override
    public Shape<?> getIcon() {
        return icon;
    }

    @Override
    public String getTitle() {
        return "Creates a new connector";
    }

    @Override
    public void click(final Context context, 
                        final Element element) {

        final Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) element;
        final AbstractCanvasHandler canvasHandler = context.getCanvasHandler();
        final double x = context.getX();
        final double y = context.getY();
        
        clientFactoryServices.newElement( UUID.uuid(), getEdgeIdentifier(), new ServiceCallback<Element>() {
            
            @Override
            public void onSuccess(final Element edgeItem) {
                
                final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) edgeItem;
                final ShapeFactory<? ,?, ?> edgeFactory = shapeManager.getFactory( getEdgeIdentifier() );
                
                final ConnectorDragProxyFactory.Item proxyItem = new ConnectorDragProxyFactory.Item() {
                    @Override
                    public Edge<View<?>, Node> getEdge() {
                        return edge;
                    }

                    @Override
                    public Node<View<?>, Edge> getSourceNode() {
                        return sourceNode;
                    }

                    @Override
                    public ShapeFactory getShapeFactory() {
                        return edgeFactory;
                    }
                };

                edgeBuilderControl.enable( canvasHandler );

                canvasHighlight = new CanvasHighlight( canvasHandler );
                graphBoundsIndexer.build( canvasHandler.getDiagram().getGraph() );

                connectorDragProxyFactory
                        .proxyFor( canvasHandler )
                        .newInstance( proxyItem, (int) x, (int) y, new DragProxyCallback() {
                            
                            @Override
                            public void onStart(final int x, 
                                                final int y) {
                                
                            }

                            @Override
                            public void onMove(final int x,
                                               final int y) {

                                // TODO: Two expensive calls to bounds indexer, this one and the one inside connectorDragProxyFactory.
                                
                                final Node targetNode = graphBoundsIndexer.getAt( x, y );
                                
                                if ( null != targetNode ) {

                                    final EdgeBuildRequest request = new EdgeBuildRequestImpl( x, y, edge, sourceNode, targetNode );
                                    final boolean allows = edgeBuilderControl.allows( request );

                                    if ( allows ) {
                                        
                                        canvasHighlight.highLight( targetNode );
                                        
                                    }
                                    
                                } else {
                                    
                                    canvasHighlight.unhighLight();
                                    
                                }
                                
                                
                            }

                            @Override
                            public void onComplete(final int x,
                                                   final int y) {

                                final Node targetNode = graphBoundsIndexer.getAt( x, y );

                                if ( null != targetNode ) {

                                    final EdgeBuildRequest request = new EdgeBuildRequestImpl( x, y, edge, sourceNode, targetNode );
                                    edgeBuilderControl.build( request );


                                }

                                edgeBuilderControl.disable();

                                canvasHighlight.unhighLight();

                            }
                        });

                
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                LOGGER.log(Level.SEVERE, error.toString() );
            }
            
        });
        
        
        
    }

}
