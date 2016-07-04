package org.wirez.core.definition.adapter;

import org.wirez.core.api.DefinitionManager;
import org.wirez.core.api.FactoryManager;
import org.wirez.core.definition.morph.MorphDefinition;
import org.wirez.core.definition.morph.MorphProperty;
import org.wirez.core.definition.morph.PropertyMorphDefinition;
import org.wirez.core.definition.util.DefinitionUtils;

import java.util.*;

public abstract class AbstractMorphAdapter<S> implements MorphAdapter<S>  {

    protected DefinitionUtils definitionUtils;
    protected FactoryManager factoryManager;

    protected final List<MorphDefinition> morphDefinitions = new LinkedList<>();

    public AbstractMorphAdapter( final DefinitionUtils definitionUtils,
                                 final FactoryManager factoryManager ) {
        this.definitionUtils = definitionUtils;
        this.factoryManager = factoryManager;
    }

    protected abstract <T> T doMerge( S source, MorphDefinition definition, T result );

    @Override
    @SuppressWarnings("unchecked")
    public <T> Iterable<MorphDefinition> getMorphDefinitions( final T definition ) {

        if ( null != definition && hasMorphDefinitions() ) {

            final String[] ids = getDefinitionIds( definition );
            final String definitionId = ids[0];
            final String baseId = ids[1];

            return getMorphDefinitions( definitionId, baseId );

        }

        return null;
    }

    protected Iterable<MorphDefinition> getMorphDefinitions( final String id,
                                                             final String baseId ) {

        if ( null != id ) {

            final List<MorphDefinition> result = new LinkedList<>();

            for ( MorphDefinition morphDefinition : morphDefinitions ) {

                if ( morphDefinition.accepts( id ) ||
                        ( null != baseId && morphDefinition.accepts( baseId ) ) ) {

                    result.add( morphDefinition );

                }

            }

            return result;

        }

        return null;
    }

    @Override
    public <T> Iterable<MorphProperty> getMorphProperties( final T definition ) {

        if ( null != definition && hasMorphDefinitions() ) {

            final String[] ids = getDefinitionIds( definition );
            final String definitionId = ids[0];
            final String baseId = ids[1];

            return getMorphProperties( definitionId, baseId );

        }

        return null;
    }

    protected Iterable<MorphProperty> getMorphProperties( final String definitionId,
                                                          final String baseId ) {

        if ( null != definitionId ) {

            final List<MorphProperty> result = new LinkedList<>();

            for ( MorphDefinition morphDefinition : morphDefinitions ) {

                final boolean acceptsDefinition = morphDefinition.accepts( definitionId ) ||
                        ( null != baseId && morphDefinition.accepts( baseId ) );

                if ( acceptsDefinition && ( morphDefinition instanceof PropertyMorphDefinition) ) {

                    final PropertyMorphDefinition propertyMorphDefinition = (PropertyMorphDefinition) morphDefinition;

                    final Iterable<MorphProperty> morphProperties = propertyMorphDefinition.getMorphProperties( definitionId );

                    addAll( result, morphProperties );

                    final Iterable<MorphProperty> baseMorphProperties = null != baseId ?
                            propertyMorphDefinition.getMorphProperties( definitionId ) : null;

                    if ( null != baseMorphProperties ) {

                        addAll( result, baseMorphProperties );

                    }


                }

            }

            return result;

        }

        return null;
    }

    @Override
    public <T> Iterable<String> getTargets( final T definition, final MorphDefinition morphDefinition ) {

        final String[] ids = definitionUtils.getDefinitionIds( definition );
        final String definitionId = ids[0];
        final String baseId = ids[1];
        return getTargets( definition.getClass(), definitionId, baseId );
    }

    protected Iterable<String> getTargets(final Class<?> type,
                                          final String definitionId,
                                          final String baseId ) {

        if ( null != definitionId ) {

            final List<String> result = new LinkedList<>();

            for ( MorphDefinition morphDefinition : morphDefinitions ) {

                final boolean acceptsDefinition = morphDefinition.accepts( definitionId ) ||
                        ( null != baseId && morphDefinition.accepts( baseId ) );

                if ( acceptsDefinition  ) {

                    final Iterable<String> t1 = morphDefinition.getTargets( definitionId );

                    addAll( result, t1 );

                    if ( null != baseId ) {

                        final Iterable<String> t2 = morphDefinition.getTargets( baseId );

                        addAll( result, t2 );

                    }

                }

            }

            return result;

        }

        return null;
    }

    @Override
    public <T> T morph( final S source,
                        final MorphDefinition morphDefinition,
                        final String targetId ) {

        if ( null == source ) {

            throw new IllegalArgumentException( "Cannot morph from unspecified source." );

        }

        if ( null == morphDefinition ) {

            throw new IllegalArgumentException( "Cannot morph from unspecified Morph Definition." );

        }

        if ( null == targetId ) {

            throw new IllegalArgumentException( "Cannot morph to unspecified target." );

        }

        final T target = factoryManager.newDomainObject( targetId );

        if ( null == target ) {

            throw new RuntimeException( "Morph failed. Cannot build a Definition instance for [" + targetId + "]" );

        }

        return doMerge( source, morphDefinition, target );

    }

    protected <T> String[] getDefinitionIds( final T definition ) {
        return definitionUtils.getDefinitionIds( definition );

    }

    protected DefinitionManager getDefinitionManager() {
        return definitionUtils.getDefinitionManager();
    }

    protected <T> void addAll(Collection<T> source, Iterable<T> values ) {
        if ( null != values && values.iterator().hasNext() ) {
            for ( final T value : values ) {
                source.add( value );
            }
        }
    }

    protected boolean contains( final Iterable<String> iterable, String value ){
        if ( null != iterable && iterable.iterator().hasNext() ) {
            for ( final String v : iterable ) {
                if ( value.equals( v ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean hasMorphDefinitions() {
        return !morphDefinitions.isEmpty();
    }


}
