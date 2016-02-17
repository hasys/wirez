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
package org.wirez.core.api.graph.command.impl;

import org.uberfire.commons.validation.PortablePreconditions;
import org.wirez.core.api.command.Command;
import org.wirez.core.api.command.CommandResult;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.command.GraphCommandFactory;
import org.wirez.core.api.graph.command.GraphCommandResult;
import org.wirez.core.api.graph.content.ViewContent;
import org.wirez.core.api.graph.impl.BoundImpl;
import org.wirez.core.api.graph.impl.BoundsImpl;
import org.wirez.core.api.rule.RuleManager;
import org.wirez.core.api.rule.RuleViolation;
import org.wirez.core.api.util.ElementUtils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Command to update an element's bounds.
 */
public class UpdateElementPositionCommand extends AbstractCommand {

    private static Logger LOGGER = Logger.getLogger("org.wirez.core.api.graph.commands.UpdateElementPositionCommand");
    
    private Element element;
    private Double x;
    private Double y;
    private Double oldX;
    private Double oldY;


    public UpdateElementPositionCommand(final GraphCommandFactory commandFactory,
                                        final Element element,
                                        final Double x,
                                        final Double y) {
        super(commandFactory);
        this.element = PortablePreconditions.checkNotNull( "element",
                element );;
        this.x = PortablePreconditions.checkNotNull( "x",
                x );
        this.y = PortablePreconditions.checkNotNull( "y",
                y );
    }
    
    @Override
    public CommandResult<RuleViolation> allow(final RuleManager ruleManager) {
        // TODO: Check bounds values.
        return new GraphCommandResult();
    }

    @Override
    public CommandResult<RuleViolation> execute(final RuleManager ruleManager) {
        final Double[] oldPosition = ElementUtils.getPosition((ViewContent) element.getContent());
        final Double[] oldSize = ElementUtils.getSize((ViewContent) element.getContent());
        this.oldX = oldPosition[0];
        this.oldY = oldPosition[1];
        final double w = oldSize[0];
        final double h = oldSize[1];

        LOGGER.log(Level.FINE, "Moving element bounds to [" + x + "," + y + "] [" + ( x + w ) + "," + ( y + h ) + "]");
        final BoundsImpl newBounds = new BoundsImpl(
                new BoundImpl(x + w, y + h),
                new BoundImpl(x, y)
        );
        ((ViewContent) element.getContent()).setBounds(newBounds);
        return new GraphCommandResult(new ArrayList<RuleViolation>());
    }
    
    @Override
    public CommandResult<RuleViolation> undo(RuleManager ruleManager) {
        final Command<RuleManager, RuleViolation> undoCommand = commandFactory.updateElementPositionCommand( element, oldX, oldY);
        return undoCommand.execute( ruleManager );
    }

    public Double getOldX() {
        return oldX;
    }

    public Double getOldY() {
        return oldY;
    }

    @Override
    public String toString() {
        return "UpdateElementPositionCommand [element=" + element.getUUID() + ", x=" + x + ", y=" + y + "]";
    }
    
}