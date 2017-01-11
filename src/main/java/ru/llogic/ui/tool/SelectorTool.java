package ru.llogic.ui.tool;

import java.util.Optional;
import java.util.function.Consumer;

import javafx.collections.ListChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.ui.DocumentManager;
import ru.llogic.ui.widget.ElementWidget;

/**
 * @author tolmalev
 */
public class SelectorTool extends ToolBase {
    private static final Logger logger = LogManager.getLogger(SelectorTool.class);

    private final Pane elementsPane;
    private final Pane selectionPane;

    private Optional<Point2D> selectionStart = Optional.empty();
    private Optional<Point2D> dragStart = Optional.empty();

    public SelectorTool(DocumentManager documentManager, Pane elementsPane, Pane selectionPane) {
        super(documentManager);

        this.elementsPane = elementsPane;
        this.selectionPane = selectionPane;

        initSelectionHandling();
        initMoveHandling();
    }

    private void initMoveHandling() {
        Consumer<Node> consumer = this::initMoveHandling;

        forAllChildren(consumer);
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

    private void initMoveHandling(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (isActive()) {
                dragStart = Optional.of(new Point2D(event.getX(), event.getY()));
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (isActive()) {
                dragStart = Optional.empty();
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isActive() && dragStart.isPresent()) {
                Point2D dragDelta = new Point2D(event.getX(), event.getY())
                        .add(-dragStart.get().getX(), -dragStart.get().getY());

                logger.info("Mouse dragged: " + dragDelta.getX() + " " + dragDelta.getY());
            }
        });
    }

    private void initSelectionHandling() {

        Rectangle selection = new Rectangle(0, 0, 0, 0);
        selection.setFill(Color.rgb(176, 204, 241, 100.0 / 255));
        selection.setStroke(Color.rgb(53, 100, 255));

        elementsPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isActive() && selectionStart.isPresent()) {
                selection.setLayoutX(Math.min(event.getX(), selectionStart.get().getX()));
                selection.setLayoutY(Math.min(event.getY(), selectionStart.get().getY()));

                selection.setWidth(Math.max(event.getX(), selectionStart.get().getX()) - selection.getLayoutX());
                selection.setHeight(Math.max(event.getY(), selectionStart.get().getY()) - selection.getLayoutY());
            }
        });

        elementsPane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (isActive() && event.getTarget().equals(elementsPane)) {
                selectionStart = Optional.of(new Point2D(event.getX(), event.getY()));

                logger.debug("Started selection at " + selectionStart.get());

                selection.setLayoutX(event.getX());
                selection.setLayoutY(event.getY());
                selection.setWidth(0);
                selection.setHeight(0);

                selectionPane.getChildren().add(selection);
            }
        });

        elementsPane.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (isActive() && selectionStart.isPresent()) {
                selectionPane.getChildren().remove(selection);

                Point2D start = new Point2D(
                        Math.min(selectionStart.get().getX(), event.getX()),
                        Math.min(selectionStart.get().getY(), event.getY())
                );

                Point2D end = new Point2D(
                        Math.max(selectionStart.get().getX(), event.getX()),
                        Math.max(selectionStart.get().getY(), event.getY())
                );

                logger.debug("Selection completed width bounds: " + start + ", " + end);
                selectionStart = Optional.empty();

                selectElements(
                        new BoundingBox(
                        start.getX(), start.getY(),
                        end.getX() - start.getX(), end.getY() - start.getY()),
                        isAddToSelection(event)
                );
            }
        });

        forAllChildren(node -> {
            if (node instanceof ElementWidget) {
                node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (isActive()) {
                        selectOneNode((ElementWidget) node, isAddToSelection(event));
                    }
                });
            }
        });
    }

    public void selectElements(Bounds bounds, boolean addToSelection) {
        for (Node node : elementsPane.getChildren()) {
            if (node instanceof ElementWidget && node.getBoundsInParent().intersects(bounds)) {
                selectNodeInternal(node);
            } else if (!addToSelection) {
                unselectNode(node);
            }
        }
    }

    public void selectOneNode(ElementWidget node, boolean addToSelection) {
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

    @Override
    public void deactivate() {
        super.deactivate();
        unselectAll();
    }

    private boolean isAddToSelection(MouseEvent event) {
        return event.isControlDown() || event.isMetaDown();
    }
}
