package org.wirez.basicset.definition.factory;

import org.wirez.basicset.definition.property.*;
import org.wirez.basicset.definition.property.background.BgColor;
import org.wirez.basicset.definition.property.background.BorderColor;
import org.wirez.basicset.definition.property.background.BorderSize;
import org.wirez.basicset.definition.property.font.FontBorderSize;
import org.wirez.basicset.definition.property.font.FontColor;
import org.wirez.basicset.definition.property.font.FontFamily;
import org.wirez.basicset.definition.property.font.FontSize;
import org.wirez.core.definition.factory.BindableModelFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.LinkedHashSet;
import java.util.Set;

@ApplicationScoped
public class BasicPropertyFactory extends BindableModelFactory<Object> {

    private static final Set<Class<?>> SUPPORTED_PROP_CLASSES = new LinkedHashSet<Class<?>>() {{
        add(Name.class);
        add(BgColor.class);
        add(BorderColor.class);
        add(BorderSize.class);
        add(FontBorderSize.class);
        add(FontColor.class);
        add(FontFamily.class);
        add(FontSize.class);
        add(Height.class);
        add(Width.class);
        add(Radius.class);
        add(IconType.class);
    }};

    public BasicPropertyFactory() {
    }

    @Override
    public Set<Class<?>> getAcceptedClasses() {
        return SUPPORTED_PROP_CLASSES;
    }

    @Override
    public Object build(final Class<?> clazz) {
        if (Name.class.equals(clazz)) {
            return buildName();
        }
        if (BgColor.class.equals(clazz)) {
            return buildBgColor();
        }
        if (BorderColor.class.equals(clazz)) {
            return buildBorderColor();
        }
        if (BorderSize.class.equals(clazz)) {
            return buildBorderSize();
        }
        if (FontBorderSize.class.equals(clazz)) {
            return buildFontBorderSize();
        }
        if (FontColor.class.equals(clazz)) {
            return buildFontColor();
        }
        if (FontFamily.class.equals(clazz)) {
            return buildFontFamily();
        }
        if (FontSize.class.equals(clazz)) {
            return buildFontSize();
        }
        if (Height.class.equals(clazz)) {
            return buildHeight();
        }
        if (Width.class.equals(clazz)) {
            return buildWidth();
        }
        if (Radius.class.equals(clazz)) {
            return buildRadius();
        }
        if (IconType.class.equals(clazz)) {
            return buildIconType();
        }

        throw new RuntimeException( "This factory should provide a property for [" + clazz + "]" );
    }

    public Name buildName() {
        return new Name();
    }

    public BgColor buildBgColor() {
        return new BgColor();
    }

    public BorderColor buildBorderColor() {
        return new BorderColor();
    }

    public BorderSize buildBorderSize() {
        return new BorderSize();
    }

    public FontBorderSize buildFontBorderSize() {
        return new FontBorderSize();
    }

    public FontColor buildFontColor() {
        return new FontColor();
    }

    public FontFamily buildFontFamily() {
        return new FontFamily();
    }

    public FontSize buildFontSize() {
        return new FontSize();
    }
    
    public Height buildHeight() {
        return new Height();
    }

    public Width buildWidth() {
        return new Width();
    }

    public Radius buildRadius() {
        return new Radius();
    }
    public IconType buildIconType() {
        return new IconType();
    }
    

}
