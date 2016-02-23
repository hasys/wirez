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

package org.wirez.core.api.graph.command;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.wirez.core.api.command.AbstractCommandResult;
import org.wirez.core.api.rule.RuleViolation;

import java.util.Collection;

@Portable
public class GraphCommandResult extends AbstractCommandResult<RuleViolation> {

    public GraphCommandResult() {
    }

    public GraphCommandResult(Collection<RuleViolation> violations) {
        super(violations);
    }

    @Override
    protected boolean isError(final RuleViolation violation) {
        return RuleViolation.Type.ERROR.equals(violation.getViolationType());
    }

    @Override
    protected String getMessage(final RuleViolation violation) {
        return violation.getMessage();
    }

}
