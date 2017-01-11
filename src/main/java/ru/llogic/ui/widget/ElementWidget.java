package ru.llogic.ui.widget;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.core.Element;
import ru.llogic.core.Point;
import ru.llogic.ui.GridUtils;

/**
 * @author tolmalev
 */
public abstract class ElementWidget<T extends Element> extends BorderPane {
    private static final Logger logger = LogManager.getLogger(ElementWidget.class);

    protected final T element;

    protected ElementWidget(T element) {
        this.element = element;

        getStylesheets().add("ru/llogic/ui/main.css");
        getStylesheets().add("ru/llogic/ui/widget/default_elements.css");
//        getStyleClass().add("element");

//        getStyleClass().add(getClass().getSimpleName());

        setCenter(buildCenter());
        if (leftPointsCount() > 0) {
            setLeft(buildLeft());
        }
        if (rightPointsCount() > 0) {
            setRight(buildRight());
        }

        addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (clickToMeOrMyChildren(event) && event.getClickCount() == 2) {
                onDoubleClick(event);
            }
        });
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
        logger.info("Double clicked element: ", this);
    }

    protected Node buildLeft() {
        Pane pane = new Pane();
        for (int i = 0; i < leftPointsCount(); i++) {
            int y = GridUtils.gridSize(i + 1);
            pane.getChildren().add(new Line(2, y, GridUtils.gridSize(1), y));
            pane.getChildren().add(new Circle(0, y, 2, Color.rgb(132, 2, 4)));
        }
        return pane;
    }

    protected Node buildRight() {
        Pane pane = new Pane();
        for (int i = 0; i < rightPointsCount(); i++) {
            int y = GridUtils.gridSize(i + 1);
            pane.getChildren().add(new Line(0, y, GridUtils.gridSize(1), y));
        }
        return pane;
    }

    protected Node buildCenter() {
        int widthCells = 3;
        int heightCells = 2;

        widthCells = Math.max(widthCells, 1 + Math.max(topPointsCount(), bottomPointsCount()));
        heightCells = Math.max(heightCells, 1 + Math.max(leftPointsCount(), rightPointsCount()));

        Rectangle rectangle = new Rectangle(
                GridUtils.gridSize(widthCells),
                GridUtils.gridSize(heightCells)
        );
        return rectangle;
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
}
