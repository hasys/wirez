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

package org.wirez.bpmn.client.shape;

import org.wirez.bpmn.definition.BPMNDiagram;
import org.wirez.bpmn.definition.property.background.BackgroundSet;
import org.wirez.bpmn.definition.property.font.FontSet;
import org.wirez.bpmn.definition.property.general.BPMNGeneral;
import org.wirez.core.graph.Edge;
import org.wirez.core.graph.Node;
import org.wirez.core.graph.content.view.View;
import org.wirez.core.client.shape.MutationContext;
import org.wirez.core.client.shape.view.HasTitle;
import org.wirez.shapes.client.view.RectangleView;

public class BPMNDiagramShape extends BPMNBasicShape<BPMNDiagram, RectangleView> {

    public BPMNDiagramShape(final RectangleView view) {
        super(view);
        getShapeView().setTitlePosition(HasTitle.Position.TOP);
    }

    @Override
    public void applyProperties(final Node<View<BPMNDiagram>, Edge> element, final MutationContext mutationContext) {
        super.applyProperties(element, mutationContext);

        // Size.
        final Double w = element.getContent().getDefinition().getWidth().getValue();
        final Double h = element.getContent().getDefinition().getHeight().getValue();
        _applyWidthAndHeight(element, w, h, mutationContext);

    }

    @Override
    public void afterDraw() {
        super.afterDraw();
        getShapeView().setFillAlpha(0.8);
    }

    @Override
    protected BPMNGeneral getBPMBpmnGeneralSet(final Node<View<BPMNDiagram>, Edge> element) {
        return element.getContent().getDefinition().getGeneral();
    }


    @Override
    protected BackgroundSet getBackgroundSet(final Node<View<BPMNDiagram>, Edge> element) {
        return element.getContent().getDefinition().getBackgroundSet();
    }

    @Override
    protected FontSet getFontSet(final Node<View<BPMNDiagram>, Edge> element) {
        return element.getContent().getDefinition().getFontSet();
    }

    @Override
    public String toString() {
        return "BPMNDiagramShape{}";
    }

}
