package org.wirez.bpmn.api.factory;

import org.wirez.bpmn.api.*;
import org.wirez.core.api.factory.DefinitionSetBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class BPMNDefinitionSetBuilder implements DefinitionSetBuilder<BPMNDefinitionSet> {

    BPMNDefinitionBuilder bpmnDefinitionFactory;

    protected BPMNDefinitionSetBuilder() {
    }

    @Inject
    public BPMNDefinitionSetBuilder(BPMNDefinitionBuilder bpmnDefinitionFactory) {
        this.bpmnDefinitionFactory = bpmnDefinitionFactory;
    }

    @Override
    public boolean accepts(final String id) {
        return id.equals(BPMNDefinitionSet.ID);
    }

    @Override
    public BPMNDefinitionSet build(final String id) {
        return new BPMNDefinitionSet(bpmnDefinitionFactory.buildBPMNGraph(), 
                bpmnDefinitionFactory.buildBPMNDiagram(),
                bpmnDefinitionFactory.buildStartNoneEvent(),
                bpmnDefinitionFactory.buildEndNoneEvent(),
                bpmnDefinitionFactory.buildTask(),
                bpmnDefinitionFactory.buildSequenceFlow(),
                bpmnDefinitionFactory.buildParallelGateway(),
                bpmnDefinitionFactory.buildEndTerminateEvent(),
                bpmnDefinitionFactory.buildLane());
    }
    
}