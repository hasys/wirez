package org.wirez.core.client.components.drag;

import org.wirez.core.client.canvas.Layer;

public interface DragProxy<T> {

    interface Callback {

        void onMove(int x, int y);

        void onComplete(int x, int y);

    }
    
    void create(Layer layer, int x, int y, T item, Callback callback);
    
}
