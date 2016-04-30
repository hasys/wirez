package org.wirez.lienzo.toolbox;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.wirez.lienzo.toolbox.builder.On;

public class Toolboxes {
    public static On hoverToolBoxFor(WiresShape shape) {
        return new HoverToolbox.HoverToolboxBuilder(shape);
    }

    public static On toolBoxFor(WiresShape shape) {
        return new StaticToolbox.Builder(shape);
    }

//    public static ButtonGrid toolBoxAt(int x, int y) {
//        return new StaticToolbox.Builder(x, y);
//    }
}
