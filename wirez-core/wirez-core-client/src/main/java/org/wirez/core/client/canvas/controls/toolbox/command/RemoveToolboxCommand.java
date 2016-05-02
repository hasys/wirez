package org.wirez.core.client.canvas.controls.toolbox.command;

import com.ait.lienzo.client.core.shape.Shape;
import com.google.gwt.user.client.Window;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.command.CanvasCommandManager;
import org.wirez.core.client.canvas.command.factory.CanvasCommandFactory;
import org.wirez.core.client.session.command.Session;
import org.wirez.core.client.util.SVGUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class RemoveToolboxCommand implements ToolboxCommand {

    CanvasCommandFactory commandFactory;
    CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager;
    
    private final Shape<?> icon;

    @Inject
    public RemoveToolboxCommand(final CanvasCommandFactory commandFactory,
                                final @Session CanvasCommandManager<AbstractCanvasHandler> canvasCommandManager) {
        this.commandFactory = commandFactory;
        this.canvasCommandManager = canvasCommandManager;
        this.icon = SVGUtils.createSVGIcon(SVGUtils.getRemoveIcon());;
    }

    @Override
    public Shape<?> getIcon() {
        return icon;
    }

    @Override
    public String getTitle() {
        return "Remove";
    }

    @Override
    public void execute(final Context context, 
                        final Element element) {
        
        // TODO: Remove use of hardcoded confirm box here.
        if ( Window.confirm("Are you sure?") ) {
            canvasCommandManager.execute( context.getCanvasHandler(), commandFactory.DELETE_NODE((Node) element) );
        }
        
    }
    
}
