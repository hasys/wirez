package org.wirez.shapes.proxy.icon;

public interface DynamicIconProxy<W> extends IconProxy<W> {
    
    ICONS getIcon(W definition);
    
}
