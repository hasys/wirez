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

package org.wirez.bpmn.definition.property.simulation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.wirez.bpmn.definition.BPMNProperty;
import org.wirez.core.definition.annotation.Description;
import org.wirez.core.definition.annotation.property.*;
import org.wirez.core.definition.property.PropertyType;
import org.wirez.core.definition.property.type.StringType;

@Portable
@Bindable
@Property
public class TimeUnit implements BPMNProperty {

    @Caption
    public static final transient String caption = "Time Unit";

    @Description
    public static final transient String description = "Time unit options (seconds, minutes, hours).";

    @ReadOnly
    public static final Boolean readOnly = false;

    @Optional
    public static final Boolean optional = false;

    @Type
    public static final PropertyType type = new StringType();


    @DefaultValue
    public static final transient String defaultValue = "ms";

    @Value
    private String value = defaultValue;

    public TimeUnit() {
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isOptional() {
        return optional;
    }

    public PropertyType getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}