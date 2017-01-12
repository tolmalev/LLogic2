package ru.llogic.ui.tool;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
import ru.llogic.ui.GridUtils;
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

    private Set<ElementWidget> selectedElements = new HashSet<>();

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
        class MovedElement {
            final ElementWidget widget;
            final Rectangle rectangle;
            final Point2D startPosition;

            public MovedElement(ElementWidget widget) {
                this.widget = widget;

                Bounds bounds = widget.getBoundsInParent();

                rectangle = new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(),
                        bounds.getHeight());
                rectangle.setMouseTransparent(true);
                rectangle.setFill(Color.TRANSPARENT);

                elementsPane.getChildren().add(rectangle);

                this.startPosition = new Point2D(rectangle.getX(), rectangle.getY());
            }
        };

        AtomicReference<List<MovedElement>> rectangles = new AtomicReference<>(null);

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (isActive()) {
                dragStart = Optional.of(new Point2D(event.getX(), event.getY()));
                rectangles.set(selectedElements
                        .stream()
                        .map(widget -> new MovedElement(widget))
                        .collect(Collectors.toList())
                );
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (isActive()) {
                dragStart = Optional.empty();
                List<MovedElement> rects = rectangles.get();
                if (rects != null) {
                    for (MovedElement rect : rects) {
                        rect.widget.setLayoutX(rect.rectangle.getX());
                        rect.widget.setLayoutY(rect.rectangle.getY());
                    }
                }
            }
            List<MovedElement> rects = rectangles.get();
            if (rects != null) {
                elementsPane.getChildren().removeAll(rects.stream().map(r -> r.rectangle).collect(Collectors.toList()));
            }
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (isActive() && dragStart.isPresent()) {
                Point2D dragDelta = new Point2D(event.getX(), event.getY())
                        .add(-dragStart.get().getX(), -dragStart.get().getY());

                logger.info("Mouse dragged: " + dragDelta.getX() + " " + dragDelta.getY());

                Point2D delta = GridUtils.toGridDelta(dragDelta);

                Color color;
                if (delta.getX() == 0 && delta.getY() == 0) {
                    color = Color.TRANSPARENT;
                } else {
                    color = Color.rgb(0, 255, 0, 0.3);
                }

                List<MovedElement> rects = rectangles.get();
                if (rects != null) {
                    for (MovedElement rect : rects) {
                        rect.rectangle.setFill(color);
                        rect.rectangle.setX(rect.startPosition.getX() + delta.getX());
                        rect.rectangle.setY(rect.startPosition.getY() + delta.getY());
                    }
                }
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
        selectedElements.clear();
    }

    private void selectNodeInternal(Node node) {
        if (node instanceof ElementWidget) {
            node.getStyleClass().add("selected");
            selectedElements.add((ElementWidget) node);
        }
    }

    void unselectNode(Node node) {
        if (node instanceof ElementWidget) {
            node.getStyleClass().remove("selected");
            selectedElements.remove(node);
        }
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
