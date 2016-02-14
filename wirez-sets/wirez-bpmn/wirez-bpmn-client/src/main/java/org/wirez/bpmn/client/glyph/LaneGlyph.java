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

package org.wirez.bpmn.client.glyph;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import org.wirez.bpmn.api.Lane;
import org.wirez.core.client.ShapeGlyph;
import org.wirez.core.client.util.ShapeUtils;

public class LaneGlyph implements ShapeGlyph {

    public static final LaneGlyph INSTANCE = new LaneGlyph();
    private static final double WIDTH = 50;
    private static final double HEIGHT = 50;
    private Group group = new Group();

    public LaneGlyph() {
        final Rectangle rectangle = new Rectangle(WIDTH, HEIGHT)
                .setStrokeWidth(0.5)
                .setFillGradient(ShapeUtils.getLinearGradient(Lane.COLOR, "#FFFFFF", WIDTH, HEIGHT));
        group.add(rectangle);
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }
}