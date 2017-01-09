package ru.llogic.core;

/**
 * @author tolmalev
 */
public class ShortCircuitException extends RuntimeException {
    public final Point point;
    public final PointState calculatedState;
    public final PointState settedState;

    public ShortCircuitException(Point point, PointState calculatedState, PointState settedState) {
        super("Can't set state " + settedState + " for point " + point.id + " current state is " + calculatedState);
        this.point = point;
        this.calculatedState = calculatedState;
        this.settedState = settedState;
    }
}
