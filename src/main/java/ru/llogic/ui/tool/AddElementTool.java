package ru.llogic.ui.tool;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ru.llogic.ui.DocumentManager;
import ru.llogic.ui.GridUtils;

/**
 * @author tolmalev
 */
public class AddElementTool extends ToolBase {
    private Rectangle newElem;

    public AddElementTool(DocumentManager documentManager, Pane elementsPane, Pane newElementPane) {
        super(documentManager);

        elementsPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (isActive()) {
                if (event.getTarget().equals(elementsPane) && event.getClickCount() == 1) {
                    addElement(documentManager, event.getX(), event.getY());
                }
            }
        });

        newElem = new Rectangle(0, 0);
        newElem.setMouseTransparent(false);

        newElementPane.getChildren().add(newElem);

        EventHandler<MouseEvent> handler = event -> {
            if (isActive()) {
                Point2D gridPoint = GridUtils.rectanglePosition(event.getX(), event.getY(),
                        getWidthCells(), getHeightCells());

                newElem.setLayoutX(gridPoint.getX());
                newElem.setLayoutY(gridPoint.getY());
                newElem.setWidth(GridUtils.gridSize(getWidthCells()));
                newElem.setHeight(GridUtils.gridSize(getHeightCells()));

                if (documentManager.placeIsFree(newElem.getBoundsInParent())) {
                    newElem.setFill(Color.rgb(0, 255, 0, 0.3));
                } else {
                    newElem.setFill(Color.rgb(255, 0, 0, 0.3));
                }
            }
        };

        elementsPane.addEventHandler(MouseEvent.MOUSE_MOVED, handler);
        elementsPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, handler);

        elementsPane.addEventHandler(MouseEvent.MOUSE_EXITED, event -> newElem.setFill(Color.TRANSPARENT));
    }

    protected void addElement(DocumentManager documentManager, double x, double y) {
        documentManager.addAndElement(x, y);
    }

    protected int getHeightCells() {
        return 4;
    }

    protected int getWidthCells() {
        return 5;
    }

    @Override
    public void deactivate() {
        super.deactivate();
        newElem.setFill(Color.TRANSPARENT);
    }
}
