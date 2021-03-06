package org.wirez.core.client.shape.view;

public interface HasTitle<T> {

    enum Position {
        CENTER, LEFT, RIGHT, TOP, BOTTOM;
    }
    
    T setTitle(String title);
    
    T setTitlePosition(Position position);

    T setTitleStrokeColor(String color);

    T setTitleFontFamily(String fontFamily);
    
    T setTitleFontSize(double fontSize);

    T setTitleStrokeWidth(double strokeWidth);
    
    T moveTitleToTop();
    
    T refreshTitle();
    
}
