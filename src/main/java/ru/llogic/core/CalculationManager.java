package ru.llogic.core;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.geometry.Point2D;

/**
 * @author tolmalev
 */
public class CalculationManager {
    private final AtomicLong pointId = new AtomicLong(1);

    private Map<Point, PointState> pointState = new ConcurrentHashMap<>();
    private Map<Point, PointState> calculatedPointState = new ConcurrentHashMap<>();

    private Map<Point, Set<Point>> connections = new ConcurrentHashMap<>();
    private Set<Connection> allConnections = new HashSet<>();

    private Map<Point, Element> elementPoints = new ConcurrentHashMap<>();

    private volatile PriorityBlockingQueue<RunnableWithTime> calculationQueue = new PriorityBlockingQueue<>();

    private AtomicLong queueOrder = new AtomicLong(0);
    private AtomicLong elementId = new AtomicLong(0);

    private Thread calculatorThread;

    private final Set<Runnable> calculationQueueListeners = new HashSet<>();
    private final Set<BiConsumer<Point, Point>> addConnectionListeners = new HashSet<>();
    private final Set<BiConsumer<Point, Point>> removeConnectionListeners = new HashSet<>();

    public CalculationManager() {
        calculatorThread = new Thread(this::calculateAll);
        calculatorThread.start();
    }

    public synchronized Point createPoint() {
        return createPoint(PointState.Z);
    }

    public synchronized void removeElement(Element<?> element) {
        //remove all points
        element.getPoints().forEach(this::removePoint);
    }

    public synchronized void removePoint(Point point) {
        //first remove all connections
        getPointConnections(point).forEach(to -> removeConnection(point, to));

        //remove connection to Element
        elementPoints.remove(point);

        //remove state
        pointState.remove(point);
        calculatedPointState.remove(point);
    }

    private Set<Point> getPointConnections(Point point) {
        return connections.getOrDefault(point, new HashSet<>());
    }

    public synchronized Point createPoint(PointState state) {
        Point point = new Point(pointId.getAndIncrement(), this);

        pointState.put(point, state);
        calculatedPointState.put(point, state);

        return point;
    }

    public void addConnection(Point a, Point b) {
        synchronized (connections) {
            allConnections.add(new Connection(a, b));

            PointState state1 = calculatedPointState.get(a);
            PointState state2 = calculatedPointState.get(b);
            if (state1 != state2 && state1 != PointState.Z && state2 != PointState.Z) {
                throw new ShortCircuitException(b, state2, state1);
            }
            connections.computeIfAbsent(a, k -> new HashSet<>()).add(b);
            connections.computeIfAbsent(b, k -> new HashSet<>()).add(a);

            calculateStates(a, Optional.empty());

            synchronized (addConnectionListeners) {
                addConnectionListeners.forEach(listener -> listener.accept(a, b));
            }
        }
    }

    public Set<Connection> getAllConnections() {
        return Collections.unmodifiableSet(allConnections);
    }

    public void removeConnection(Point a, Point b) {
        synchronized (connections) {
            allConnections.remove(new Connection(a, b));

            getPointConnections(a).remove(b);
            getPointConnections(b).remove(a);

            calculateStates(a, Optional.empty());
            calculateStates(b, Optional.empty());

            synchronized (removeConnectionListeners) {
                removeConnectionListeners.forEach(listener -> listener.accept(a, b));
            }
        }
    }

    public boolean hasConnection(Point a, Point b) {
        return getPointConnections(a).contains(b);
    }

    public PointState getPointState(Point point) {
        PointState st = calculatedPointState.get(point);
        if (st == null) {
            throw new RuntimeException("Unknown point");
        }
        return st;
    }

    public void updatePointStateAsync(Point point, PointState state, Element source, long delayMillis) {
        addToCalculationQueue(() -> updatePointState(point, state, source), delayMillis);
    }

    public synchronized void updatePointState(Point point, PointState state) {
        updatePointState(point, state, Optional.empty());
    }

    public synchronized void updatePointState(Point point, PointState state, Element source) {
        updatePointState(point, state, Optional.of(source));
    }

    public synchronized void updatePointState(Point point, PointState state, Optional<Element> source) {
        PointState oldState = this.pointState.get(point);
        PointState current = calculatedPointState.get(point);

        if (oldState == state) {
            //nothing changed
            pointState.put(point, state);
            return;
        }

        if (oldState == PointState.Z && current == state) {
            //smth changed but calculated state hasn't
            pointState.put(point, state);
            return;
        }

        if (state != PointState.Z && dfsFind(point, p -> p != point
                && pointState.get(p) != PointState.Z
                && pointState.get(p) != state))
        {
            throw new ShortCircuitException(point, current, state);
        }

        pointState.put(point, state);
        calculateStates(point, source);
    }

