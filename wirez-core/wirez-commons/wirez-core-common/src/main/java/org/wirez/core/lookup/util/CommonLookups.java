package org.wirez.core.lookup.util;

import org.wirez.core.api.DefinitionManager;
import org.wirez.core.graph.Edge;
import org.wirez.core.graph.Node;
import org.wirez.core.graph.Graph;
import org.wirez.core.graph.content.definition.Definition;
import org.wirez.core.graph.util.GraphUtils;
import org.wirez.core.lookup.LookupManager;
import org.wirez.core.lookup.criteria.Criteria;
import org.wirez.core.lookup.definition.DefinitionLookupManager;
import org.wirez.core.lookup.definition.DefinitionLookupRequest;
import org.wirez.core.lookup.definition.DefinitionLookupRequestImpl;
import org.wirez.core.lookup.definition.DefinitionRepresentation;
import org.wirez.core.lookup.rule.RuleLookupManager;
import org.wirez.core.lookup.rule.RuleLookupRequest;
import org.wirez.core.lookup.rule.RuleLookupRequestImpl;
import org.wirez.core.rule.CardinalityRule;
import org.wirez.core.rule.ConnectionRule;
import org.wirez.core.rule.EdgeCardinalityRule;
import org.wirez.core.rule.Rule;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * An utils class that provides common used look-ups and other logic for querying the domain model and the rules model, 
 * that is used along the application.
 *
 * // TODO: Cache.
 */
@ApplicationScoped
public class CommonLookups {

    DefinitionManager definitionManager;
    DefinitionLookupManager definitionLookupManager;
    RuleLookupManager ruleLookupManager;
    GraphUtils graphUtils;

    protected CommonLookups() {
    }

    @Inject
    public CommonLookups(final DefinitionManager definitionManager,
                         final GraphUtils graphUtils,
                         final @Criteria DefinitionLookupManager definitionLookupManager,
                         final @Criteria RuleLookupManager ruleLookupManager) {
        this.definitionManager = definitionManager;
        this.graphUtils = graphUtils;
        this.definitionLookupManager = definitionLookupManager;
        this.ruleLookupManager = ruleLookupManager;
    }

    /**
     * Returns the allowed edge definition identifiers that can be added as outgoing edges for the given source node.
     */
    public <T> Set<String> getAllowedConnectors( final String defSetId,
                                                 final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                 final int page,
                                                 final int pageSize) {

        final Set<String> result = new LinkedHashSet<>();

        if ( null != defSetId && null != sourceNode ) {

            final T definition = sourceNode.getContent().getDefinition();
            final Set<String> connectionAllowedEdges = getConnectionRulesAllowedEdges( defSetId, definition, page ,pageSize );

            if ( null != connectionAllowedEdges && !connectionAllowedEdges.isEmpty() ) {


                for ( final String allowedEdgeId : connectionAllowedEdges ) {
                    final int edgeCount = countOutgoingEdges( sourceNode, allowedEdgeId );
                    final boolean isOutEdgeCardinalityRuleAllowed
                            = isOutEdgeCardinalityRuleAllowed( defSetId,
                            sourceNode.getContent().getDefinition(),
                            allowedEdgeId, edgeCount);

                    if ( isOutEdgeCardinalityRuleAllowed ) {
                        result.add( allowedEdgeId );
                    }
                }

            }

        }

        return result;
    }

