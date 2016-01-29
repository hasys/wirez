package org.wirez.bpmn.backend.marshall.json.builder;

import org.wirez.core.api.definition.Definition;
import org.wirez.core.api.graph.Edge;
import org.wirez.core.api.graph.Node;
import org.wirez.core.api.graph.content.ViewContent;

public interface EdgeObjectBuilder<W extends Definition> extends GraphObjectBuilder<W, Edge<ViewContent<W>, Node>> {
    
}