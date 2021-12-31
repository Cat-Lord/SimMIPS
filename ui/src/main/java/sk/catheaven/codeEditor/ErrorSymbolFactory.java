package sk.catheaven.codeEditor;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.reactfx.value.Val;

import java.util.function.IntFunction;

public class ErrorSymbolFactory implements IntFunction<Node> {
    private final ObservableList<Integer> shownLine;

    public ErrorSymbolFactory(ObservableList<Integer> shownLine) {
        this.shownLine = shownLine;
    }

    @Override
    public Node apply(int lineNumber) {
        Circle triangle = new Circle(5,5,4);
        triangle.setFill(Color.RED);

        ObservableValue<Boolean> visible = new SimpleBooleanProperty(shownLine.contains(lineNumber));

        triangle.visibleProperty().bind(
                Val.flatMap(triangle.sceneProperty(),
                        scene -> scene != null ? visible : Val.constant(false)
                ));

        return triangle;
    }
}
