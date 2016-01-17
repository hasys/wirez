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

package org.wirez.core.api.graph.factory;

import org.wirez.core.api.definition.Definition;
import org.wirez.core.api.graph.Bounds;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.ViewContent;
import org.wirez.core.api.graph.content.ViewContentImpl;
import org.wirez.core.api.graph.impl.DefaultBound;
import org.wirez.core.api.graph.impl.DefaultBounds;
import org.wirez.core.api.graph.impl.NodeImpl;

import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.util.Map;
import java.util.Set;

@Dependent
@Named("nodeFactoryImpl")
public class NodeFactoryImpl<W extends Definition> implements NodeFactory<ViewContent<W>> {
    
    @Override
    public Node<ViewContent<W>, Edge> build(String uuid, Set<String> labels, Map<String, Object> properties) {
        return new NodeImpl<ViewContent<W>>(uuid, properties, labels, new ViewContentImpl<>( (W) this, buildBounds()) );
    }

    // TODO: ??
    protected Bounds buildBounds() {
        return new DefaultBounds(new DefaultBound(0d, 0d), new DefaultBound(0d, 0d));
    }
    
}
