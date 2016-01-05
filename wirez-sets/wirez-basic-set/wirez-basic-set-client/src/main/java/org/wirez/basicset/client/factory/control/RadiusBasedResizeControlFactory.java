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

package org.wirez.basicset.client.factory.control;

import org.wirez.basicset.client.control.RadiusBasedResizeControl;
import org.wirez.core.client.Shape;
import org.wirez.core.client.factory.control.ShapeControlFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RadiusBasedResizeControlFactory implements ShapeControlFactory<Shape, RadiusBasedResizeControl<Shape>> {

    public RadiusBasedResizeControlFactory() {
    }

    @Override
    public RadiusBasedResizeControl<Shape> build(final Shape shape) {
        return new RadiusBasedResizeControl<Shape>();
    }
}