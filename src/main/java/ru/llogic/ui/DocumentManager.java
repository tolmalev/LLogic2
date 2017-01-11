package ru.llogic.ui;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.Point;
import ru.llogic.core.element.AndElement;
import ru.llogic.core.element.InElement;
import ru.llogic.ui.tool.AddElementTool;
import ru.llogic.ui.tool.SelectorTool;
import ru.llogic.ui.tool.ToolBase;
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

    private final SelectorTool selectorTool;
    private final AddElementTool addElementTool;

    private ToolBase activeTool;

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

        selectorTool = new SelectorTool(this, elementsPane, selectionPane);
        addElementTool = new AddElementTool(this, elementsPane, newElementPane);

        activateTool(selectorTool);
    }

    public InElement addInElement(double x, double y) {
        InElement inElement = new InElement(calculationManager);
        inElement.calculate();

        InElementWidget widget = new InElementWidget(inElement);

        addWidget(x, y, inElement, widget);

        return inElement;
    }

    public AndElement addAndElement(double x, double y) {
        AndElement andElement = new AndElement(calculationManager, 2);
        andElement.calculate();

        AndElementWidget widget = new AndElementWidget(andElement);

        addWidget(x, y, andElement, widget);

        return andElement;
    }

    private <T extends Element> void addWidget(double x, double y, T element, ElementWidget<T> widget) {
        Point2D gridPoint = GridUtils.rectanglePosition(x, y, widget.widthCells(), widget.heightCells());

        widget.setLayoutX(gridPoint.getX());
        widget.setLayoutY(gridPoint.getY());

        widgets.put(element, widget);
        elementsPane.getChildren().add(widget);

        // hack
        widget.widthProperty().addListener(evt -> linesPane.draw());
        widget.heightProperty().addListener(evt -> linesPane.draw());
    }

    public void selectElements(Bounds bounds, boolean addToSelection) {
        for (Node node : elementsPane.getChildren()) {
            if (node instanceof ElementWidget && node.getBoundsInParent().intersects(bounds)) {
                selectNodeInternal(node);
            } else if (!addToSelection) {
                unselectNode(node);
            }
        }
//        linesPane.setCursor(Cursor.DEFAULT);
    }

    public void selectOneNode(Node node, boolean addToSelection) {
        if (!addToSelection) {
            unselectAll();
        }
        selectNodeInternal(node);
    }

    public void unselectAll() {
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

    public void activateSelector() {
        activateTool(selectorTool);
    }

    public void activateAddAndElement() {
        activateTool(addElementTool);
    }

    private void activateTool(ToolBase tool) {
        if (activeTool != null) {
            activeTool.deactivate();
        }

        activeTool = tool;
        tool.activate();
    }
}
