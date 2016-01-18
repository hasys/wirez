package org.wirez.core.api.service.definition;

import org.jboss.errai.bus.server.annotations.Remote;
import org.wirez.core.api.graph.Element;

@Remote
public interface DefinitionService {

    DefinitionSetResponse getDefinitionSet(String id);

    DefinitionResponse getDefinition(String id);
    
    Element buildGraphElement(String definitionId);
    
}