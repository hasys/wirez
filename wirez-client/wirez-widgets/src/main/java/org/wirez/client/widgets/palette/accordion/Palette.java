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

package org.wirez.client.widgets.palette.accordion;

import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.wirez.client.widgets.palette.accordion.group.PaletteGroup;
import org.wirez.client.widgets.palette.accordion.group.PaletteGroupItem;
import org.wirez.core.api.DefinitionManager;
import org.wirez.core.api.definition.adapter.DefinitionAdapter;
import org.wirez.core.client.ShapeManager;
import org.wirez.core.client.ShapeSet;
import org.wirez.core.client.components.glyph.GlyphTooltip;
import org.wirez.core.client.components.glyph.ShapeGlyphDragHandler;
import org.wirez.core.client.service.ClientFactoryServices;
import org.wirez.core.client.service.ClientRuntimeError;
import org.wirez.core.client.service.ServiceCallback;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.factory.ShapeFactory;
import org.wirez.core.client.shape.view.ShapeGlyph;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Refactor implementing org.wirez.core.client.components.palette.Palette
@Dependent
public class Palette implements IsWidget {

    private static Logger LOGGER = Logger.getLogger(Palette.class.getName());
    
    public interface View extends UberView<Palette> {
        
        View setNoCanvasViewVisible(boolean isVisible);
        
        View setGroupsViewVisible(boolean isVisible);
        
        View addGroup(IsWidget paletteGroupView);

        View clearGroups();
        
        View clear();

    }

    public interface Callback {

        void onAddShape( Object definition, ShapeFactory<?, ?, ? extends Shape> factory, double x, double y );
    }

    ShapeManager shapeManager;
    DefinitionManager definitionManager;
    ClientFactoryServices clientFactoryServices;
    SyncBeanManager beanManager;
    GlyphTooltip paletteTooltip;
    ShapeGlyphDragHandler shapeGlyphDragHandler;
    ErrorPopupPresenter errorPopupPresenter;
    View view;
    
    private Callback callback;
    
    @Inject
    public Palette(final View view,
                   final ErrorPopupPresenter errorPopupPresenter,
                   final ShapeManager shapeManager,
                   final DefinitionManager definitionManager,
                   final ClientFactoryServices clientFactoryServices,
                   final SyncBeanManager beanManager,
                   final GlyphTooltip paletteTooltip,
                   final ShapeGlyphDragHandler shapeGlyphDragHandler) {
        this.view = view;
        this.errorPopupPresenter = errorPopupPresenter;
        this.shapeManager = shapeManager;
        this.definitionManager = definitionManager;
        this.clientFactoryServices = clientFactoryServices;
        this.beanManager = beanManager;
        this.paletteTooltip = paletteTooltip;
        this.shapeGlyphDragHandler = shapeGlyphDragHandler;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        showNoCanvasState();
    }
    
    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void showNoCanvasState() {
        clear();
        view.setGroupsViewVisible(false);
        view.setNoCanvasViewVisible(true);
    }
    
    public void show(final int width, final String shapeSetId, final Callback callback) {
        this.callback = callback;
        clear();
        final ShapeSet wirezShapeSet = getShapeSet(shapeSetId);
        final String definitionSetId = wirezShapeSet.getDefinitionSetId();

        clientFactoryServices.newDomainObject(definitionSetId, new ServiceCallback<Object>() {
            @Override
            public void onSuccess(final Object definitionSet) {
                doShow(width, wirezShapeSet, definitionSet);
                view.setNoCanvasViewVisible(false);
                view.setGroupsViewVisible(true);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                showError(error);
            }
        });
        
    }
    
    private ShapeSet getShapeSet(final String shapeSetId) {
        final Collection<ShapeSet> sets = shapeManager.getShapeSets();
        for (final ShapeSet set  : sets) {
            if (set.getId().equals(shapeSetId)) {
                return set;
            }
        }
        return null;
    }
    
    public void clear() {
        view.clear();
    }
    
