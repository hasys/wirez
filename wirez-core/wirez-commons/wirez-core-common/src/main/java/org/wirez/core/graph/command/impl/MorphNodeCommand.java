/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wirez.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.wirez.core.api.DefinitionManager;
import org.wirez.core.command.CommandResult;
import org.wirez.core.definition.adapter.DefinitionAdapter;
import org.wirez.core.definition.adapter.MorphAdapter;
import org.wirez.core.definition.morph.MorphDefinition;
import org.wirez.core.graph.Edge;
import org.wirez.core.graph.Node;
import org.wirez.core.graph.command.GraphCommandExecutionContext;
import org.wirez.core.graph.command.GraphCommandResultBuilder;
import org.wirez.core.graph.content.definition.Definition;
import org.wirez.core.rule.RuleViolation;

import java.util.Set;

/**
 * A Command to morph a node in a graph.
 */
@Portable
public final class MorphNodeCommand extends AbstractGraphCommand {

    private Node<Definition, Edge> candidate;
    private MorphDefinition morphDefinition;
    private String morphTarget;
    private String oldMorphTarget;

    public MorphNodeCommand(@MapsTo("candidate") Node<Definition, Edge> candidate,
                            @MapsTo("morphDefinition") MorphDefinition morphDefinition,
                            @MapsTo("morphTarget") String morphTarget) {
        this.candidate = PortablePreconditions.checkNotNull( "candidate",
                                                             candidate );
        this.morphDefinition = PortablePreconditions.checkNotNull( "morphDefinition",
                morphDefinition );
        this.morphTarget = PortablePreconditions.checkNotNull( "morphTarget",
                morphTarget );
        
        this.oldMorphTarget = null;
        
    }
    
    @Override
    public CommandResult<RuleViolation> allow(final GraphCommandExecutionContext context) {
        return check( context );
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = check( context );
        
        if ( !results.getType().equals( CommandResult.Type.ERROR ) ) {
            
            final DefinitionManager definitionManager = context.getDefinitionManager();

            final Object currentDef = candidate.getContent().getDefinition();
            final String currentDefId = definitionManager.getDefinitionAdapter( currentDef.getClass() ).getId( currentDef );
            this.oldMorphTarget = currentDefId;

            final MorphAdapter<Object> morphAdapter = context.getDefinitionManager().getMorphAdapter( currentDef.getClass() );

            if ( null == morphAdapter ) {

                throw new RuntimeException( "No morph adapter found for definition [" + currentDef.toString() + "] " +
                        "and target morph [" + morphTarget + "]" );

            }
            
            final Object newDef = morphAdapter.morph( currentDef, morphDefinition, morphTarget );

            if ( null == newDef ) {

                throw new RuntimeException( "No morph resulting Definition. [ morphSource=" + currentDefId + ", " +
                        "morphTarget=" + morphTarget + "]" );

            }

            // Morph the node definition to the new one.
            candidate.getContent().setDefinition( newDef );

            // Update candidate roles.
            final DefinitionAdapter<Object> adapter = definitionManager.getDefinitionAdapter( newDef.getClass() );

            final Set<String> newLabels = adapter.getLabels( newDef );

            candidate.getLabels().clear();

            if ( null != newLabels ) {

                candidate.getLabels().addAll( newLabels );

            }
            
        }
        
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> doCheck(final GraphCommandExecutionContext context) {
        // TODO: check rules before morphing?
        return GraphCommandResultBuilder.RESULT_OK;
    }

    @Override
    public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
        final MorphNodeCommand undoCommand = new MorphNodeCommand( candidate, morphDefinition, oldMorphTarget );
        return undoCommand.execute( context );
    }

    @Override
    public String toString() {
        return "MorphNodeCommand [candidate=" + candidate.getUUID() + ", morphTarget=" + morphTarget + "]";
    }
}
