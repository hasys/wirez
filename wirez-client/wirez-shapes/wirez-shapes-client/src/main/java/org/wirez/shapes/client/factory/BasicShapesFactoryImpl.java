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

package org.wirez.shapes.client.factory;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import org.wirez.client.lienzo.canvas.wires.WiresCanvas;
import org.wirez.core.definition.util.DefinitionUtils;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.shape.*;
import org.wirez.core.client.shape.factory.AbstractProxyShapeFactory;
import org.wirez.core.client.shape.view.ShapeGlyph;
import org.wirez.core.client.shape.view.ShapeGlyphBuilder;
import org.wirez.core.client.shape.view.ShapeView;
import org.wirez.shapes.factory.BasicShapesFactory;
import org.wirez.shapes.proxy.*;
import org.wirez.shapes.proxy.icon.ICONS;
import org.wirez.shapes.proxy.icon.IconProxy;
import org.wirez.shapes.client.proxy.RectangleShape;
import org.wirez.shapes.client.view.PolygonView;
import org.wirez.shapes.client.view.RectangleView;
import org.wirez.shapes.client.view.ShapeViewFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Map;

@Dependent
public class BasicShapesFactoryImpl<W> 
        extends AbstractProxyShapeFactory<W, ShapeView, MutableShape<W, ShapeView>, BasicShapeProxy<W>>
        implements BasicShapesFactory<W, AbstractCanvasHandler> {

    protected static final double DEFAULT_SIZE = 50;

    protected ShapeViewFactory shapeViewFactory;
    protected DefinitionUtils definitionUtils;
    protected ShapeGlyphBuilder<Group> glyphBuilder;
    
    protected BasicShapesFactoryImpl() {
    }
    
    @Inject
    public BasicShapesFactoryImpl(final org.wirez.shapes.client.view.ShapeViewFactory shapeViewFactory,
                                  final DefinitionUtils definitionUtils,
                                  final ShapeGlyphBuilder<Group> glyphBuilder) {

        this.shapeViewFactory = shapeViewFactory;
        this.definitionUtils = definitionUtils;
        this.glyphBuilder = glyphBuilder;
        
    }

    @Override
    @SuppressWarnings("unchecked")
    public MutableShape<W, ShapeView> build( final W definition, 
                     final AbstractCanvasHandler context) {

        final String id = definitionUtils.getDefinitionId( definition );
        final BasicShapeProxy<W> proxy = getProxy( id );

        return build( definition, proxy, context );
    }

    @SuppressWarnings("unchecked")
    protected MutableShape<W, ShapeView> build( final W definition,
                                                final BasicShapeProxy<W> proxy,
                                                final AbstractCanvasHandler context) {

        WiresManager wiresManager = context != null ?
                ((WiresCanvas) context.getCanvas()).getWiresManager() : null;
        
        MutableShape<W, ShapeView> shape = null;
        
        if ( isCircle(proxy) ) {

            final CircleProxy<W> circleProxy = (CircleProxy<W>) proxy;

            final double radius = circleProxy.getRadius( definition );

            final org.wirez.shapes.client.view.CircleView view =
                    shapeViewFactory.circle( radius, wiresManager );

            shape = new org.wirez.shapes.client.proxy.CircleShape( view, circleProxy );

        }

        if ( isRectangle(proxy) ) {

            final RectangleProxy<W> rectangleProxy = (RectangleProxy<W>) proxy;

            final double width = rectangleProxy.getWidth( definition );
            final double height = rectangleProxy.getHeight( definition );

            final RectangleView view =
                    shapeViewFactory.rectangle( width, height, wiresManager );

            shape = new RectangleShape( view, rectangleProxy );

        }

        if ( isPolygon(proxy) ) {

            final PolygonProxy<W> polygonProxy = (PolygonProxy<W>) proxy;

            final double radius = polygonProxy.getRadius( definition );
            final String fillColor = polygonProxy.getBackgroundColor( definition );

            final PolygonView view =
                    shapeViewFactory.polygon( radius,
                            fillColor,
                            wiresManager );

            shape = new org.wirez.shapes.client.proxy.PolygonShape( view, polygonProxy );

        }

        if ( isIcon(proxy) ) {

            final IconProxy<W> iconProxy = (IconProxy<W>) proxy;

            final ICONS icon = org.wirez.shapes.client.proxy.IconShape.getIcon( definition, iconProxy );
            final double width = iconProxy.getWidth( definition );
            final double height = iconProxy.getHeight( definition );

            final org.wirez.shapes.client.view.IconShapeView view =
                    shapeViewFactory.icon( icon, width, height, wiresManager );

            shape = new org.wirez.shapes.client.proxy.IconShape( view, iconProxy );

        }
        
        // Add children, if any.
        if ( null != shape && proxy instanceof HasChildProxies) {

            final HasChildProxies<W> hasChildren = (HasChildProxies<W>) proxy;
            final Map<BasicShapeProxy<W>, HasChildren.Layout> childProxies = hasChildren.getChildProxies();
            if ( null != childProxies && !childProxies.isEmpty() ) {
                for ( final Map.Entry<BasicShapeProxy<W>, HasChildren.Layout> entry : childProxies.entrySet() ) {

                    final BasicShapeProxy<W> child = entry.getKey();
                    final HasChildren.Layout layout = entry.getValue();

                    final MutableShape<W, ShapeView> childShape = this.build( definition, child, context);

                    if ( childShape instanceof AbstractCompositeShape ) {

                        ( (AbstractCompositeShape) shape).addChild( (AbstractShape) childShape, layout );

                    }
                    
                }
            }

        }

        if ( null != shape ) {
        
            return shape;
            
        }
        
        final String id = definitionUtils.getDefinitionId( definition );
        throw new RuntimeException( "This factory supports [" + id + "] but cannot built a shape for it." );

    }
    
    private boolean isCircle( final BasicShapeProxy<W> proxy ) {
        return proxy instanceof CircleProxy;
    }

    private boolean isRectangle( final BasicShapeProxy<W> proxy ) {
        return proxy instanceof RectangleProxy;
    }

    private boolean isPolygon( final BasicShapeProxy<W> proxy ) {
        return proxy instanceof PolygonProxy;
    }
    
    private boolean isIcon( final BasicShapeProxy<W> proxy ) {
        return proxy instanceof IconProxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ShapeGlyph glyph( final Class<?> clazz, 
                                final double width, 
                                final double height) {

        final String id = getDefinitionId( clazz );

        return glyphBuilder
                .definition( id )
                .factory( this )
                .height( height )
                .width( width )
                .build();
    }
    
}
