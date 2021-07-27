package sk.catheaven.utils;

import java.util.Locale;

public class Style {
    private final static String DEFAULT_COLOUR = "#98999a";
    
    private int x;
    private int y;
    private int width;
    private int height;
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
    
    @Override
    public String toString() {
        return "Style {" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", colour='" + colour.toUpperCase(Locale.ROOT) + '\'' +
                '}';
    }
}