    /**
     * Returns the allowed definition identifiers that can be used as target node for the given source node and 
     * the given edge (connector) identifier.
     */
    public <T> Set<String> getAllowedDefinitions(final String defSetId,
                                                 final Graph<?, ? extends Node> graph,
                                                 final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                                 final String edgeId,
                                                 final int page,
                                                 final int pageSize) {
        
        final Set<String> result = new LinkedHashSet<>();
        
        if ( null != defSetId && null != graph && null != sourceNode && null != edgeId ) {

            final T definition = sourceNode.getContent().getDefinition();
            
            final Set<String> allowedTargetRoles = getConnectionRulesAllowedTargets( defSetId, definition, edgeId, page, pageSize );
            
            if  ( null != allowedTargetRoles && !allowedTargetRoles.isEmpty() ) {
                
                final Set<String> allowedTargetRoles2 = new LinkedHashSet<>();
                
                for ( final String s : allowedTargetRoles ) {
                    
                    final boolean isCardinalityAllowed = isCardinalitySatisfied( defSetId, graph, definition );
                    
                    if ( isCardinalityAllowed ) {
                        allowedTargetRoles2.add( s );
                    }
                    
                }
                
                if ( !allowedTargetRoles2.isEmpty() ) {

                    final Set<String> allowedTargetRoles3 = new LinkedHashSet<>();

                    for ( final String s : allowedTargetRoles2 ) {

                        final HashSet<String> hs = new HashSet<String>(1) {{
                            add( s );
                        }};
                        
                        final boolean isInEdgeCardinalityRuleAllowed
                                = isInEdgeCardinalityRuleAllowed( defSetId,
                                hs, edgeId);
                        
                        if ( isInEdgeCardinalityRuleAllowed ) {
                            allowedTargetRoles3.add( s );
                        }

                    }
                    
                    return getDefinitions( defSetId, allowedTargetRoles3 );
                    
                }
                
            }

        }

        return result;
    }

    /**
     * Returns all the Definition Set's definition identifiers that contains the given labels. 
     */
    public Set<String> getDefinitions( final String defSetId, final Set<String> labels ) {
        
        if ( null != labels && !labels.isEmpty() ) {

            final DefinitionLookupRequest request =
                    new DefinitionLookupRequestImpl.Builder()
                            .definitionSetId(defSetId)
                            .labels( labels )
                            .page( 0 )
                            .pageSize( 100 )
                            .build();

            final LookupManager.LookupResponse<DefinitionRepresentation> response = definitionLookupManager.lookup( request );
            final List<DefinitionRepresentation> definitionRepresentations = response.getResults();
            
            if ( null != definitionRepresentations && !definitionRepresentations.isEmpty() ) {
                
                final Set<String> result = new LinkedHashSet<>();
                
                for ( final DefinitionRepresentation definitionRepresentation : definitionRepresentations ) {
                    final String id = definitionRepresentation.getDefinitionId();
                    result.add( id );
                }
                
                return result;
            }
            
        }
        
        return new HashSet<>(0);
    }

    /**
     * Returns the allowed edge identifiers that satisfy connection rules for the given 
     * source definition ( domain model object, not a graph node ). 
     */
    public <T> Set<String> getConnectionRulesAllowedEdges(final String defSetId,
                                                          final T sourceDefinition,
                                                          final int page,
                                                          final int pageSize) {

        final List<Rule> rules = lookupConnectionRules( defSetId, sourceDefinition, null, page, pageSize );

        if ( null != rules && !rules.isEmpty() ) {

            final Set<String> result = new LinkedHashSet<>();
            for ( final Rule rule : rules ) {
                final ConnectionRule cr = (ConnectionRule) rule;
                final String edgeId = cr.getId();
                result.add( edgeId );
            }

            return result;
        }

        return null;
    }

    /**
     * Returns the allowed definitions identifiers that satisfy connection rules for a given source 
     * definition ( domain model object, not a node ).and the given edge (connector) identifier.  
     */
    public <T> Set<String> getConnectionRulesAllowedTargets(final String defSetId,
                                                            final T sourceDefinition,
                                                            final String edgeId,
                                                            final int page,
                                                            final int pageSize) {

        final List<Rule> rules = lookupConnectionRules( defSetId, sourceDefinition, edgeId, page, pageSize );

        if ( null != rules && !rules.isEmpty() ) {

            final Set<String> result = new LinkedHashSet<>();
            for ( final Rule rule : rules ) {
                final ConnectionRule cr = (ConnectionRule) rule;

                final Set<ConnectionRule.PermittedConnection> connections = cr.getPermittedConnections();
                if ( null != connections && !connections.isEmpty() ) {

                    for (final ConnectionRule.PermittedConnection connection : connections ) {

                        result.add( connection.getEndRole() );

                    }

                }

            }

            return result;
        }

        return null;
    }

