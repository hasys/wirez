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

package org.wirez.core.api.definition.property;

import org.uberfire.commons.validation.PortablePreconditions;

public abstract class BaseHasDefaultValueProperty<C> extends BaseProperty implements HasDefaultValue<C> {

    protected C defaultValue;

    public BaseHasDefaultValueProperty(final String id) {
        super(id);
    }

    public BaseHasDefaultValueProperty(final String id,
                                       final String caption,
                                       final String description,
                                       final boolean isReadOnly,
                                       final boolean isOptional,
                                       final boolean isPublic,
                                       final C defaultValue) {
        super(id, caption, description, isReadOnly, isOptional, isPublic);
        this.defaultValue = PortablePreconditions.checkNotNull( "defaultValue",
                defaultValue );
    }

    @Override
    public C getDefaultValue() {
        return defaultValue;
    }

    public BaseHasDefaultValueProperty<C> setDefaultValue(C defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

}