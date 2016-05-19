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

package org.wirez.core.graph.store;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.wirez.core.graph.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Portable
public class GraphNodeStoreImpl implements GraphNodeStore<Node> {

    protected Map<String, Node> nodes = new HashMap<String, Node>();
    
    @Override
    public Node add(final Node node) {
        return nodes.put(node.getUUID(), node);
    }

    @Override
    public Node remove(final String uuid) {
        return nodes.remove(uuid);
    }

    @Override
    public Node get(final String uuid) {
        return nodes.get(uuid);
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.values().iterator();
    }
}