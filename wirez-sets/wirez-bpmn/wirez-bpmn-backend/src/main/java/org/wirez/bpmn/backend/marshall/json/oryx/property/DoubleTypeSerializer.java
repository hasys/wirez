package org.wirez.bpmn.backend.marshall.json.oryx.property;

import org.wirez.core.definition.property.PropertyType;
import org.wirez.core.definition.property.type.DoubleType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DoubleTypeSerializer implements Bpmn2OryxPropertySerializer<Double> {
    
    @Override
    public boolean accepts( PropertyType type ) {
        return DoubleType.name.equals( type.getName() );
    }

    @Override
    public Double parse( String value ) {
        return Double.parseDouble( value );
    }

    @Override
    public String serialize( Double value ) {
        return Double.toString( value );
    }
}
