package org.wirez.core.client.components.palette;

import org.wirez.core.client.canvas.Layer;

public interface Palette<T, V> {

    interface CloseCallback {

        void onClose();

    }
    
    interface ItemHoverCallback {

        void onItemHover(int index, double x, double y);

    }

    interface ItemOutCallback {

        void onItemOut(int index);

    }

    interface ItemMouseDownCallback {

        void onItemMouseDown(int index, int x, int y);

    }

    interface ItemClickCallback {

        void onItemClick(int index, int x, int y);

    }

    T setCloseCallback(CloseCallback callback);

    T setItemHoverCallback(ItemHoverCallback callback);

    T setItemOutCallback(ItemOutCallback callback);

    T setItemMouseDownCallback(ItemMouseDownCallback callback);

    T setItemClickCallback(ItemClickCallback callback);
    
    T setX(int x);

    T setY(int y);

    T setIconSize(int iconSize);

    T setPadding(int padding);

    T show( Layer layer, V[]items );
    
    T clear();
    
}
