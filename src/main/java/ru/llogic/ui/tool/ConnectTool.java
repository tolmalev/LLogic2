package ru.llogic.ui.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import ru.llogic.core.Element;
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

    private Point startPoint = null;
    private Point endPoint = null;
    private Point2D currentPosition = null;

    public ConnectTool(DocumentManager documentManager, Pane elementsPane) {
        super(documentManager);
        this.elementsPane = elementsPane;
        this.connectionCanvas = CanvasUtils.createResisableCanvas(elementsPane, this::draw);
        forAllChildren(node -> {
            node.layoutXProperty().addListener(e -> draw());
            node.layoutYProperty().addListener(e -> draw());
        });

        elementsPane.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            if (event.getTarget() instanceof ElementWidget) {
                if (isActive()) {
                    startPoint = findPoint(event);
                    if (startPoint != null) {
                        draw();
                    }
                }
            } else {
                startPoint = null;
                draw();
            }
        });

        elementsPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isActive()) {
                if (startPoint != null) {
                    currentPosition = new Point2D(event.getX(), event.getY());
                    endPoint = findPoint(event);
                    if (endPoint != null && endPoint.equals(startPoint)) {
                        endPoint = null;
                    }

                    draw();
                }
            }
        });

        elementsPane.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (isActive()) {
                if (startPoint != null && endPoint != null && !startPoint.equals(endPoint)) {
                    documentManager.getCalculationManager().addConnection(startPoint, endPoint);
                }
                startPoint = null;
                endPoint = null;
                currentPosition = null;

                draw();
            }

        });
    }

    private Point findPoint(MouseEvent event) {
        for (Node child : elementsPane.getChildren()) {
            if (child instanceof ElementWidget) {
                Element<?> element = ((ElementWidget) child).getElement();
                for (Point point : element.getPoints()) {
                    Point2D position = documentManager.getPosition(point);

                    if (position.distance(event.getX(), event.getY()) < 6) {
                        return point;
                    }
                }
            }
        }
        return null;
    }

    private void draw() {
        GraphicsContext c = connectionCanvas.getGraphicsContext2D();
        c.clearRect(0, 0, connectionCanvas.getWidth(), connectionCanvas.getHeight());

        ArrayList<Point> points = new ArrayList<>();
        if (startPoint != null) points.add(startPoint);
        if (endPoint != null) points.add(endPoint);

        for (Point point : points) {
            Point2D position = documentManager.getPosition(point);

            c.setStroke(Color.GREEN);
            c.strokeOval(position.getX() - 3, position.getY() - 3, 6, 6);

            if (currentPosition != null) {
                c.strokeLine(position.getX(), position.getY(), currentPosition.getX(), currentPosition.getY());
            }
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        startPoint = null;
        endPoint = null;
        currentPosition = null;
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
