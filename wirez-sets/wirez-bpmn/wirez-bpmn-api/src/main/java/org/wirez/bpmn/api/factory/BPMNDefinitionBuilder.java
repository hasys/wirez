package org.wirez.bpmn.api.factory;

import org.wirez.bpmn.api.*;
import org.wirez.core.api.factory.DefinitionBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.Set;

@ApplicationScoped
public class BPMNDefinitionBuilder implements DefinitionBuilder<BPMNDefinition> {

    BPMNPropertyBuilder bpmnPropertyFactory;
    BPMNPropertySetBuilder bpmnPropertySetFactory;

    protected BPMNDefinitionBuilder() {
    }

    @Inject
    public BPMNDefinitionBuilder(BPMNPropertyBuilder bpmnPropertyFactory, BPMNPropertySetBuilder bpmnPropertySetFactory) {
        this.bpmnPropertyFactory = bpmnPropertyFactory;
        this.bpmnPropertySetFactory = bpmnPropertySetFactory;
    }

    private static final Set<String>  SUPPORTED_DEF_IDS = new LinkedHashSet<String>() {{
        add(BPMNGraph.ID);
        add(BPMNDiagram.ID);
        add(Task.ID);
        add(StartNoneEvent.ID);
        add(EndNoneEvent.ID);
        add(EndTerminateEvent.ID);
        add(SequenceFlow.ID);
        add(ParallelGateway.ID);
        add(Lane.ID);
    }};
    @Override
    public boolean accepts(final String id) {
        return SUPPORTED_DEF_IDS.contains(id);
    }

    @Override
    public BPMNDefinition build(final String id) {

        if (BPMNGraph.ID.equals(id)) {
            return buildBPMNGraph();
        }
        if (BPMNDiagram.ID.equals(id)) {
            return buildBPMNDiagram();
        }
        if (Task.ID.equals(id)) {
            return buildTask();
        }
        if (StartNoneEvent.ID.equals(id)) {
            return buildStartNoneEvent();
        }
        if (EndNoneEvent.ID.equals(id)) {
            return buildEndNoneEvent();
        }
        if (EndTerminateEvent.ID.equals(id)) {
            return buildEndTerminateEvent();
        }
        if (SequenceFlow.ID.equals(id)) {
            return buildSequenceFlow();
        }
        if (ParallelGateway.ID.equals(id)) {
            return buildParallelGateway();
        }
        if (Lane.ID.equals(id)) {
            return buildLane();
        }
        return null;
    }

    public BPMNGraph buildBPMNGraph() {
        BPMNGraph graph = new BPMNGraph(bpmnPropertySetFactory.buildGeneralSet());
        graph.getGeneral().getName().setValue("My BPMN graph");
        return graph;
    }

    public Lane buildLane() {
        Lane lane = new Lane(bpmnPropertySetFactory.buildGeneralSet(),
                bpmnPropertySetFactory.buildBackgroundSet(),
                bpmnPropertySetFactory.buildFontSet(),
                bpmnPropertyFactory.buildWidth(),
                bpmnPropertyFactory.buildHeight());
        lane.getGeneral().getName().setValue("My lane");
        lane.getBackgroundSet().getBgColor().setValue(Lane.COLOR);
        lane.getBackgroundSet().getBorderSize().setValue(Lane.BORDER_SIZE);
        lane.getWidth().setValue(Lane.WIDTH);
        lane.getHeight().setValue(Lane.HEIGHT);
        return lane;
    }

    public Task buildTask() {
        Task task = new Task(bpmnPropertySetFactory.buildGeneralSet(),
                    bpmnPropertySetFactory.buildBackgroundSet(),
                    bpmnPropertySetFactory.buildFontSet(),
                    bpmnPropertyFactory.buildWidth(),
                    bpmnPropertyFactory.buildHeight());
        task.getGeneral().getName().setValue("My task");
        task.getBackgroundSet().getBgColor().setValue(Task.COLOR);
        task.getBackgroundSet().getBorderSize().setValue(Task.BORDER_SIZE);
        task.getWidth().setValue(Task.WIDTH);
        task.getHeight().setValue(Task.HEIGHT);
        return task;
    }

