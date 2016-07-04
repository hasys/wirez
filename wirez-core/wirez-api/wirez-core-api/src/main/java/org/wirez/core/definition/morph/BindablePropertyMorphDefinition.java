package org.wirez.core.definition.morph;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class BindablePropertyMorphDefinition extends BindableMorphDefinition
        implements PropertyMorphDefinition {

    protected abstract Map<Class<?>, Collection<MorphProperty>> getBindableMorphProperties();

    @Override
    public Iterable<MorphProperty> getMorphProperties( String definitionId ) {
        final Class<?> type = getSourceType( definitionId );
        return getMorphPropertiesForType( type );
    }

    public Iterable<MorphProperty> getMorphPropertiesForType( Class<?> type ) {
        if ( null != type ) {
            return getBindableMorphProperties().get( type );
        }
        return null;
    }

    @Override
    protected Map<Class<?>, Collection<Class<?>>> getDomainMorphs() {

        if ( null != getBindableMorphProperties() && !getBindableMorphProperties().isEmpty() ) {

            final Map<Class<?>, Collection<Class<?>>> result = new LinkedHashMap<>();

            for ( final Map.Entry<Class<?>, Collection<MorphProperty>> entry : getBindableMorphProperties().entrySet() ) {

                final Class<?> sourceType = entry.getKey();

                final Collection<MorphProperty> pms = entry.getValue();

                for ( final MorphProperty pm : pms ) {

                    final BindableMorphProperty<?, ?> morphProperty = (BindableMorphProperty<?, ?>) pm;

                    Collection<Class<?>> targets = result.get( sourceType );

                    if ( null == targets ) {

                        targets = new LinkedList<>();

                        result.put( sourceType, targets );
                    }

                    targets.addAll( morphProperty.getMorphTargetClasses().values() );

                }

            }

            return result;

        }

        return null;

    }

}