    private void doShow(final int width, final ShapeSet wirezShapeSet, final Object definitionSet) {

        // Clear current palette groups.
        view.clearGroups();

        final Collection<String> definitions = definitionManager.getDefinitionSetAdapter( definitionSet.getClass() ).getDefinitions( definitionSet );
        final Collection<ShapeFactory<?, ?, ? extends Shape>> factories = wirezShapeSet.getFactories();

        // Load entries.
        final Map<String, List<PaletteGroupItem>> paletteGroupItems = new HashMap<>();
        for (final ShapeFactory<?, ?, ? extends Shape> factory : factories) {
            final Object definition = getDefinition(definitions, factory);

            if ( null != definition ) {
                
                final DefinitionAdapter definitionAdapter = definitionManager.getDefinitionAdapter( definition.getClass() );

                final String category = definitionAdapter.getCategory( definition );
                final String description = factory.getDescription();
                final ShapeGlyph glyph = factory.getGlyphFactory().build();
                
                // Shapes not considered to be on the palette.
                if ( null == glyph ) {
                    continue;
                }

                List<PaletteGroupItem> items = paletteGroupItems.get(category);
                if ( null == items ) {
                    items = new LinkedList<>();
                    paletteGroupItems.put(category, items);
                }
                
                PaletteGroupItem paletteGroupItem = PaletteGroup.buildItem(glyph, description,
                        new PaletteGroupItem.Handler() {

                            @Override
                            public void onFocus(double x, double y) {
                                paletteTooltip.show(glyph,description,  width + 5, y - 50);
                            }

                            @Override
                            public void onLostFocus() {
                                paletteTooltip.hide();
                            }

                            /**
                             * Add the shape into the canvas diagram when mouse click at a fixed position.
                             * Disabled for now.
                             */
                            @Override
                            public void onClick() {
                                // TODO: Disabled adding shape on just click. It should be added inside the BPMNDiagram by default.
                                // log(Level.FINE, "Palette: Adding " + description);
                                // addShapeToCanvasEvent.fire(new AddShapeToCanvasEvent(definition, factory));
                            }

                            /**
                             * Add the shape into the canvas diagram using drag features. 
                             * Drag proxy and final position into the canvas is given by the glyph drag handler implementation.
                             */
                            @Override
                            public void onDragStart(final LienzoPanel parentPanel, final double x, final double y) {

                                shapeGlyphDragHandler.show(parentPanel, glyph, x, y, new ShapeGlyphDragHandler.Callback() {
                                    @Override
                                    public void onMove(final LienzoPanel floatingPanel, final double x, final double y) {

                                    }

                                    @Override
                                    public void onComplete(final LienzoPanel floatingPanel, final double x, final double y) {
                                        if ( null != callback ) {
                                            log(Level.FINE, "Palette: Adding " + description + " at " + x + "," + y);
                                            callback.onAddShape( definition, factory, x, y );
                                        }
                                    }
                                });

                            }
                        });

                items.add(paletteGroupItem);
                
            }
            
        }
        
        // Show palette groups.
        if (!paletteGroupItems.isEmpty()) {
            
            int x = 0;
            final Set<Map.Entry<String, List<PaletteGroupItem>>> entries = paletteGroupItems.entrySet();
            for (final Map.Entry<String, List<PaletteGroupItem>> entry : entries) {
                final String category = entry.getKey();
                final List<PaletteGroupItem> items = entry.getValue();

                PaletteGroup paletteGroup = buildPaletteGroup();
                paletteGroup.show(category, x == 0, width, items);
                view.addGroup(paletteGroup);
                x++;
            }
        }
        
    }
    
    private Object getDefinition(final Collection<String> definitions, final ShapeFactory factory) {
        for (final String definitionId : definitions) {
            if (factory.accepts( definitionId )) {
                return clientFactoryServices.newDomainObject( definitionId );
            }
        }
        return null;
    }
    
    private PaletteGroup buildPaletteGroup() {
        return beanManager.lookupBean( PaletteGroup.class ).newInstance();
    }

    void showError(final ClientRuntimeError error) {
        final String message = error.getCause() != null ? error.getCause() : error.getMessage();
        showError(message);
    }

    void showError(final String message) {
        errorPopupPresenter.showMessage(message);
    }

    private void log(final Level level, final String message) {
        if ( LogConfiguration.loggingIsEnabled() ) {
            LOGGER.log(level, message);
        }
    }
    
}