    public StartNoneEvent buildStartNoneEvent() {
        StartNoneEvent event = new StartNoneEvent(bpmnPropertySetFactory.buildGeneralSet(),
                    bpmnPropertySetFactory.buildBackgroundSet(),
                    bpmnPropertySetFactory.buildFontSet(),
                    bpmnPropertyFactory.buildRadius());
        event.getGeneral().getName().setValue("My start event");
        event.getBackgroundSet().getBgColor().setValue(StartNoneEvent.COLOR);
        event.getRadius().setValue(StartNoneEvent.RADIUS);
        return event;
    }
    

    public EndNoneEvent buildEndNoneEvent() {
        EndNoneEvent event = new EndNoneEvent(bpmnPropertySetFactory.buildGeneralSet(),
                    bpmnPropertySetFactory.buildBackgroundSet(),
                    bpmnPropertySetFactory.buildFontSet(),
                    bpmnPropertyFactory.buildRadius());
        event.getGeneral().getName().setValue("My end event");
        event.getBackgroundSet().getBgColor().setValue(EndNoneEvent.COLOR);
        event.getRadius().setValue(EndNoneEvent.RADIUS);
        return event;
    }

    public EndTerminateEvent buildEndTerminateEvent() {
        EndTerminateEvent endTerminateEvent =  new EndTerminateEvent(bpmnPropertySetFactory.buildGeneralSet(),
                bpmnPropertySetFactory.buildBackgroundSet(),
                bpmnPropertySetFactory.buildFontSet(),
                bpmnPropertyFactory.buildRadius());
        endTerminateEvent.getGeneral().getName().setValue("My terminate event");
        endTerminateEvent.getBackgroundSet().getBgColor().setValue(EndTerminateEvent.COLOR);
        endTerminateEvent.getBackgroundSet().getBorderColor().setValue(EndTerminateEvent.BORDER_COLOR);
        endTerminateEvent.getRadius().setValue(EndTerminateEvent.RADIUS);
        return endTerminateEvent;
    }
    
    public SequenceFlow buildSequenceFlow() {
        SequenceFlow flow = new SequenceFlow(bpmnPropertySetFactory.buildGeneralSet(),
                    bpmnPropertySetFactory.buildBackgroundSet());
        flow.getGeneral().getName().setValue("My sequence flow");
        flow.getBackgroundSet().getBgColor().setValue(SequenceFlow.COLOR);
        flow.getBackgroundSet().getBorderSize().setValue(SequenceFlow.BORDER_SIZE);
        flow.getBackgroundSet().getBorderColor().setValue(SequenceFlow.BORDER_COLOR);
        return flow;
    }

    public ParallelGateway buildParallelGateway() {
        ParallelGateway gateway = new ParallelGateway(bpmnPropertySetFactory.buildGeneralSet(),
                    bpmnPropertySetFactory.buildBackgroundSet(),
                    bpmnPropertySetFactory.buildFontSet(),
                    bpmnPropertyFactory.buildRadius());
        gateway.getGeneral().getName().setValue("My gateway");
        gateway.getBackgroundSet().getBgColor().setValue(ParallelGateway.COLOR);
        gateway.getRadius().setValue(ParallelGateway.RADIUS);
        return gateway;
    }

    public BPMNDiagram buildBPMNDiagram() {
        BPMNDiagram bpmnDiagram = new BPMNDiagram(bpmnPropertySetFactory.buildGeneralSet(),
                    bpmnPropertySetFactory.buildDiagramSet(),
                bpmnPropertySetFactory.buildBackgroundSet(),
                bpmnPropertySetFactory.buildFontSet(),
                bpmnPropertyFactory.buildWidth(),
                bpmnPropertyFactory.buildHeight());
        bpmnDiagram.getGeneral().getName().setValue("My BPMN diagram");
        bpmnDiagram.getBackgroundSet().getBgColor().setValue(BPMNDiagram.COLOR);
        bpmnDiagram.getBackgroundSet().getBorderSize().setValue(BPMNDiagram.BORDER_SIZE);
        bpmnDiagram.getBackgroundSet().getBorderColor().setValue(BPMNDiagram.BORDER_COLOR);
        bpmnDiagram.getWidth().setValue(BPMNDiagram.WIDTH);
        bpmnDiagram.getHeight().setValue(BPMNDiagram.HEIGHT);
        return bpmnDiagram;
    }
}