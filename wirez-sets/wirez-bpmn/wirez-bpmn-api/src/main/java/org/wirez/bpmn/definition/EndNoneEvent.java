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

package org.wirez.bpmn.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.livespark.formmodeler.metaModel.FieldDef;
import org.livespark.formmodeler.metaModel.Slider;
import org.wirez.bpmn.definition.property.Radius;
import org.wirez.bpmn.definition.property.background.BackgroundSet;
import org.wirez.bpmn.definition.property.font.FontSet;
import org.wirez.bpmn.definition.property.general.BPMNGeneral;
import org.wirez.bpmn.definition.property.simulation.ThrowEventAttributes;
import org.wirez.bpmn.shape.proxy.EndNoneEventShapeProxy;
import org.wirez.core.definition.annotation.Description;
import org.wirez.core.definition.annotation.Shape;
import org.wirez.core.definition.annotation.definition.*;
import org.wirez.core.definition.factory.Builder;
import org.wirez.core.graph.Node;
import org.wirez.shapes.factory.BasicShapesFactory;

import java.util.HashSet;
import java.util.Set;

@Portable
@Bindable
@Definition( type = Node.class, builder = EndNoneEvent.EndNoneEventBuilder.class )
@Shape( factory = BasicShapesFactory.class, proxy = EndNoneEventShapeProxy.class )
public class EndNoneEvent implements BPMNDefinition {

    @Category
    public static final transient String category = Categories.END_EVENTS;

    @Title
    public static final transient String title = "End Event";

    @Description
    public static final transient String description = "Untyped end event";

    @PropertySet
    private BPMNGeneral general;

    @PropertySet
    private BackgroundSet backgroundSet;

    @PropertySet
    private ThrowEventAttributes throwEventAttributes;

    @PropertySet
    private FontSet fontSet;

    @Property
    @FieldDef(label = "Radius", property = "value")
    @Slider( min = 25.0, max = 50.0, step = 1, precision = 0.0 )
    private Radius radius;

    @Labels
    private final Set<String> labels = new HashSet<String>() {{
        add( "all" );
        add( "sequence_end" );
        add( "to_task_event" );
        add( "from_task_event" );
        add( "fromtoall" );
        add( "choreography_sequence_end" );
        add( "Endevents_all" );
        add( "EndEventsMorph" );
    }};

    @NonPortable
    public static class EndNoneEventBuilder implements Builder<EndNoneEvent> {

        public static final transient String COLOR = "#ff7b5e";
        public static final Double BORDER_SIZE = 2d;
        public static final String BORDER_COLOR = "#000000";
        public static final Double RADIUS = 14d;

        @Override
        public EndNoneEvent build() {
            return new EndNoneEvent(  new BPMNGeneral( "End" ),
                    new BackgroundSet( COLOR, BORDER_COLOR, BORDER_SIZE ),
                    new FontSet(),
                    new ThrowEventAttributes(),
                    new Radius( RADIUS ) );
        }

    }
    
    public EndNoneEvent()  {

    }

    public EndNoneEvent(@MapsTo("general") BPMNGeneral general,
                 @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                 @MapsTo("fontSet") FontSet fontSet,
                 @MapsTo("throwEventAttributes") ThrowEventAttributes throwEventAttributes,
                 @MapsTo("radius") Radius radius) {
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.throwEventAttributes = throwEventAttributes;
        this.radius = radius;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public Radius getRadius() {
        return radius;
    }

    public BPMNGeneral getGeneral() {
        return general;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public ThrowEventAttributes getThrowEventAttributes() {
        return throwEventAttributes;
    }

    public void setGeneral( BPMNGeneral general ) {
        this.general = general;
    }

    public void setBackgroundSet( BackgroundSet backgroundSet ) {
        this.backgroundSet = backgroundSet;
    }

    public void setThrowEventAttributes( ThrowEventAttributes throwEventAttributes ) {
        this.throwEventAttributes = throwEventAttributes;
    }

    public void setFontSet( FontSet fontSet ) {
        this.fontSet = fontSet;
    }

    public void setRadius( Radius radius ) {
        this.radius = radius;
    }
}
