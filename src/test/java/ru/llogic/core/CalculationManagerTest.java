package ru.llogic.core;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.llogic.core.element.AndElement;
import ru.llogic.core.element.DelayElement;
import ru.llogic.core.element.NotElement;
import ru.llogic.core.element.OrElement;
import ru.llogic.core.element.OrNotElement;
import ru.llogic.core.element.XorElement;


/**
 * @author tolmalev
 */
public class CalculationManagerTest {

    private CalculationManager manager;

    @BeforeEach
    void setup() {
        manager = new CalculationManager();
    }

    @Test
    void simpleAddPointsAndConnections() {
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            points.add(manager.createPoint());
        }

        for (int i = 1; i < points.size(); i++) {
            manager.addConnection(points.get(i - 1), points.get(i));
        }

        manager.updatePointState(points.get(0), PointState.HIGH);
        assertPointState(points.get(points.size() - 1), PointState.HIGH);

        manager.updatePointState(points.get(0), PointState.LOW);
        assertPointState(points.get(points.size() - 1), PointState.LOW);

        manager.removeConnection(points.get(0), points.get(1));
        for (int i = 1; i < points.size(); i++) {
            assertPointState(points.get(points.size() - 1), PointState.Z);
        }
    }

    @Test
    void badConnections() {
        // star
        // p1 -> p2 -> p3
        //       |
        //       p4

        Point p1 = manager.createPoint();
        Point p2 = manager.createPoint();
        Point p3 = manager.createPoint();
        Point p4 = manager.createPoint();

        manager.addConnection(p1, p2);
        manager.addConnection(p2, p3);
        manager.addConnection(p2, p4);

        manager.updatePointState(p1, PointState.HIGH);
        assertThrows(() -> manager.updatePointState(p2, PointState.LOW), ShortCircuitException.class);
        assertThrows(() -> manager.updatePointState(p3, PointState.LOW), ShortCircuitException.class);
        assertThrows(() -> manager.updatePointState(p4, PointState.LOW), ShortCircuitException.class);

        manager.updatePointState(p4, PointState.HIGH);
        assertThrows(() -> manager.updatePointState(p2, PointState.LOW), ShortCircuitException.class);
        assertThrows(() -> manager.updatePointState(p3, PointState.LOW), ShortCircuitException.class);

        manager.updatePointState(p1, PointState.Z);
        assertThrows(() -> manager.updatePointState(p2, PointState.LOW), ShortCircuitException.class);
        assertThrows(() -> manager.updatePointState(p3, PointState.LOW), ShortCircuitException.class);

        manager.updatePointState(p4, PointState.Z);
        manager.updatePointState(p3, PointState.LOW);
        assertThrows(() -> manager.updatePointState(p4, PointState.HIGH), ShortCircuitException.class);

        // p1 -> p2 -> p3
        //
        //       p4
        manager.removeConnection(p2, p4);
        manager.updatePointState(p4, PointState.HIGH);

        assertThrows(() -> manager.addConnection(p2, p4), ShortCircuitException.class);
    }

    @Test
    void andElement() {
        Element element = new AndElement(manager, 2);

        Point in1 = element.getPoints().get(0);
        Point in2 = element.getPoints().get(1);
        Point out = element.getPoints().get(2);

        manager.updatePointState(in1, PointState.LOW);
        manager.updatePointState(in2, PointState.LOW);
        assertPointState(out, PointState.LOW);

        manager.updatePointState(in1, PointState.HIGH);
        assertPointState(out, PointState.LOW);

        manager.updatePointState(in2, PointState.HIGH);
        assertPointState(out, PointState.HIGH);

        manager.updatePointState(in1, PointState.LOW);
        assertPointState(out, PointState.LOW);
    }

    @Test
    void orElement() {
        Element element = new OrElement(manager, 2);

        Point in1 = element.getPoints().get(0);
        Point in2 = element.getPoints().get(1);
        Point out = element.getPoints().get(2);

        manager.updatePointState(in1, PointState.LOW);
        manager.updatePointState(in2, PointState.LOW);
        assertPointState(out, PointState.LOW);

        manager.updatePointState(in1, PointState.HIGH);
        assertPointState(out, PointState.HIGH);

        manager.updatePointState(in2, PointState.HIGH);
        assertPointState(out, PointState.HIGH);

        manager.updatePointState(in1, PointState.LOW);
        assertPointState(out, PointState.HIGH);

        manager.updatePointState(in2, PointState.LOW);
        assertPointState(out, PointState.LOW);
    }

    @Test
    void xorElement() {
        Element element = new XorElement(manager, 2);

        Point in1 = element.getPoints().get(0);
        Point in2 = element.getPoints().get(1);
        Point out = element.getPoints().get(2);

        manager.updatePointState(in1, PointState.LOW);
        manager.updatePointState(in2, PointState.LOW);
        assertPointState(out, PointState.LOW);

        manager.updatePointState(in1, PointState.HIGH);
        assertPointState(out, PointState.HIGH);

        manager.updatePointState(in2, PointState.HIGH);
        assertPointState(out, PointState.LOW);

        manager.updatePointState(in1, PointState.LOW);
        assertPointState(out, PointState.HIGH);

        manager.updatePointState(in2, PointState.LOW);
        assertPointState(out, PointState.LOW);
    }

    @Test
    void noElement() {
        Element element = new NotElement(manager);

        Point in = element.getPoints().get(0);
        Point out = element.getPoints().get(1);

        manager.updatePointState(in, PointState.LOW);
        assertPointState(out, PointState.HIGH);

        manager.updatePointState(in, PointState.HIGH);
        assertPointState(out, PointState.LOW);

        manager.updatePointState(in, PointState.LOW);
        assertPointState(out, PointState.HIGH);
    }

    @org.junit.jupiter.api.Test
    void delayElement() throws InterruptedException {
        Element element = new DelayElement(manager, 100);

        Point in = element.getPoints().get(0);
        Point out = element.getPoints().get(1);

        manager.updatePointState(in, PointState.LOW);
        assertPointState(out, PointState.LOW);

        manager.updatePointState(in, PointState.HIGH);
        for (int i = 0; i < 8; i++) {
            Thread.sleep(10);
            Assert.assertEquals(PointState.LOW, manager.getPointState(out));
        }
        manager.updatePointState(in, PointState.HIGH);
    }

    @Test
    void rsTrigger() {
        //RS trigger of 2 or-not elements
        Element orNot1 = new OrNotElement(manager, 2);
        Element orNot2 = new OrNotElement(manager, 2);

        Point in11 = orNot1.getPoints().get(0);
        Point in12 = orNot1.getPoints().get(1);
        Point out1 = orNot1.getPoints().get(2);

        Point in21 = orNot2.getPoints().get(0);
        Point in22 = orNot2.getPoints().get(1);
        Point out2 = orNot2.getPoints().get(2);

        manager.addConnection(out1, in21);
        manager.addConnection(out2, in12);

        manager.updatePointState(in11, PointState.LOW);
        manager.updatePointState(in22, PointState.LOW);

        // initial state
        assertPointState(out1, PointState.HIGH);
        assertPointState(out2, PointState.LOW);

        // set to low
        manager.updatePointState(in11, PointState.HIGH);
        assertPointState(out1, PointState.LOW);
        assertPointState(out2, PointState.HIGH);

        //keep state
        manager.updatePointState(in11, PointState.LOW);
        assertPointState(out1, PointState.LOW);
        assertPointState(out2, PointState.HIGH);

        // set to high
        manager.updatePointState(in22, PointState.HIGH);
        assertPointState(out1, PointState.HIGH);
        assertPointState(out2, PointState.LOW);
    }

    @Test
    void delayNotGenerator() throws InterruptedException {
        Element not = new NotElement(manager);
        Element delay = new DelayElement(manager, 100);

        manager.addConnection(not.getPoints().get(1), delay.getPoints().get(0));
        manager.addConnection(delay.getPoints().get(1), not.getPoints().get(0));

        not.calculate();

        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
            Assert.assertEquals(i % 2 == 0 ? PointState.HIGH : PointState.LOW, not.getPointState(1));
        }
    }

    private void assertPointState(Point out, PointState state) {
        manager.waitUntilEmptyQueue();
        Assert.assertEquals(state, manager.getPointState(out));
    }

    private void assertThrows(Runnable r, Class<? extends Throwable> clazz) {
        try {
            r.run();
        } catch (Throwable e) {
            if (clazz.isInstance(e)) {
                return;
            } else {
                Assert.fail("Must throw " + clazz + " but received " + e);
            }
        }
        Assert.fail("Must throw " + clazz + " but completed ok");
    }
}