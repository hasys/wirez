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

package org.wirez.core.client.canvas;

import org.wirez.core.api.definition.Definition;
import org.wirez.core.api.graph.Element;
import org.wirez.core.api.graph.Graph;
import org.wirez.core.api.graph.Node;
import org.wirez.core.client.factory.ShapeFactory;

public interface CanvasHandler {

    /**
     * Initializes a graphical shape canvas.
     */
    CanvasHandler initialize(CanvasSettings settings);

    CanvasHandler register(ShapeFactory factory, Element candidate);

    CanvasHandler deregister(Element candidate);
    /**
     * Listens to events from elements in the canvas.
     */
    CanvasHandler addListener(CanvasListener listener);

    /**
     * Returns the canvas' unique identifier. 
     */
    String getUUID();

    /**
     * Returns the working copy of the graph. 
     */
    Graph<? extends Definition, ? extends Node> getGraph();
    
    /**
     * Returns the shape canvas.
     */
    Canvas getCanvas();
    
}
