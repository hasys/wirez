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

package org.wirez.core.processors.rule;

import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.wirez.core.api.definition.adapter.binding.BindableDefinitionSetRuleAdapter;
import org.wirez.core.processors.AbstractBindableAdapterGenerator;
import org.wirez.core.processors.ProcessingRule;

import javax.annotation.processing.Messager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindableDefinitionSetRuleAdapterGenerator extends AbstractBindableAdapterGenerator {

    @Override
    protected String getTemplatePath() {
        return "BindableDefinitionSetRuleAdapter.ftl";
    }


    public StringBuffer generate(String packageName, String className,
                                 String defSetClassName,
                                 List<ProcessingRule> rules,
                                 Messager messager) throws GenerationException {

        Map<String, Object> root = new HashMap<String, Object>();
        
        root.put("packageName",
                packageName);
        root.put("className",
                className);
        root.put("parentAdapterClassName",
                BindableDefinitionSetRuleAdapter.class.getName());
        root.put("generatedByClassName",
                BindableDefinitionSetRuleAdapterGenerator.class.getName());
        root.put("defSetClassName",
                defSetClassName);
        root.put("rules",
                rules);
        root.put("rulesSize",
                rules.size());
        root.put("ruleIds",
                getRuleIds(rules));
        
        // Generate code
        return writeTemplate(packageName, className, root, messager);

    }
    
    private List<String> getRuleIds(List<ProcessingRule> rules) {
        List<String> result = new ArrayList<>();
        for (ProcessingRule entity : rules ) {
            result.add(entity.getId());
        }
        return result;
    }
    
}