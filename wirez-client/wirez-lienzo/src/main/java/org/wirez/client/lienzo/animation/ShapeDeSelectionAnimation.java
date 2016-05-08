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

package org.wirez.client.lienzo.animation;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import org.wirez.core.client.animation.Deselect;

import javax.enterprise.context.Dependent;
import java.util.Collection;

import static com.ait.lienzo.client.core.animation.AnimationProperty.Properties.STROKE_ALPHA;

@Dependent
@Deselect
public final class ShapeDeSelectionAnimation extends AbstractShapeAnimation 
        implements org.wirez.core.client.animation.ShapeDeSelectionAnimation {

    private double strokeWidth;
    private double strokeAlpha;
    private String color;

    @Override
    public void run() {
        final Collection<com.ait.lienzo.client.core.shape.Shape> decorators = getDecorators();
        if ( null != decorators && !decorators.isEmpty() ) {
            for( final com.ait.lienzo.client.core.shape.Shape decorator : decorators ) {
                decorator.setStrokeWidth(strokeWidth).setStrokeColor("");
                decorator.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(STROKE_ALPHA(strokeAlpha)), 
                        getDuration(), animationCallback);
            }
        }
    }

    @Override
    public org.wirez.core.client.animation.ShapeDeSelectionAnimation setStrokeWidth( final double sw ) {
        this.strokeWidth = sw;
        return this;
    }

    @Override
    public org.wirez.core.client.animation.ShapeDeSelectionAnimation setStrokeAlpha( final double sa ) {
        this.strokeAlpha = sa;
        return this;
    }

    @Override
    public org.wirez.core.client.animation.ShapeDeSelectionAnimation setColor( final String color ) {
        this.color = color;
        return this;
    }
    
}