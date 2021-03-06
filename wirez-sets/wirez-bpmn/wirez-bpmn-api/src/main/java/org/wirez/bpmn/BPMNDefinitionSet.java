package org.wirez.bpmn;

import org.jboss.errai.databinding.client.api.Bindable;
import org.wirez.bpmn.definition.*;
import org.wirez.bpmn.definition.factory.BPMNAbstractGraphFactory;
import org.wirez.core.definition.annotation.Description;
import org.wirez.core.definition.annotation.definitionset.DefinitionSet;
import org.wirez.core.rule.annotation.CanContain;
import org.wirez.core.graph.Graph;
import org.wirez.core.rule.annotation.Occurrences;
import org.wirez.core.definition.annotation.ShapeSet;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Bindable
@DefinitionSet(
        type = Graph.class,
        factory = BPMNAbstractGraphFactory.FACTORY_NAME,
        definitions = {
            BPMNDiagram.class,
            StartNoneEvent.class,
            EndNoneEvent.class,
            Task.class,
            SequenceFlow.class,
            ParallelGateway.class,
            EndTerminateEvent.class,
            Lane.class 
        }
)
@CanContain( roles = { "diagram" } )
@Occurrences(
        role = "Startevents_all",
        min = 0
)
@Occurrences(
        role = "Endevents_all",
        min = 0
)
@ShapeSet
public class BPMNDefinitionSet {

    @Description
    public static final transient String description = "BPMN v2 Set";

    public BPMNDefinitionSet() {
        
    }
    
    public String getDescription() {
        return description;
    }

}
