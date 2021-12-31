package sk.catheaven.model;


import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import sk.catheaven.core.Style;
import sk.catheaven.core.cpu.Component;

public class DatapathComponent {
    private final Component component;
    private Node node;
    private Style style;
    private String label;

    public DatapathComponent(Component component) {
        this.component = component;
        this.style = component.getStyle();
        this.label = component.getLabel();
    }

    /**
     * Allows subclasses to define their own shape.
     * @return Shape of the component
     */
    protected Shape getShape() {
        return style.isDrawDistinct() ? new Ellipse(style.getWidth(), style.getHeight())
                                      : new Rectangle(style.getWidth(), style.getHeight());
    }

    /**
     * Provides a way to override in subclasses. Should be called from subclass and not created individually to avoid
     * missing or inconsistent styling.
     * @return Text node with current component label.
     */
    protected Text getTextNode() {
        Text text = new Text(getLabel());
        text.getStyleClass().add("component-labeled-text");
        return text;
    }

    /**
     * Creates node of the component with a text label.
     * @return Node with shape and text on top of it
     */
    public Node getNode() {
        if (this.node == null) {
            final StackPane layout = new StackPane();
            layout.setMaxWidth(style.getWidth());
            layout.setAlignment(Pos.CENTER_LEFT);

            layout.setLayoutX(style.getX());
            layout.setLayoutY(style.getY());

            final Shape shape = getShape();
            shape.setStroke(Paint.valueOf("black"));
            shape.setFill(Paint.valueOf(getStyle().getColour()));
            layout.getChildren().add(shape);

            final Text text = getTextNode();
            text.setText(" ".concat(this.label));   // visual space in front

            // only add the label if it fits 'into' the shape
            if (text.getLayoutBounds().getWidth() < shape.getLayoutBounds().getWidth()) {
                layout.getChildren().add(text);
                text.toFront();        // call *after* adding to the layout
            }
            this.node = layout;
        }
        return node;
    }

    public String getLabel() {
        return "";
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(StyleImpl style) {
        this.style = style;
    }

}
