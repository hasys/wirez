package org.wirez.core.api.adapter.shared;

import org.wirez.core.api.adapter.PropertySetAdapter;
import org.wirez.core.api.definition.property.DefaultPropertySet;
import org.wirez.core.api.definition.property.Property;

import javax.enterprise.context.ApplicationScoped;
import java.util.Set;

@ApplicationScoped
public class DefaultPropertySetAdapter implements PropertySetAdapter<DefaultPropertySet> {

    @Override
    public boolean accepts(final Class pojoClass) {
        return pojoClass.equals(DefaultPropertySet.class);
    }


    @Override
    public Set<Property> getProperties(DefaultPropertySet pojo) {
        Set<Property> result = null;
        if ( null != pojo ) {
            result = pojo.getProperties();
        }
        
        return result;
    }

    @Override
    public int getPriority() {
        return 1;
    }
    
}