package org.wirez.core.client.canvas.controls.toolbox.command;

import com.ait.lienzo.client.core.shape.Shape;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.wirez.core.api.graph.Element;
import org.wirez.core.client.canvas.AbstractCanvasHandler;
import org.wirez.core.client.components.popup.PopupBox;
import org.wirez.core.client.util.SVGUtils;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class NameToolboxCommand implements ToolboxCommand, IsWidget {

    PopupBox<AbstractCanvasHandler, Element> toolbox;

    private final Shape<?> icon;
    
    @Inject
    public NameToolboxCommand(PopupBox<AbstractCanvasHandler, Element> toolbox) {
        this.toolbox = toolbox;
        this.icon = SVGUtils.createSVGIcon(SVGUtils.getTextEditIcon());
    }

    @Override
    public Shape<?> getIcon() {
        return icon;
    }

    @Override
    public String getTitle() {
        return "Edit name";
    }

    @Override
    public void execute(final Context context, 
                        final Element element) {
        toolbox.initialize(context.getCanvasHandler());
        toolbox.show(element, context.getX(), context.getY());
    }

    @Override
    public Widget asWidget() {
        return toolbox.asWidget();
    }
}
