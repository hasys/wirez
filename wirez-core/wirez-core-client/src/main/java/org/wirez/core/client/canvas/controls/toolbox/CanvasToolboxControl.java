/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.wirez.core.client.canvas.controls.toolbox;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.shared.core.types.Direction;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.wirez.core.api.graph.Element;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.wirez.core.client.canvas.controls.toolbox.command.Context;
import org.wirez.core.client.canvas.controls.toolbox.command.ContextImpl;
import org.wirez.core.client.canvas.controls.toolbox.command.ToolboxCommand;
import org.wirez.core.client.shape.Shape;
import org.wirez.lienzo.toolbox.HoverToolbox;
import org.wirez.lienzo.toolbox.Toolboxes;
import org.wirez.lienzo.toolbox.builder.ButtonsOrRegister;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.*;

// TODO: Move pending lienzo code to view.
@Dependent
public class CanvasToolboxControl extends AbstractCanvasHandlerRegistrationControl 
        implements ToolboxControl<AbstractCanvasHandler, Element>, IsWidget {


    public interface View extends IsWidget{

        View addWidget(IsWidget widget);

        View clear();

    }

    private final Map<String, HoverToolbox> toolboxMap = new HashMap<>();
    SyncBeanManager beanManager;
    View view;

    @Inject
    public CanvasToolboxControl(final SyncBeanManager beanManager, 
                                final View view) {
        this.beanManager = beanManager;
        this.view = view;
    }
    
    @SuppressWarnings("unchecked")
    protected List<ToolboxCommand> getCommands( final Element element ) {

        if ( element.getContent() instanceof  org.wirez.core.api.graph.content.view.View ) {
            final org.wirez.core.api.graph.content.view.View viewContent = 
                    (org.wirez.core.api.graph.content.view.View) element.getContent();

            final List<ToolboxCommand> result = new LinkedList<>();

            // Create a command provider instance for each one available and load the provided commands..
            Collection<SyncBeanDef<ToolboxControlProvider>> beanDefSets = beanManager.lookupBeans(ToolboxControlProvider.class);
            for (SyncBeanDef<ToolboxControlProvider> providersSet : beanDefSets) {
                ToolboxControlProvider<Object> commandsProvider  = providersSet.newInstance();
                final List<ToolboxCommand> providerCommands = commandsProvider.getCommands( viewContent.getDefinition() );
                if ( null != providerCommands && !providerCommands.isEmpty() ) {
                    result.addAll( providerCommands );
                }
            }

            return result;
            
        }
        
        return null;
    }

    @Override
    public void enable(final AbstractCanvasHandler canvasHandler) {
        // Add the control view.
        canvasHandler.getCanvas().addControl( CanvasToolboxControl.this.asWidget() );
        super.enable( canvasHandler );
    }

    @Override
    public void register(final Element element) {

        final Shape shape = canvasHandler.getCanvas().getShape( element.getUUID() );
        
        if (shape.getShapeView() instanceof WiresShape) {

            final List<ToolboxCommand> commands = getCommands( element );
            
            if ( null != commands && !commands.isEmpty() ) {
                
                final WiresShape wiresShape = (WiresShape) shape.getShapeView();

                final ButtonsOrRegister register = Toolboxes.hoverToolBoxFor(wiresShape)
                        .on(Direction.SOUTH_EAST)
                        .towards(Direction.EAST)
                        .grid(commands.size(), 1);

                for (final ToolboxCommand command : commands) {

                    // TODO: Use command title (tooltip).

                    register.add(command.getIcon())
                            .setClickHandler(event -> {
                                Context _context = new ContextImpl(canvasHandler,
                                        event.getX(),
                                        event.getY());
                                setCommandView(command).execute(_context, element);
                            })
                            .end();

                }

                final HoverToolbox hoverToolbox = register.register();
                toolboxMap.put( element.getUUID(), hoverToolbox );
                  
            }
            
        }
        
    }

    @Override
    public void deregister(final Element element) {

        final HoverToolbox hoverToolbox = toolboxMap.get( element.getUUID() );
        if ( null != hoverToolbox ) {
            hoverToolbox.remove();
        }
        
    }

    @Override
    public void disable() {
        
        // Delete the control view.
        canvasHandler.getCanvas().deleteControl( CanvasToolboxControl.this.asWidget() );
        
        // De-register all toolbox components.
        for ( final HoverToolbox hoverToolbox : toolboxMap.values() ) {
            hoverToolbox.remove();
        }
        
    }

    private ToolboxCommand setCommandView(final ToolboxCommand command) {
        view.clear();
        if (command instanceof IsWidget) {
            view.addWidget(((IsWidget) command).asWidget());
        }
        return command;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

}
