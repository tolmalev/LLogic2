package ru.llogic.ui.tool;

import java.util.function.Consumer;

import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import ru.llogic.core.Point;
import ru.llogic.ui.CanvasUtils;
import ru.llogic.ui.DocumentManager;
import ru.llogic.ui.widget.ElementWidget;

/**
 * @author tolmalev
 */
public class ConnectTool extends ToolBase {
    private final Pane elementsPane;
    private final Canvas connectionCanvas;

    public ConnectTool(DocumentManager documentManager, Pane elementsPane) {
        super(documentManager);
        this.elementsPane = elementsPane;
        this.connectionCanvas = CanvasUtils.createResisableCanvas(elementsPane, this::draw);
        forAllChildren(node -> {
            node.layoutXProperty().addListener(e -> draw());
            node.layoutYProperty().addListener(e -> draw());
        });
    }

    private void draw() {
        GraphicsContext c = connectionCanvas.getGraphicsContext2D();
        c.clearRect(0, 0, connectionCanvas.getWidth(), connectionCanvas.getHeight());

        for (Node node : elementsPane.getChildren()) {
            if (node instanceof ElementWidget) {
                for (Object point : ((ElementWidget) node).getElement().getPoints()) {
                    Point2D position = documentManager.getPosition((Point) point);

                    c.setStroke(Color.GREEN);
                    c.strokeOval(position.getX() - 3, position.getY() - 3, 6, 6);
                }
            }
        }
    }

    private void forAllChildren(Consumer<Node> consumer) {
        elementsPane.getChildren().forEach(consumer);
        elementsPane.getChildren().addListener(
                (ListChangeListener<Node>) c -> {
                    while (c.next()) {
                        c.getAddedSubList().forEach(consumer);
                    }
                }
        );
    }
}
