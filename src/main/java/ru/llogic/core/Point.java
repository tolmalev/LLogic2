package ru.llogic.core;

/**
 * @author tolmalev
 */
public class Point {
    public final long id;

    private final CalculationManager calculationManager;

    Point(long id, CalculationManager calculationManager) {
        this.id = id;
        this.calculationManager = calculationManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point point = (Point) o;

        return id == point.id;
    }

    public PointState getState() {
        return calculationManager.getPointState(this);
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                '}';
    }
}
