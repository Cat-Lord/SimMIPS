/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.main;
	
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.reactfx.value.Val;

import java.util.function.IntFunction;


/**
 * Given the line number, return a node (graphic) to display to the left of a line.
 * Code taken from: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/lineindicator/ArrowFactory.java
 */
public class ArrowFactory implements IntFunction<Node> {
    private final ObservableValue<Integer> shownLine;

    ArrowFactory(ObservableValue<Integer> shownLine) {
        this.shownLine = shownLine;
    }

    @Override
    public Node apply(int lineNumber) {
        Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);
        triangle.setFill(Color.GREEN);

        ObservableValue<Boolean> visible = Val.map(shownLine, sl -> sl == lineNumber);

        triangle.visibleProperty().bind(
            Val.flatMap(triangle.sceneProperty(), scene -> {
                return scene != null ? visible : Val.constant(false);
        }));

        return triangle;
    }
}
