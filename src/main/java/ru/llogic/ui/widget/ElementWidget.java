package ru.llogic.ui.widget;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.EventTarget;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.core.Element;
import ru.llogic.core.Point;
import ru.llogic.ui.GridUtils;

/**
 * @author tolmalev
 */
public abstract class ElementWidget<T extends Element> extends Canvas {
    private static final Logger logger = LogManager.getLogger(ElementWidget.class);

    protected final T element;

    protected ElementWidget(T element) {
        super();
        this.element = element;

        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
        getStyleClass().addListener((ListChangeListener<String>) c -> draw());

        setWidth(getWidthInPixels());
        setHeight(getHeightInPixels());

        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (clickToMeOrMyChildren(event) && event.getClickCount() == 2) {
                onDoubleClick(event);
            }
        });
    }

    protected void redrawOnPointChange(Point point) {
        point.addStateChangeListener(state -> Platform.runLater(this::draw));
    }

    public double getWidthInPixels() {
        return GridUtils.gridSize(widthCells());
    }

    public double getHeightInPixels() {
        return GridUtils.gridSize(heightCells());
    }

    public void draw() {
        GraphicsContext c = getGraphicsContext2D();
        c.clearRect(0, 0, getWidth(), getHeight());

        drawPoints();
        drawCenter();
//        drawBorder();
    }

    protected void drawCenter() {
        GraphicsContext c = getGraphicsContext2D();

        c.setFill(Color.GRAY);
        Bounds bounds = getCenterBounds();
        c.fillRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    protected Bounds getCenterBounds() {
        double minX = 0;
        double minY = 0;
        double maxX = getWidth();
        double maxY = getHeight();

        if (leftPointsCount() > 0) minX += GridUtils.gridSize(1);
        if (topPointsCount() > 0) minY += GridUtils.gridSize(1);
        if (rightPointsCount() > 0) maxX -= GridUtils.gridSize(1);
        if (bottomPointsCount() > 0) maxY -= GridUtils.gridSize(1);

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    protected void drawPoints() {
        GraphicsContext c = getGraphicsContext2D();

        c.setFill(Color.BLACK);
        c.setLineWidth(1);
        c.setStroke(Color.BLACK);

        Bounds bounds = getCenterBounds();
        for (Point point : ((List<Point>) element.getPoints())) {
            Point2D position = getPointPosition(point);
            c.fillOval(position.getX() - 2, position.getY() - 2, 4, 4);

            double lineX, lineY;
            if(position.getX() < bounds.getMinX()) {
                lineY = position.getY();
                lineX = bounds.getMinX();
            } else if (position.getX() > bounds.getMaxX() ) {
                lineY = position.getY();
                lineX = bounds.getMaxX();
            } else if(position.getY() < bounds.getMinY()) {
                lineX = position.getX();
                lineY = bounds.getMinY();
            } else {
                lineX = position.getX();
                lineY = bounds.getMaxY();
            }


            c.strokeLine(position.getX(), position.getY(), lineX, lineY);
        }
    }

    protected void drawBorder() {
        GraphicsContext c = getGraphicsContext2D();

        if (getStyleClass().contains("selected")) {
            c.setStroke(Color.rgb(53, 53, 255));
            c.setLineWidth(2);

            double dx = getWidthInPixels() / 5;
            double dy = getHeightInPixels() / 5;

            c.strokeLine(0, 0, dx, 0);
            c.strokeLine(0, getHeight(), dx, getHeight());
            c.strokeLine(getWidth() - dx, 0, getWidth(), 0);
            c.strokeLine(getWidth() - dx, getHeight(), getWidth(), getHeight());

            c.strokeLine(0, 0, 0, dy);
            c.strokeLine(getWidth(), 0, getWidth(), dy);
            c.strokeLine(0, getHeight() - dy, 0, getHeight());
            c.strokeLine(getWidth(), getHeight() - dy, getWidth(), getHeight());
        }
    }

    private boolean clickToMeOrMyChildren(MouseEvent event) {
        // I'm not sure, it's necessary
        EventTarget now = event.getTarget();
        while(now != null && now instanceof Node) {
            if (now.equals(this)) {
                return true;
            }
            now = ((Node) now).getParent();
        }
        return false;
    }


    protected void onDoubleClick(MouseEvent event) {
    }

    public abstract Point2D getPointPosition(Point point);

    public int leftPointsCount() {
        return 0;
    }

    public int topPointsCount() {
        return 0;
    }

    public int rightPointsCount() {
        return 0;
    }

    public int bottomPointsCount() {
        return 0;
    }

    public int centerWidthCells() {
        return Math.max(3, 1 + Math.max(topPointsCount(), bottomPointsCount()));
    }

    public int centerHeightCells() {
        return Math.max(2, 1 + Math.max(leftPointsCount(), rightPointsCount()));
    }

    public int widthCells() {
        return centerWidthCells()
                + (leftPointsCount() > 0 ? 1 : 0)
                + (rightPointsCount() > 0 ? 1 : 0);
    }

    public int heightCells() {
        return centerHeightCells()
                + (topPointsCount() > 0 ? 1 : 0)
                + (bottomPointsCount() > 0 ? 1 : 0);
    }
}
