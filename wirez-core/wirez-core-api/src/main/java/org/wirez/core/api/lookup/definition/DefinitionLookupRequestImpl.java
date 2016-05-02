package org.wirez.core.api.lookup.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.wirez.core.api.lookup.AbstractLookupRequest;
import org.wirez.core.api.lookup.AbstractLookupRequestBuilder;

import java.util.Set;

@Portable
public final class DefinitionLookupRequestImpl extends AbstractLookupRequest implements DefinitionLookupRequest {
    
    private final String definitionSetId;
    
    public DefinitionLookupRequestImpl(@MapsTo("criteria") String criteria,
                                       @MapsTo("page") int page,
                                       @MapsTo("pageSize") int pageSize,
                                       @MapsTo("definitionSetId") String definitionSetId) {
        super( criteria, page, pageSize );
        this.definitionSetId = definitionSetId;
    }

    @Override
    public String getDefinitionSetId() {
        return definitionSetId;
    }

    @NonPortable
    public static class Builder extends AbstractLookupRequestBuilder<Builder> {

        enum Type {
            NODE, EDGE;
        }
        
        private String defSetId;
        private final StringBuilder criteria = new StringBuilder();
        
        public Builder definitionSetId( final String defSetId ) {
            this.defSetId = defSetId;
            return this;
        }

        public Builder id( final String id) {
            criteria.append( "id=" ).append( id ).append( ";" );
            return this;
        }


        public Builder type( final Type type ) {
            criteria.append( "type=" ).append( type.name().toLowerCase() ).append( ";" );
            return this;
        }

        public Builder labels( final Set<String> labels  ) {
            criteria.append( "labels=" ).append( fromSet( labels ) ).append( ";" );
            return this;
        }

        public DefinitionLookupRequest build( ) {
            return new DefinitionLookupRequestImpl( criteria.toString(), page, pageSize, defSetId );
        }

    }
}
