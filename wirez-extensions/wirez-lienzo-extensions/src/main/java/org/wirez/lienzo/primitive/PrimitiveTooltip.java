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

package org.wirez.lienzo.primitive;

import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class PrimitiveTooltip extends FlowPanel {

    private static final double PADDING = 50;
    private static final double TRIANGLE_SIZE = 20;
    private static final double ALPHA = 0.8;
    private static final Shadow SHADOW = new Shadow(ColorName.BLACK.getColor().setA(0.80), 10, 3, 3);
    
    private Layer canvasLayer = new Layer();
    private LienzoPanel lienzoPanel;

    public PrimitiveTooltip() {
        
        attach();
        
    }

    public PrimitiveTooltip show(final IPrimitive<?> _glyph,
                                 final String text,
                                 final double width,
                                 final double height,
                                 final double x,
                                 final double y) {

        reset();

        final IPrimitive<?> glyph = (IPrimitive<?>) _glyph.copy();
        final Text descText = new Text(text)
                .setFontSize(10)
                .setFontFamily("Verdana");

        final BoundingBox descTextBB = descText.getBoundingBox();
        final double descTextBbW = descTextBB.getWidth();
        final double descTextBbH = descTextBB.getHeight();
        
        final double dw = ( descTextBbW >  width ? ( descTextBbW + PADDING ) : ( width + PADDING ) ); 
        final double dh = height + descTextBbH + PADDING;
        final IPrimitive<?> decorator = buildDecorator(dw, dh);
        final double w = dw + ( TRIANGLE_SIZE * 2);
        final double h = dh;

        
        lienzoPanel = new LienzoPanel( (int) w , (int) h );

        this.add(lienzoPanel);

        lienzoPanel.getScene().add( canvasLayer );
        
        canvasLayer.add(decorator);
        canvasLayer.add(glyph);
        canvasLayer.add(descText);
        
        glyph.setX( TRIANGLE_SIZE + ( w / 2) - (width / 2) );
        glyph.setY(PADDING / 2);

        descText.setX( TRIANGLE_SIZE + ( w / 2) - (descTextBbW / 2) );
        descText.setY(glyph.getY() + height + descTextBbH + 5);
        
        
        this.getElement().getStyle().setLeft(x, Style.Unit.PX);
        this.getElement().getStyle().setTop(y, Style.Unit.PX);
        this.getElement().getStyle().setDisplay(Style.Display.INLINE);
        lienzoPanel.draw();
        
        return this;
    }

    public PrimitiveTooltip hide() {
        reset();
        this.getElement().getStyle().setDisplay(Style.Display.NONE);
        return this;
    }

    public PrimitiveTooltip remove() {
        reset();
        deattach();
        return this;
    }
    
    private void attach() {
        RootPanel.get().add(this);
        this.getElement().getStyle().setPosition(Style.Position.FIXED);
        this.getElement().getStyle().setZIndex(20);
        this.getElement().getStyle().setDisplay(Style.Display.NONE);
    }

    private void deattach() {
        RootPanel.get().remove(this);
    }
    
    private IPrimitive<?> buildDecorator(final double width, final double height) {
        final double h2 = height / 2;
        final double s2 = TRIANGLE_SIZE / 2;
        final Triangle triangle = new Triangle(new Point2D(0, h2), new Point2D(TRIANGLE_SIZE, h2 + s2), new Point2D(TRIANGLE_SIZE, h2 - s2))
                .setFillColor(ColorName.LIGHTGREY)
                .setFillAlpha(ALPHA)
                .setStrokeAlpha(0);
        
        final Rectangle rectangle = new Rectangle(width + TRIANGLE_SIZE, height)
                .setX(TRIANGLE_SIZE)
                .setY(0)
                .setCornerRadius(10)
                .setFillColor(ColorName.LIGHTGREY)
                .setFillAlpha(ALPHA)
                .setStrokeAlpha(0);
                //.setShadow(SHADOW);

        final Group decorator = new Group();
        decorator.add(rectangle);
        decorator.add(triangle);

        return decorator;
    }
    
    private void reset() {
        this.clear();
        canvasLayer = new Layer();
        lienzoPanel = null;
    }
    
}
