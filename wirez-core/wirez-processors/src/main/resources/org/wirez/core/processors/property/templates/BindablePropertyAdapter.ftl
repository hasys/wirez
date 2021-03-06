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

package ${packageName};

import ${parentAdapterClassName};

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentAdapterClassName} {

    private static final Map<Class, String> propValueFieldNames = new HashMap<Class, String>(${valuePropNamesSize}) {{
        <#list valuePropNames as valuePropName>
            put( ${valuePropName.className}.class, "${valuePropName.methodName}" );
        </#list>
    
    }};

    private static final Map<Class, String> propDefaultValueFieldNames = new HashMap<Class, String>(${defaultValuePropNamesSize}) {{
        <#list defaultValuePropNames as defaultValuePropName>
            put( ${defaultValuePropName.className}.class, "${defaultValuePropName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> propTypeFieldNames = new HashMap<Class, String>(${propTypePropNamesSize}) {{
        <#list propTypePropNames as propTypePropName>
              put( ${propTypePropName.className}.class, "${propTypePropName.methodName}" );
        </#list>
    }};
    
    private static final Map<Class, String> propCaptionFieldNames = new HashMap<Class, String>(${captionPropNamesSize}) {{
        <#list captionPropNames as captionPropName>
              put( ${captionPropName.className}.class, "${captionPropName.methodName}" );
        </#list>
    }};
    
    private static final Map<Class, String> propDescriptionFieldNames = new HashMap<Class, String>(${descriptionPropNamesSize}) {{
        <#list descriptionPropNames as descriptionPropName>
              put( ${descriptionPropName.className}.class, "${descriptionPropName.methodName}" );
        </#list>
    }};
    
    private static final Map<Class, String> propReadOnlyFieldNames = new HashMap<Class, String>(${readOnlyPropNamesSize}) {{
        <#list readOnlyPropNames as readOnlyPropName>
            put( ${readOnlyPropName.className}.class, "${readOnlyPropName.methodName}" );
        </#list>
    }};
    
    private static final Map<Class, String> propOptionalFieldNames = new HashMap<Class, String>(${optionalPropNamesSize}) {{
        <#list optionalPropNames as optionalPropName>
           put( ${optionalPropName.className}.class, "${optionalPropName.methodName}" );
        </#list>
    }};

    @Override
    protected Map<Class, String> getPropertyValueFieldNames() {
        return propValueFieldNames;
    }
    
    @Override
    protected Map<Class, String> getPropertyDefaultValueFieldNames() {
        return propDefaultValueFieldNames;
    }

    @Override
    protected Map<Class, String> getPropertyTypeFieldNames() {
         return propTypeFieldNames;
    }
    
    @Override
    protected Map<Class, String> getPropertyCaptionFieldNames() {
          return propCaptionFieldNames;
    }
    
    @Override
    protected Map<Class, String> getPropertyDescriptionFieldNames() {
          return propDescriptionFieldNames;
    }
    
    @Override
    protected Map<Class, String> getPropertyReadOnlyFieldNames() {
          return propReadOnlyFieldNames;
    }
    
    @Override
    protected Map<Class, String> getPropertyOptionalFieldNames() {
          return propOptionalFieldNames;
    }

}
