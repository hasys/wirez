package org.wirez.core.client.canvas.controls.toolbox.command;

import com.ait.lienzo.client.core.animation.*;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.wirez.core.client.shape.Shape;
import org.wirez.core.client.shape.view.ShapeGlyph;
import org.wirez.lienzo.palette.MiniPalette;

import javax.enterprise.context.Dependent;

@Dependent
public class AddNodeCommandView implements AddNodeCommand.View{

    private AddNodeCommand presenter ;
    private final MiniPalette miniPallete = new MiniPalette();
    
    @Override
    public AddNodeCommand.View init(final AddNodeCommand presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public AddNodeCommand.View showPalette(final Shape shape, 
                                           final int x, 
                                           final int y,
                                           final ShapeGlyph... glyphs) {
        final WiresShape wiresShape = (WiresShape) shape.getShapeView();
        showMiniPalette( wiresShape, x, y, glyphs );
        return this;
    }

    @Override
    public AddNodeCommand.View clear() {
        removeMiniPalette();
        return this;
    }
    
    private void showMiniPalette(final WiresShape shape, 
                                 final int x, 
                                 final int y,
                                 final ShapeGlyph... glyphs) {
        
        if ( null != shape && null != glyphs && glyphs.length > 0 ) {
            
            final IPrimitive<?>[] items = new IPrimitive[ glyphs.length ];
            for ( int c = 0; c < glyphs.length; c++ ) {
                items[c] = glyphs[c].getGroup();
            }

            shape.getWiresLayer().getLayer().add( miniPallete );

            miniPallete.setPadding(10)
                    .setIconSize(16)
                    .setX(x)
                    .setY(y)
                    .build( items )
                    .setAlpha(0)
                    .animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)), 500, new AnimationCallback());
        
        } else {
            
            miniPallete.clear();
            
        }
        
        
    }
    
    private void removeMiniPalette() {
        
        miniPallete.animate(AnimationTweener.LINEAR, AnimationProperties.toPropertyList(AnimationProperty.Properties.ALPHA(1)), 500, new AnimationCallback() {
            @Override
            public void onClose(IAnimation animation, IAnimationHandle handle) {
                super.onClose(animation, handle);
                miniPallete.clear();
            }
        });
        
    }
    
}
