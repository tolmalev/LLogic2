package ru.llogic.ui;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import ru.llogic.core.PointState;

/**
 * @author tolmalev
 */
public class LinesCanvas extends Canvas {

    public void setDocumentManager(DocumentManager documentManager) {
        this.documentManager = documentManager;

        widthProperty().bind(((Region)getParent()).widthProperty());
        heightProperty().bind(((Region)getParent()).heightProperty());
    }

    private DocumentManager documentManager;

    public LinesCanvas() {
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    public void draw() {
        if (documentManager == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }

        GraphicsContext context2D = getGraphicsContext2D();
        context2D.clearRect(0, 0, getWidth(), getHeight());

        getUiConnections().forEach(this::drawConnection);
    }

    private List<UiConnection> getUiConnections() {
        return documentManager
                .getCalculationManager()
                .getAllConnections()
                .stream()
                .map(c -> new UiConnection(
                        documentManager.getPosition(c.getFrom()),
                        documentManager.getPosition(c.getTo()),
                        c.getFrom().getState()
                ))
                .collect(Collectors.toList());
    }

    private void drawConnection(UiConnection connection) {
        GraphicsContext context2D = getGraphicsContext2D();

        context2D.setStroke(getLineColor(connection.getState()));
        context2D.setLineWidth(getLineWidth(connection.getState()));

        context2D.strokeLine(
                connection.getStart().getX(), connection.getStart().getY(),
                connection.getEnd().getX(), connection.getEnd().getY()
        );
    }

    private static double getLineWidth(PointState state) {
        return state == PointState.Z
                ? 1
                : 2;
    }

    private static Paint getLineColor(PointState state) {
        switch (state) {
            case HIGH: return Color.GREEN;
            case LOW: return Color.RED;
            default: return Color.GREY;
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
}
