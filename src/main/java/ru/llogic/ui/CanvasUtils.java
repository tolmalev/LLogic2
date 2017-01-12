package ru.llogic.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * @author tolmalev
 */
public class CanvasUtils {
    public static Canvas createResisableCanvas(Pane node, Runnable draw) {
        Canvas canvas = new Canvas();
        canvas.setMouseTransparent(true);
        canvas.widthProperty().bind(node.widthProperty());
        canvas.heightProperty().bind(node.heightProperty());
        canvas.toFront();

        canvas.widthProperty().addListener(evt -> draw.run());
        canvas.heightProperty().addListener(evt -> draw.run());

        node.getChildren().add(canvas);

        return canvas;
    }
}
