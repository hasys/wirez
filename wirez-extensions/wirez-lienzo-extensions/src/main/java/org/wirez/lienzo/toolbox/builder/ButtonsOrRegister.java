package org.wirez.lienzo.toolbox.builder;

import com.ait.lienzo.client.core.shape.Shape;
import org.wirez.lienzo.toolbox.ToolboxButton;

public interface ButtonsOrRegister extends Register {
    ButtonsOrRegister add(ToolboxButton button);

    Button add(Shape<?> shape);
}
