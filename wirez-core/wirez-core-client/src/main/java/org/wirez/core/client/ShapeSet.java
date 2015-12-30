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

package org.wirez.core.client;

import com.google.gwt.safehtml.shared.SafeUri;
import org.wirez.core.api.definition.Definition;
import org.wirez.core.api.definition.DefinitionSet;
import org.wirez.core.client.factory.ShapeFactory;

import java.util.Collection;

public interface ShapeSet {

    /**
     * Get the identifier for the set.
     */
    String getId();

    /**
     * Get the name for the set.
     */
    String getName();

    /**
     * Get the description for the set.
     */
    String getDescription();

    /**
     * Get the uri for the thumbnail.
     */
    SafeUri getThumbnailUri();

    /**
     * The id of the wirez set.
     */
    DefinitionSet getDefinitionSet();

    /**
     * The shape factories available in this set.
     * @return The available shapes' factories.
     */
    Collection<ShapeFactory<? extends Definition, ? extends Shape>> getFactories();
}