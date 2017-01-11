package ru.llogic.ui;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.Point;
import ru.llogic.core.element.AndElement;
import ru.llogic.core.element.InElement;
import ru.llogic.ui.widget.AndElementWidget;
import ru.llogic.ui.widget.ElementWidget;
import ru.llogic.ui.widget.InElementWidget;

/**
 * @author tolmalev
 */
public class DocumentManager {
    private static final Logger logger = LogManager.getLogger(DocumentManager.class);

    private boolean active = false;

    private final Pane mainPane;

    private final Selector selector;

    private final LinesCanvas linesPane;
    private final Pane newElementPane;
    private final Pane elementsPane;
    private final Pane selectionPane;

    private final Map<Element, ElementWidget> widgets = new ConcurrentHashMap<>();
    private final CalculationManager calculationManager;

    public DocumentManager(UiController uiController, Pane mainPane) {
        this.mainPane = mainPane;
        this.calculationManager = new CalculationManager();

        linesPane = (LinesCanvas) mainPane.lookup(".lines-pane");
        linesPane.setDocumentManager(this);

        calculationManager.addCalculationQueueListener(linesPane::draw);

        newElementPane = (Pane) mainPane.lookup(".new-element-pane");
        elementsPane = (Pane) mainPane.lookup(".elements_pane");
        selectionPane = (Pane) mainPane.lookup(".selection-pane");

        selector = new Selector(elementsPane, selectionPane, this);

        elementsPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getTarget().equals(elementsPane) && event.getClickCount() == 1) {
                addAndElement(event.getX(), event.getY());
            }
        });

        Rectangle newElem = new Rectangle(0, 0);
        newElementPane.getChildren().add(newElem);

        elementsPane.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            Point2D gridPoint = GridUtils.rectanglePosition(event.getX(), event.getY(), 5, 4);

            newElem.setLayoutX(gridPoint.getX());
            newElem.setLayoutY(gridPoint.getY());
            newElem.setWidth(50);
            newElem.setHeight(40);

            if (elementsPane.getChildren()
                    .filtered(node -> node.getBoundsInParent().intersects(newElem.getBoundsInParent()))
                    .isEmpty())
            {
                newElem.setFill(Color.rgb(0, 255, 0, 0.3));
            } else {
                newElem.setFill(Color.rgb(255, 0, 0, 0.3));
            }
        });
    }

    public InElement addInElement(double x, double y) {
        InElement inElement = new InElement(calculationManager);
        inElement.calculate();

        InElementWidget widget = new InElementWidget(inElement);

        Point2D gridPoint = GridUtils.rectanglePosition(x, y, 5, 4);

        widget.setLayoutX(gridPoint.getX());
        widget.setLayoutY(gridPoint.getY());

        widgets.put(inElement, widget);
        elementsPane.getChildren().add(widget);

        return inElement;
    }

    public AndElement addAndElement(double x, double y) {
        AndElement andElement = new AndElement(calculationManager, 2);
        andElement.calculate();

        AndElementWidget widget = new AndElementWidget(andElement);

        Point2D gridPoint = GridUtils.rectanglePosition(x, y, 5, 4);

        widget.setLayoutX(gridPoint.getX());
        widget.setLayoutY(gridPoint.getY());

        widgets.put(andElement, widget);
        elementsPane.getChildren().add(widget);

        return andElement;
    }

    void selectionCompleted(Bounds bounds, boolean addToSelection) {
        for (Node node : elementsPane.getChildren()) {
            if (node instanceof ElementWidget && node.getBoundsInParent().intersects(bounds)) {
                selectNodeInternal(node);
            } else if (!addToSelection) {
                unselectNode(node);
            }
        }
//        linesPane.setCursor(Cursor.DEFAULT);
    }

    void selectOneNode(Node node, boolean addToSelection) {
        if (!addToSelection) {
            unselectAll();
        }
        selectNodeInternal(node);
    }

    void unselectAll() {
        elementsPane.getChildren().forEach(this::unselectNode);
    }

    private void selectNodeInternal(Node node) {
        node.getStyleClass().add("selected");
    }

    void unselectNode(Node node) {
        node.getStyleClass().remove("selected");
    }

    private void addWidget(AndElementWidget widget) {
        mainPane.getChildren().add(widget);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    CalculationManager getCalculationManager() {
        return calculationManager;
    }

    public Point2D getPosition(Point point) {
        Optional<Element<?>> element = calculationManager.getConnectedElement(point);
        if (element.isPresent()) {
            ElementWidget widget = widgets.get(element.get());
            Point2D positionInWidget = widget.getPointPosition(point);
            return positionInWidget.add(widget.getBoundsInParent().getMinX(), widget.getBoundsInParent().getMinY());
        } else {
            return new Point2D(100, 100);
        }
    }
}
