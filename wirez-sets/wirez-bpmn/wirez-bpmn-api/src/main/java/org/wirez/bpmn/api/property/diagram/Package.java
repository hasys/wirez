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

package org.wirez.bpmn.api.property.diagram;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.wirez.core.api.annotation.property.DefaultValue;
import org.wirez.core.api.annotation.property.Value;
import org.wirez.core.api.definition.property.BaseHasDefaultValueProperty;
import org.wirez.core.api.definition.property.BaseProperty;
import org.wirez.core.api.definition.property.PropertyType;
import org.wirez.core.api.definition.property.type.StringType;

@Portable
@org.wirez.core.api.annotation.property.Property
public class Package extends BaseProperty {

    public static final String ID = "package";

    public static final String DEFAULT_VALUE = "/defaultPackage/defaultPackage";

    private String value = DEFAULT_VALUE;
    
    public Package() {
        super(ID, "Package", "The diagram's package", false, false, true);
    }

    @Override
    public PropertyType getType() {
        return new StringType();
    }

    @DefaultValue
    public String getDefaultValue() {
        return DEFAULT_VALUE;
    }
    
    @Value
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}