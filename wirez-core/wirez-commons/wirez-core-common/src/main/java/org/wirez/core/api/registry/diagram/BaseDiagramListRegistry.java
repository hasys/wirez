package org.wirez.core.api.registry.diagram;

import org.wirez.core.api.diagram.Diagram;
import org.wirez.core.api.registry.BaseListRegistry;

import java.util.List;

public abstract class BaseDiagramListRegistry<D extends Diagram> extends BaseListRegistry<D> implements DiagramRegistry<D> {

    public BaseDiagramListRegistry(final List<D> items) {
        super(items);
    }

    @Override
    protected String getItemId(final D item) {
        return item.getUUID();
    }

    @Override
    public void update(final D diagram) {

        final int index = items.indexOf(diagram);

        if ( index != -1 ) {
            
            final boolean isRemoved = items.remove(diagram);
            
            if (isRemoved) {
                items.add(index, diagram);
            }
            
        } else {
            
            throw new RuntimeException("Diagram with uuid [" + diagram.getUUID() + "] cannot be updated as it does not exist.");
            
        }
        
    }
    
}