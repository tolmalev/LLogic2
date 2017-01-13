package ru.llogic.ui;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.Point;
import ru.llogic.core.element.AndElement;
import ru.llogic.core.element.ByteInElement;
import ru.llogic.core.element.InElement;
import ru.llogic.core.element.PointElement;
import ru.llogic.ui.tool.AddElementTool;
import ru.llogic.ui.tool.ConnectTool;
import ru.llogic.ui.tool.SelectorTool;
import ru.llogic.ui.tool.ToolBase;
import ru.llogic.ui.widget.AndElementWidget;
import ru.llogic.ui.widget.ByteInElementWidget;
import ru.llogic.ui.widget.ElementWidget;
import ru.llogic.ui.widget.InElementWidget;
import ru.llogic.ui.widget.PointWidget;

/**
 * @author tolmalev
 */
public class DocumentManager {
    private static final Logger logger = LogManager.getLogger(DocumentManager.class);

    private boolean active = false;

    private final Pane mainPane;

    private final SelectorTool selectorTool;
    private final AddElementTool addElementTool;
    private final ToolBase byteInElementTool;
    private final ConnectTool connectTool;

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

        calculationManager.addCalculationQueueListener(() -> Platform.runLater(linesPane::draw));
        calculationManager.addNewConnectionListener((a, b) -> Platform.runLater(linesPane::draw));
        calculationManager.addRemoveConnectionListener((a, b) -> Platform.runLater(linesPane::draw));

        newElementPane = (Pane) mainPane.lookup(".new-element-pane");
        elementsPane = (Pane) mainPane.lookup(".elements_pane");
        selectionPane = (Pane) mainPane.lookup(".selection-pane");

        selectorTool = new SelectorTool(this, elementsPane, selectionPane);
        addElementTool = new AddElementTool(this, elementsPane, newElementPane);
        connectTool = new ConnectTool(this, elementsPane);

        byteInElementTool = new AddElementTool(this, elementsPane, newElementPane) {
            @Override
            protected int getWidthCells() {
                return 4;
            }

            @Override
            protected int getHeightCells() {
                return 9;
            }

            @Override
            protected void addElement(DocumentManager documentManager, double x, double y) {
                addWidget(x, y, new ByteInElementWidget(new ByteInElement(calculationManager, 129)));
            }
        };

        activateTool(selectorTool);
    }

    public InElement addInElement(double x, double y) {
        InElement inElement = new InElement(calculationManager);

        InElementWidget widget = new InElementWidget(inElement);

        addWidget(x, y, widget);

        return inElement;
    }

    public AndElement addAndElement(double x, double y) {
        AndElement andElement = new AndElement(calculationManager, 2);

        AndElementWidget widget = new AndElementWidget(andElement);

        addWidget(x, y, widget);

        return andElement;
    }

    public PointElement addPointElement(double x, double y) {
        PointElement element = new PointElement(calculationManager);
        addWidget(x, y, new PointWidget(element));
        return element;
    }

    private <T extends Element<?>> void addWidget(double x, double y, ElementWidget<T> widget) {
        T element = widget.getElement();

        Point2D gridPoint = GridUtils.rectanglePosition(x, y, widget.widthCells(), widget.heightCells());

        widget.setLayoutX(gridPoint.getX() - GridUtils.ELEMENT_BORDER);
        widget.setLayoutY(gridPoint.getY() - GridUtils.ELEMENT_BORDER);

        widgets.put(element, widget);
        elementsPane.getChildren().add(widget);

        // hack
        widget.widthProperty().addListener(evt -> linesPane.draw());
        widget.heightProperty().addListener(evt -> linesPane.draw());
        widget.layoutXProperty().addListener(evt -> linesPane.draw());
        widget.layoutYProperty().addListener(evt -> linesPane.draw());

        element.addToCalculationQueue();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CalculationManager getCalculationManager() {
        return calculationManager;
    }

    public Point2D getPosition(Point point) {
        Optional<Element<?>> element = calculationManager.getConnectedElement(point);
        if (element.isPresent()) {
            ElementWidget widget = widgets.get(element.get());
            Point2D positionInWidget = widget.getPointPosition(point);
            return positionInWidget
                    .add(widget.getBoundsInParent().getMinX(), widget.getBoundsInParent().getMinY())
                    .add(GridUtils.ELEMENT_BORDER, GridUtils.ELEMENT_BORDER);
        } else {
            return new Point2D(100, 100);
        }
    }

    public boolean placeIsFree(Bounds bounds) {
        return placeIsFree(bounds, Collections.EMPTY_SET);
    }

    public boolean placeIsFree(Bounds bounds, Set<ElementWidget> ignore) {
        return elementsPane.getChildren()
                .filtered(node -> node instanceof ElementWidget)
                .filtered(node -> !ignore.contains(node))
                .filtered(node -> node.getBoundsInParent().intersects(bounds))
                .isEmpty();
    }

    private void activateTool(ToolBase tool) {
        if (activeTool != null) {
            activeTool.deactivate();
        }

        activeTool = tool;
        tool.activate();
    }

    public void activateConnect() {
        activateTool(connectTool);
    }

    public void activateSelector() {
        activateTool(selectorTool);
    }

    public void activateAddAndElement() {
        activateTool(addElementTool);
    }

    public void activateAddByteIn() {
        activateTool(byteInElementTool);
    }
}
