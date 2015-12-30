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
package org.wirez.core.client.canvas.command.impl;


import org.wirez.core.api.command.Command;
import org.wirez.core.api.command.CommandResult;
import org.wirez.core.api.definition.property.Property;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.commands.UpdateElementPropertyValueCommand;
import org.wirez.core.api.rule.RuleManager;
import org.wirez.core.client.canvas.command.BaseCanvasCommand;
import org.wirez.core.client.canvas.command.CanvasCommand;
import org.wirez.core.client.canvas.impl.BaseCanvas;
import org.wirez.core.client.canvas.impl.BaseCanvasHandler;
import org.wirez.core.client.mutation.HasGraphElementMutation;

/**
 * A Command to update an element's property produce the corresponding shape mutation.
 */
public class UpdateCanvasElementPropertyValueCommand extends BaseCanvasCommand implements CanvasCommand {

    Element element;
    Property property;
    Object value;
    
    public UpdateCanvasElementPropertyValueCommand(final Element element ,
                                                   final Property property,
                                                   final Object value) {
        this.element = element;
        this.property = property;
        this.value = value;
    }

    @Override
    protected Command getCommand() {
        return new UpdateElementPropertyValueCommand(element, property, value);
    }

    @Override
    public CanvasCommand apply() {
        final BaseCanvasHandler wirezCanvas = (BaseCanvasHandler) canvasHandler;
        wirezCanvas.updateElementProperties(element);
        return this;
    }

    @Override
    public CommandResult execute(final RuleManager ruleManager) {
        return super.execute(ruleManager);
    }
    
    @Override
    public CommandResult undo(RuleManager ruleManager) {
        final CommandResult result = super.execute(ruleManager);
        // TODO
        return result;
    }

    @Override
    public String toString() {
        return "UpdateCanvasElementPropertyValueCommand [element=" + element.getUUID() + ", property=" + property.getId() + ", value=" +  value + "]";
    }
    
}