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

package org.wirez.core.backend.graph.factory;

import org.wirez.core.api.DefinitionManager;
import org.wirez.core.api.definition.Definition;
import org.wirez.core.api.definition.property.Property;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.ViewContent;
import org.wirez.core.api.graph.content.ViewContentImpl;
import org.wirez.core.api.graph.factory.NodeFactory;
import org.wirez.core.api.graph.impl.NodeImpl;
import org.wirez.core.api.util.UUID;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;

@Dependent
public class NodeFactoryImpl<W extends Definition> extends BaseElementFactory<W, Node<ViewContent<W>, Edge>> implements NodeFactory<W> {

    @Override
    public Node<ViewContent<W>, Edge> build(final W definition, final Set<Property> properties, final Set<String> labels) {
        Node<ViewContent<W>, Edge> node =
                new NodeImpl<ViewContent<W>>(UUID.uuid(),
                        properties,
                        labels,
                        new ViewContentImpl<>( definition, buildBounds())

                );

        return node;
    }
    
}