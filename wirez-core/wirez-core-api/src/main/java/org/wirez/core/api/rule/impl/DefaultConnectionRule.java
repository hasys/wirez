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
package org.wirez.core.api.rule.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;
import org.wirez.core.api.rule.ConnectionRule;

import java.util.Set;

@Portable
public class DefaultConnectionRule implements ConnectionRule {

    private String name;
    private String id;
    private Set<PermittedConnection> permittedConnections;

    public DefaultConnectionRule(@MapsTo("name") final String name,
                                 @MapsTo("id") final String id,
                                 @MapsTo("permittedConnections") final Set<ConnectionRule.PermittedConnection> permittedConnections) {
        this.name = PortablePreconditions.checkNotNull( "name",
                                                        name );
        this.id = PortablePreconditions.checkNotNull( "id",
                id );
        this.permittedConnections = PortablePreconditions.checkNotNull( "permittedConnections",
                permittedConnections);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<PermittedConnection> getPermittedConnections() {
        return permittedConnections;
    }

}
