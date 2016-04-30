package org.wirez.lienzo.toolbox;

import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.wirez.lienzo.toolbox.builder.AbstractBuilder;
import org.wirez.lienzo.toolbox.grid.GridToolbox;

public class StaticToolbox implements GridToolbox {

    private StaticToolbox() {
    }

    @Override
    public void show() {

    }

    @Override
    public void remove() {

    }

    @Override
    public void hide() {

    }

    public static class Builder extends AbstractBuilder {

        public Builder(WiresShape shape) {
            super(shape);
        }

//        public Builder(int x, int y) {
//            super(x, y);
//        }

        @Override
        public HoverToolbox register() {
            return null;
        }
    }
}
