package org.wirez.core.client.canvas.util;

import org.wirez.core.api.util.BoundsIndexer;
import org.wirez.core.client.canvas.AbstractCanvas;
import org.wirez.core.client.shape.Shape;

public interface CanvasBoundsIndexer extends BoundsIndexer<Shape<?>> {

    CanvasBoundsIndexer forCanvas( AbstractCanvas canvas );
    
}