    /**
     * Check if cardinality rules satisfy that a given definition ( domain model object, not a node ).can be added into a graph
     * TODO: Delegate to  the @Model RulesManager.
     */
    public <T> boolean isCardinalitySatisfied( final String defSetId,
                                               final Graph<?, ? extends Node> target,
                                               final T sourceDefinition ) {

        final Set<String> defLabels = graphUtils.getDefinitionLabels( sourceDefinition );

        final RuleLookupRequest request =
                new RuleLookupRequestImpl.Builder()
                        .definitionSetId(defSetId)
                        .type(RuleLookupRequestImpl.Builder.RuleType.CARDINALITY)
                        .roleIn( defLabels )
                        .page( 0 )
                        .pageSize( 100 )
                        .build();

        final LookupManager.LookupResponse<Rule> response = ruleLookupManager.lookup( request );

        final List<Rule> rules = response.getResults();
        
        if ( null != rules && !rules.isEmpty() ) {

            final int count = graphUtils.countDefinitions( target, sourceDefinition );

            for ( final Rule rule : rules ) {
                final CardinalityRule cr = (CardinalityRule) rule;
                final int max = cr.getMaxOccurrences();

                if ( max == 0 || max >= count ) {
                    return false;
                }

            }

        }

        return true;

    }

    private <T> List<Rule> lookupConnectionRules(final String defSetId,
                                                 final T sourceDefinition,
                                                 final String edgeId,
                                                 final int page,
                                                 final int pageSize) {

        if ( null != defSetId ) {
            
            final Set<String> defLabels = graphUtils.getDefinitionLabels( sourceDefinition );

            final RuleLookupRequestImpl.Builder builder = new RuleLookupRequestImpl.Builder();

            builder.definitionSetId(defSetId)
                    .type(RuleLookupRequestImpl.Builder.RuleType.CONNECTION)
                    .from( defLabels )
                    .page( page )
                    .pageSize( pageSize );

            if ( null != edgeId ) {

                builder.id( edgeId );

            }

            final RuleLookupRequest request = builder.build();

            final LookupManager.LookupResponse<Rule> response = ruleLookupManager.lookup( request );

            return response.getResults();
            
        }
        
        return null;
    }

    private  <T> boolean isInEdgeCardinalityRuleAllowed(final String defSetId,
                                                       final Set<String> defLabels,
                                                       final String edgeId) {

        return isEdgeCardinalityRuleAllowed( defSetId, defLabels, edgeId,
                RuleLookupRequestImpl.Builder.EdgeType.INCOMING, 0 );

    }

    private  <T> boolean isOutEdgeCardinalityRuleAllowed(final String defSetId,
                                                       final T sourceDefinition,
                                                       final String edgeId,
                                                       final int edgesCount) {

        final Set<String> defLabels = graphUtils.getDefinitionLabels( sourceDefinition );
        
        return isEdgeCardinalityRuleAllowed( defSetId, defLabels, edgeId, 
                RuleLookupRequestImpl.Builder.EdgeType.OUTGOING, edgesCount );
        
    }

    private <T> boolean isEdgeCardinalityRuleAllowed(final String defSetId,
                                                     final Set<String> defLabels,
                                                     final String edgeId,
                                                     final RuleLookupRequestImpl.Builder.EdgeType edgeType,
                                                     final int edgesCount) {

        if ( null != defSetId ) {

            final RuleLookupRequest request =
                    new RuleLookupRequestImpl.Builder()
                            .definitionSetId(defSetId)
                            .type(RuleLookupRequestImpl.Builder.RuleType.EDGECARDINALITY)
                            .edgeType(edgeType)
                            .roleIn( defLabels )
                            .id( edgeId )
                            .page( 0 )
                            .pageSize( 100 )
                            .build();

            final LookupManager.LookupResponse<Rule> response = ruleLookupManager.lookup( request );

            final List<Rule> rules = response.getResults();

            // TODO: Delegate to the @Model RulesManager?

            if ( null != rules && !rules.isEmpty() ) {

                for ( final Rule rule : rules ) {

                    final EdgeCardinalityRule cr = (EdgeCardinalityRule) rule;

                    final int max = cr.getMaxOccurrences();

                    if ( max == 0 || max >= edgesCount ) {
                        return false;
                    }

                }

            }

            return true;

        }

        return false;
    }

    private <T> int countOutgoingEdges( final Node<? extends Definition<T>, ? extends Edge> sourceNode,
                                        final String edgeId ) {

        final List<? extends Edge> edges = sourceNode.getOutEdges();
        return graphUtils.countEdges( edgeId, edges );

    }

}
