package org.wirez.core.client.canvas.command.impl;

import org.wirez.core.api.command.CommandResult;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.view.View;
import org.wirez.core.client.ClientDefinitionManager;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.wirez.core.client.canvas.command.CanvasViolation;
import org.wirez.core.client.shape.factory.ShapeFactory;

public abstract class DeleteCanvasElementCommand<E extends Element> extends AbstractCanvasGraphCommand {

    protected E candidate;
    protected Node parent;
    protected ShapeFactory factory;

    public DeleteCanvasElementCommand(final E candidate) {
        this.candidate = candidate;
        this.parent = getParent();
    }

    public DeleteCanvasElementCommand(final E candidate, final Node parent) {
        this.candidate = candidate;
        this.parent = parent;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        this.factory = getShapeFactory(context);
        doDeregister(context);
        return buildResult();
    }
    
    protected void doDeregister(final AbstractCanvasHandler context) {
        context.deregister(candidate);
    }
    
    protected Node getParent() {
        return null;
    };
    
    protected ShapeFactory getShapeFactory(AbstractCanvasHandler context) {
        final ClientDefinitionManager manager = context.getClientDefinitionManager();
        final Object def = ( (Element<View<?>>) candidate ).getContent().getDefinition();
        final DefinitionAdapter<Object> adapter = manager.getDefinitionAdapter( def.getClass() );
        final String id = adapter.getId( def );
        return context.getShapeManager().getFactory( id );
    }

}
