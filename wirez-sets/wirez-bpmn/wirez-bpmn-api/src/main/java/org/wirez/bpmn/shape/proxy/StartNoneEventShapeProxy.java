package org.wirez.bpmn.shape.proxy;

import org.wirez.bpmn.definition.StartNoneEvent;
import org.wirez.shapes.proxy.CircleProxy;

public final class StartNoneEventShapeProxy implements CircleProxy<StartNoneEvent> {
    
    @Override
    public double getRadius( final StartNoneEvent element ) {
        return element.getRadius().getValue();
    }

    @Override
    public String getBackgroundColor( final StartNoneEvent element ) {
        return element.getBackgroundSet().getBgColor().getValue();
    }

    @Override
    public String getBorderColor( final StartNoneEvent element ) {
        return element.getBackgroundSet().getBorderColor().getValue();
    }

    @Override
    public double getBorderSize( final StartNoneEvent element ) {
        return element.getBackgroundSet().getBorderSize().getValue();
    }

    @Override
    public String getFontFamily( final StartNoneEvent element ) {
        return element.getFontSet().getFontFamily().getValue();
    }

    @Override
    public String getFontColor( final StartNoneEvent element ) {
        return element.getFontSet().getFontColor().getValue();
    }

    @Override
    public double getFontSize( final StartNoneEvent element ) {
        return element.getFontSet().getFontSize().getValue();
    }

    @Override
    public String getNamePropertyValue( final StartNoneEvent element ) {
        return element.getGeneral().getName().getValue();
    }

    @Override
    public double getFontBorderSize( final StartNoneEvent element ) {
        return element.getFontSet().getFontBorderSize().getValue();
    }

    @Override
    public String getGlyphBackgroundColor() {
        return StartNoneEvent.COLOR;
    }

    @Override
    public String getDescription() {
        return StartNoneEvent.description;
    }
}
