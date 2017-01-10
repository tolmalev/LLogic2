package ru.llogic.ui;

import javafx.geometry.Point2D;

/**
 * @author tolmalev
 */
public class GridUtils {
    public static final int GRID_SIZE = 10;

    public static final int gridSize(int cells) {
        return cells * GRID_SIZE;
    }

    public static Point2D rectanglePosition(double centerX, double centerY, int xCells, int yCells) {
        return toGrid(centerX - xCells * GRID_SIZE / 2, centerY - yCells * GRID_SIZE / 2);
    }

    public static Point2D move(Point2D point, int xCells, int yCells) {
        return point.add(gridSize(xCells), gridSize(yCells));
    }

    public static Point2D toGrid(double x, double y) {
        return toGrid(new Point2D(x, y));
    }

    public static Point2D toGrid(Point2D position) {
        return new Point2D(toGrid(position.getX()), toGrid(position.getY()));
    }

    public static double toGrid(double coord) {
        return Math.floor((coord + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
    }
}
