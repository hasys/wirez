package org.wirez.core.api.lookup.definition;

import org.wirez.core.api.DefinitionManager;
import org.wirez.core.api.FactoryManager;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.api.definition.adapter.DefinitionSetAdapter;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.util.GraphUtils;
import org.wirez.core.api.lookup.criteria.AbstractCriteriaLookupManager;
import org.wirez.core.api.lookup.criteria.Criteria;
import org.wirez.core.api.registry.Map;
import org.wirez.core.api.registry.definition.DefinitionRegistry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
@Criteria
public class DefinitionLookupManagerImpl 
        extends AbstractCriteriaLookupManager<String, DefinitionRepresentation, DefinitionLookupRequest>
        implements DefinitionLookupManager {
    
    DefinitionManager definitionManager;
    FactoryManager factoryManager;
    GraphUtils graphUtils;
    DefinitionRegistry<Object> registry;

    protected DefinitionLookupManagerImpl() {
    }
    
    @Inject
    public DefinitionLookupManagerImpl(DefinitionManager definitionManager,
                                       FactoryManager factoryManager,
                                       GraphUtils graphUtils,
                                       @Map DefinitionRegistry<Object> registry) {
        this.definitionManager = definitionManager;
        this.factoryManager = factoryManager;
        this.graphUtils = graphUtils;
        this.registry = registry;
    }

    @Override
    protected List<String> getItems( DefinitionLookupRequest request ) {
        final String defSetId = request.getDefinitionSetId();
        final Object defSet = definitionManager.getDefinitionSet( defSetId );

        if ( null != defSet ) {
            final DefinitionSetAdapter<Object> definitionSetAdapter = definitionManager.getDefinitionSetAdapter(defSet.getClass());
            final Set<String> defs = definitionSetAdapter.getDefinitions(defSet);
            return new LinkedList<>( defs );
        }
        
        return null;
    }

    @Override
    protected DefinitionRepresentation buildResult( String defId ) {
        final Object def = getDomainObject( defId );
        return buildRepresentation( defId, def );
    }

    @Override
    protected boolean matches(String key, String value, String defId) {
        final Object def = getDomainObject( defId );
        final DefinitionAdapter<Object> definitionAdapter = definitionManager.getDefinitionAdapter( def.getClass() );
        
        switch ( key ) {

            case "id":
                return defId.equals( value );
            
            case "type":
                final Class<? extends Element> elemType = definitionAdapter.getGraphElement( def );
                boolean isNode = graphUtils.isNode( elemType );
                return "node".equals( value ) && isNode;
            
            case "labels":

                final Set<String> labelSet = toSet( value );
                if ( null != labelSet ) {
                    Set<String> defLabels = definitionAdapter.getLabels( def );
                    return isIntersect( labelSet, defLabels );
                }
                
                return true;
        }
        
        throw new UnsupportedOperationException( "Cannot filter definitions by key [" + key + "]" );
    }

    
    private Object getDomainObject( String id ) {
        
        Object definition = registry.get( id );
        
        if ( null == definition ) {

            definition = factoryManager.newDomainObject( id );
            registry.add( definition );
        }
        
        return definition;
    }
    
    private DefinitionRepresentation buildRepresentation( String id, Object def ) {
        final DefinitionAdapter<Object> definitionAdapter = definitionManager.getDefinitionAdapter( def.getClass() );
        final Set<String> labels = definitionAdapter.getLabels( def );
        final Class<? extends Element> graphElement = definitionAdapter.getGraphElement( def );
        boolean isNode = graphUtils.isNode( graphElement );
        return new DefinitionRepresentationImpl( id, isNode, labels );
    }

   
}
