package ru.llogic.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author tolmalev
 */
public class Point {
    public final long id;

    private final CalculationManager calculationManager;
    private final List<Consumer<PointState>> stateChangeListeners = new ArrayList<>();

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

    void stateChanged(PointState currentState) {
        synchronized (stateChangeListeners) {
            stateChangeListeners.forEach(listener -> listener.accept(currentState));
        }
    }

    public void addStateChangeListener(Consumer<PointState> listener) {
        synchronized (stateChangeListeners) {
            stateChangeListeners.add(listener);
        }
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                '}';
    }
}
