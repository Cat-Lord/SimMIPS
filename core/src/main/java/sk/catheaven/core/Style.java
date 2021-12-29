package sk.catheaven.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.model.StyleImpl;

@JsonDeserialize(as = StyleImpl.class)
public interface Style {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    String getColour();

    boolean isDrawDistinct();
}
