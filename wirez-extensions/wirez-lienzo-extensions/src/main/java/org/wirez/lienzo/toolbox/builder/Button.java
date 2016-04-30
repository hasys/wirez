package org.wirez.lienzo.toolbox.builder;


import org.wirez.lienzo.toolbox.ToolboxButton;
import org.wirez.lienzo.toolbox.event.ToolboxButtonEventHandler;

public interface Button {
    interface WhenReady {
        void whenReady(ToolboxButton button);
    }

    Button setClickHandler(ToolboxButtonEventHandler handler);

    Button setDragEndHandler(ToolboxButtonEventHandler handler);
    
    Button whenReady(WhenReady callback);

    ButtonsOrRegister end();

}
