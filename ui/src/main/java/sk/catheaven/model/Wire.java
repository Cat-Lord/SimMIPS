package sk.catheaven.model;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.shape.Polyline;
import org.controlsfx.control.PopOver;
import sk.catheaven.core.Coordinates2D;
import sk.catheaven.core.cpu.Connector;
import sk.catheaven.primeWindow.PopOverFactory;

public class Wire {
    private static final double THIN_LINE = 0.5;
    private static final double NORMAL_LINE = 1;
    private static final double THICK_LINE = 3;

    private final Label popoverContent;

    private final Polyline line;
    private final Polyline clickLine;		// this is the copy of the original line, but thicker, to allow easier clicking
    private final PopOver popOver;

    public Wire(Connector connector) {
        double[] nodesArray = new double[connector.getNodes().size() * 2];
        int index = 0;

        for (Coordinates2D node : connector.getNodes()) {
            nodesArray[index++] = node.getX();
            nodesArray[index++] = node.getY();
        }

        line = new Polyline(nodesArray);
        switch (connector.getWireType()) {
            case THIN   -> line.setStrokeWidth(THIN_LINE);
            case NORMAL -> line.setStrokeWidth(NORMAL_LINE);
            case THICK  -> line.setStrokeWidth(THICK_LINE);
        }

        clickLine = new Polyline(nodesArray);
        clickLine.setStrokeWidth((line.getStrokeWidth() +2) * 2);
        clickLine.setOpacity(0);

        popoverContent = new Label();
        popoverContent.setPadding(new Insets(15));

        popOver = PopOverFactory.get();
        popOver.setContentNode(popoverContent);
    }

    public Polyline getLine() {
        return line;
    }

    public Polyline getClickLine() {
        return clickLine;
    }

}
