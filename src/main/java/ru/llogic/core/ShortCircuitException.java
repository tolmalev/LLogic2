package ru.llogic.core;

/**
 * @author tolmalev
 */
public class ShortCircuitException extends RuntimeException {
    public final Point point;

    public ShortCircuitException(Point point) {
        super("Short circuit at point " + point);
        this.point = point;
    }
}
