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
package org.wirez.core.api.graph.commands;

import org.uberfire.commons.validation.PortablePreconditions;
import org.wirez.core.api.command.Command;
import org.wirez.core.api.command.CommandResult;
import org.wirez.core.api.command.DefaultCommandResult;
import org.wirez.core.api.definition.Definition;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.impl.DefaultEdge;
import org.wirez.core.api.graph.impl.DefaultGraph;
import org.wirez.core.api.graph.impl.DefaultNode;
import org.wirez.core.api.rule.DefaultRuleManager;
import org.wirez.core.api.rule.RuleManager;
import org.wirez.core.api.rule.RuleViolation;

import java.util.Collection;

/**
 * A Command to delete a DefaultEdge from a Graph
 */
public class DeleteEdgeCommand implements Command {

    private DefaultGraph<? extends Definition, DefaultNode, DefaultEdge> graph;
    private DefaultEdge<? extends Definition, DefaultNode> edge;

    public DeleteEdgeCommand(final DefaultGraph<? extends Definition, DefaultNode, DefaultEdge> graph,
                             final DefaultEdge<? extends Definition, DefaultNode> edge) {
        this.graph = PortablePreconditions.checkNotNull( "graph",
                graph );;
        this.edge = PortablePreconditions.checkNotNull( "edge",
                edge );;
    }
    
    @Override
    public CommandResult allow(final RuleManager ruleManager) {
        final CommandResult results = check(ruleManager);
        return results;
    }

    @Override
    public CommandResult execute(final RuleManager ruleManager) {
        final CommandResult results = check(ruleManager);
        if ( !results.getType().equals(CommandResult.Type.ERROR) ) {
            
            final DefaultNode outNode = edge.getTargetNode();
            final DefaultNode inNode = edge.getSourceNode();

            if ( null != outNode ) {
                outNode.getInEdges().remove( edge );
            }
            if ( null != inNode ) {
                inNode.getOutEdges().remove( edge );
            }

            graph.removeEdge( edge.getUUID() );
            
        }
        return results;
    }
    
    private CommandResult check(final RuleManager ruleManager) {
        final DefaultRuleManager defaultRuleManager = (DefaultRuleManager) ruleManager;
        boolean isEdgeInGraph = false;
        for ( Edge edge : graph.edges() ) {
            if ( edge.equals( this.edge ) ) {
                isEdgeInGraph = true;
                break;
            }
        }

        DefaultCommandResult results;
        if ( isEdgeInGraph ) {
            final Collection<RuleViolation> cardinalityRuleViolations = (Collection<RuleViolation>) defaultRuleManager.checkCardinality(edge.getTargetNode(), edge.getSourceNode(), edge, RuleManager.Operation.DELETE).violations();
            results = new DefaultCommandResult(cardinalityRuleViolations);
        } else {
            results = new DefaultCommandResult();
            results.setType(CommandResult.Type.ERROR);
            results.setMessage("Edge was not present in Graph and hence was not deleted");
        }

        return results;

    }

    @Override
    public CommandResult undo(RuleManager ruleManager) {
        final Command undoCommand = new AddEdgeCommand( graph, edge );
        return undoCommand.execute( ruleManager );
    }

    @Override
    public String toString() {
        return "DeleteEdgeCommand [graph=" + graph.getUUID() + ", edge=" + edge.getUUID() + "]";
    }
}