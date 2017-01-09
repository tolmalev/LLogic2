package ru.llogic.core;

/**
 * @author tolmalev
 */
public enum PointState {
    HIGH(true),
    LOW(false),
    Z(false);

    private final boolean boolValue;

    PointState(boolean boolValue) {
        this.boolValue = boolValue;
    }

    public boolean asBool() {
        return boolValue;
    }
}
