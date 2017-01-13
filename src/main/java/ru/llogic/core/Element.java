package ru.llogic.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tolmalev
 */
public abstract class Element<T extends ElementSettings> {
    private final long id;

    private final CalculationManager manager;
    private final ArrayList<Point> points;

    private final Class<T> settingsClass;
    protected T settings;

    public final void addToCalculationQueue() {
        manager.addToCalculationQueue(this);
    }

    public final synchronized void calculate() {
        doCalculate();
    }

    public abstract void doCalculate();

    protected Element(CalculationManager manager, int pointsCount, Class<T> settingsClass, T settings) {
        this.id = manager.genElementId();
        this.points = new ArrayList<>(pointsCount);
        this.settingsClass = settingsClass;
        this.settings = settings;
        for (int i = 0; i < pointsCount; i++) {
            points.add(manager.createPoint());
        }
        this.manager = manager;

        manager.bind(this);
    }

    protected void removePoint(int pointIdx) {
        manager.removePoint(points.get(pointIdx));
    }

    protected void addPoint(int idx) {
        points.add(idx, manager.createPoint());
        manager.bind(this);
    }

    public void setSettings(T settings) {
        this.settings = settings;
    }

    public int getPointsCount() {
        return points.size();
    }

    protected PointState getPointState(int pointIdx) {
        return manager.getPointState(points.get(pointIdx));
    }

    protected List<PointState> getPointStates() {
        return points
                .stream()
                .map(manager::getPointState)
                .collect(Collectors.toList());
    }

    protected void updatePointState(int pointIdx, PointState state) {
        manager.updatePointState(points.get(pointIdx), state, this);
    }

    protected void updatePointStateAsync(int pointIdx, PointState state, long delayMillis) {
        manager.updatePointStateAsync(points.get(pointIdx), state, this, delayMillis);
    }

    public T getSettings() {
        return settings;
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getPointIndex(Point point) {
        return points.indexOf(point);
    }
}
