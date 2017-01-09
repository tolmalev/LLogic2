package ru.llogic.core;

import java.time.Duration;
import java.time.Instant;

/**
 * @author tolmalev
 */
public class RunnableWithTime implements Comparable<RunnableWithTime> {
    private final Runnable target;

    private final long order;
    private final Instant time;

    public RunnableWithTime(Runnable target, long order, Instant time) {
        this.target = target;
        this.order = order;
        this.time = time;
    }


    @Override
    public int compareTo(RunnableWithTime o) {
        int res = time.compareTo(o.time);
        if (res != 0) {
            return res;
        }
        return Long.compare(order, o.order);
    }

    public boolean canBeExecutedNow() {
        return time.toEpochMilli() - Instant.now().toEpochMilli() <= 0;
    }

    public void run() throws InterruptedException {
        long timeToSleep = time.toEpochMilli() - Instant.now().toEpochMilli();
        if (timeToSleep > 0) {
            Thread.sleep(timeToSleep);
        }
        target.run();
    }
}
