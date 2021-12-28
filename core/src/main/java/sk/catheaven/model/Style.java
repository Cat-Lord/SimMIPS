package sk.catheaven.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Locale;

/**
 * Styling of a component - position, dimensions and colour.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Style {
    private final static String DEFAULT_COLOUR = "#98999a";
    
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean drawDistinct = false;
    private String colour = DEFAULT_COLOUR;
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public String getColour() {
        return colour;
    }
    
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Determines if an applied style with this feature enabled should be drawn distinctly from others.
     * @return True if the applied style should visually differ from other style.
     */
    public boolean isDrawDistinct() {
        return drawDistinct;
    }

    public void setDrawDistinct(boolean drawDistinct) {
        this.drawDistinct = drawDistinct;
    }

    @Override
    public String toString() {
        return "Style {" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", colour='" + colour.toUpperCase(Locale.ROOT) + '\'' +
                ", distinct= " + drawDistinct +
                '}';
    }
}
