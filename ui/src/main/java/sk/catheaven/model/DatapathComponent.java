package sk.catheaven.model;


import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

@org.springframework.stereotype.Component
public class DatapathComponent {
    private sk.catheaven.core.Component component;
    private Shape shape;
    private Style style;
    private String label;

    /**
     * Allows subclasses to define their own shape.
     * @return Shape of the component
     */
    protected Shape getShape() {
        return null;    // todo
//        return style.getIsDrawDistinct() ? new Ellipse(style.getWidth(), style.getHeight())
//                                        : new Rectangle(style.getWidth(), style.getHeight());
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
        final StackPane layout = new StackPane();
        layout.setLayoutX(style.getX());
        layout.setLayoutY(style.getY());

        final Shape shape = getShape();
        shape.setStroke(Paint.valueOf("black"));
        shape.setFill(Paint.valueOf(getStyle().getColour()));
        layout.getChildren().add(shape);

        final Text text = getTextNode();
        layout.getChildren().add(text);
        text.toFront();		// call *after* adding to the layout

        return new Group(layout);
    }

    public String getLabel() {
        return "";
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }
}