    private synchronized void calculateStates(Point point, Optional<Element> source) {
        PointState state = this.pointState.get(point);
        if (state == PointState.Z) {
            state = findNonZState(point).orElse(PointState.Z);
        }
        PointState finalState = state;

        dfsAll(point, p -> updateCalculatedState(finalState, p, point, source));
    }

    private void updateCalculatedState(PointState newState, Point p, Point startPoint, Optional<Element> source) {
        PointState oldState = calculatedPointState.put(p, newState);
        if (oldState != newState) {
            Element element = elementPoints.get(p);
            if (element != null) {
                if (!(source.isPresent() && source.get() == element && p == startPoint)) {
                    // not this point caused state update
                    addToCalculationQueue(element);
                }
            }
            p.stateChanged(newState);
        }
    }

    void addToCalculationQueue(Element element) {
        addToCalculationQueue(element::calculate, 0);
    }

    void addToCalculationQueue(Runnable runnable, long delayMillis) {
        calculationQueue.add(new RunnableWithTime(
                runnable,
                queueOrder.incrementAndGet(),
                Instant.now().plusMillis(delayMillis)
        ));
    }

    private void calculateAll() {
        while (!Thread.interrupted()) {
            RunnableWithTime poll = calculationQueue.peek();
            try {
                if (poll != null) {
                    // can be interrupted
                    poll.run();
                    calculationQueue.poll();
                    synchronized (calculationQueueListeners) {
                        calculationQueueListeners.forEach(Runnable::run);
                    }
                } else {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                // calculation is interrupted
                calculationQueue.clear();
                break;
            }
        }
        //TODO: completion code
        synchronized (this) {
            calculatorThread = null;
        }
    }

    private Optional<PointState> findNonZState(Point start) {
        return dfs(
                Optional.<PointState>empty(),
                start,
                (Function<Point, Optional<PointState>>) p -> {
                    PointState st = pointState.get(p);
                    if (st != PointState.Z) {
                        return Optional.of(st);
                    } else {
                        return Optional.empty();
                    }
                },
                (op1, op2) -> op1.isPresent() ? op1 : op2,
                Optional::isPresent
        );
    }

    private synchronized boolean dfsFind(Point start, Predicate<Point> fn) {
        return dfs(
                false,
                start,
                fn::test,
                (a, b) -> a || b,
                val -> val
        );
    }

    synchronized void dfsAll(Point start, Consumer<Point> fn) {
        dfs(
                false,
                start,
                (p) -> {
                    fn.accept(p);
                    return false;
                },
                (a, b) -> a && b,
                val -> val
        );
    }

    synchronized <T, R> R dfs(
            R current,
            Point start,
            Function<Point, T> mapFn,
            BiFunction<R, T, R> reduceFn,
            Predicate<R> exitFn)
    {
        return dfsInternal(current, start, mapFn, reduceFn, exitFn, new HashSet<>());
    }

    private synchronized <T, R> R dfsInternal(
            R current,
            Point start,
            Function<Point, T> mapFn,
            BiFunction<R, T, R> reduceFn,
            Predicate<R> exitFn,
            Set<Long> visited)
    {
        if (visited.contains(start.id)) {
            return current;
        }
        current = reduceFn.apply(current, mapFn.apply(start));
        if (exitFn.test(current)) {
            return current;
        }

        visited.add(start.id);
        for (Point to: getPointConnections(start)) {
            if (!visited.contains(to.id)) {
                current = dfsInternal(current, to, mapFn, reduceFn, exitFn, visited);
                if (exitFn.test(current)) {
                    return current;
                }
            }
        }
        return current;
    }

    synchronized void bind(Element<?> element) {
        element.getPoints().forEach(point -> elementPoints.put(point, element));
    }

    public void waitUntilEmptyQueue() {
        while (!calculationQueue.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public long genElementId() {
        return elementId.incrementAndGet();
    }

    public Optional<Element<?>> getConnectedElement(Point point) {
        return Optional.ofNullable(elementPoints.get(point));
    }

    public void addCalculationQueueListener(Runnable runnable) {
        synchronized (calculationQueueListeners) {
            calculationQueueListeners.add(runnable);
        }
    }

    public void addNewConnectionListener(BiConsumer<Point, Point> runnable) {
        synchronized (addConnectionListeners) {
            addConnectionListeners.add(runnable);
        }
    }

    public void addRemoveConnectionListener(BiConsumer<Point, Point> runnable) {
        synchronized (removeConnectionListeners) {
            removeConnectionListeners.add(runnable);
        }
    }
}