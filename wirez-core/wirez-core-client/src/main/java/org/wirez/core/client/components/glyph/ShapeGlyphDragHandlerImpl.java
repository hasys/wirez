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

package org.wirez.core.client.components.glyph;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import org.wirez.core.client.shape.view.ShapeGlyph;

import javax.enterprise.context.Dependent;

// TODO: Refactor implementing DragProxy<T>
@Dependent
public class ShapeGlyphDragHandlerImpl implements ShapeGlyphDragHandler {

    private static final int ZINDEX = Integer.MAX_VALUE;

    @Override
    public void show(LienzoPanel parentLienzoPanel, ShapeGlyph shapeGlyph, double x, double y, Callback callback) {

        final double proxyWidth = shapeGlyph.getWidth();
        final double proxyHeight = shapeGlyph.getHeight();
        final Group dragShape = shapeGlyph.getGroup().copy();
        dragShape.setX( proxyWidth / 2 );
        dragShape.setY( proxyHeight / 2 );

        final LienzoPanel dragProxyPanel = new LienzoPanel( ( (int) proxyWidth * 2),
                ( (int) proxyHeight * 2) );
        final Layer dragProxyLayer = new Layer();
        dragProxyLayer.add( dragShape );
        dragProxyPanel.add( dragProxyLayer );
        dragProxyLayer.batch();

        setDragProxyPosition( parentLienzoPanel,
                dragProxyPanel,
                proxyWidth,
                proxyHeight,
                x, y );

        attachDragProxyHandlers( dragProxyPanel, callback );

        RootPanel.get().add( dragProxyPanel );
    }
    
    private void setDragProxyPosition( final LienzoPanel dragProxyParentPanel,
                                       final LienzoPanel dragProxyPanel,
                                       final double proxyWidth,
                                       final double proxyHeight,
                                       final double x,
                                       final double y) {
        Style style = dragProxyPanel.getElement().getStyle();
        style.setPosition( Style.Position.ABSOLUTE );
        style.setLeft( dragProxyParentPanel.getAbsoluteLeft() + x - ( proxyWidth / 2 ),
                Style.Unit.PX );
        style.setTop( dragProxyParentPanel.getAbsoluteTop() + y - ( proxyHeight / 2 ),
                Style.Unit.PX );
        style.setZIndex( ZINDEX );
    }

    private void attachDragProxyHandlers( final LienzoPanel floatingPanel, final Callback callback ) {
        final Style style = floatingPanel.getElement().getStyle();
        final HandlerRegistration[] handlerRegs = new HandlerRegistration[ 2 ];

        //MouseMoveEvents
        handlerRegs[ 0 ] = RootPanel.get().addDomHandler(new MouseMoveHandler() {

            @Override
            public void onMouseMove( final MouseMoveEvent mouseMoveEvent ) {
                style.setLeft( mouseMoveEvent.getX() - ( floatingPanel.getWidth() / 2 ),
                        Style.Unit.PX );
                style.setTop( mouseMoveEvent.getY() - ( floatingPanel.getHeight() / 2 ),
                        Style.Unit.PX );
                final double x = mouseMoveEvent.getX();
                final double y = mouseMoveEvent.getY();
                callback.onMove(floatingPanel, x, y);
            }
        }, MouseMoveEvent.getType() );

        //MouseUpEvent
        handlerRegs[ 1 ] = RootPanel.get().addDomHandler( new MouseUpHandler() {

            @Override
            public void onMouseUp( final MouseUpEvent mouseUpEvent ) {
                handlerRegs[ 0 ].removeHandler();
                handlerRegs[ 1 ].removeHandler();
                RootPanel.get().remove( floatingPanel );
                final double x = mouseUpEvent.getX();
                final double y = mouseUpEvent.getY();
                callback.onComplete(floatingPanel, x, y);
            }
        }, MouseUpEvent.getType() );
    }

    
}
