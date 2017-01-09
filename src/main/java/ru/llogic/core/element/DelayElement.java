package ru.llogic.core.element;

import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;

/**
 * @author tolmalev
 */
public class DelayElement extends Element {
    private final long delayMillis;

    public DelayElement(CalculationManager manager, long delayMillis) {
        super(manager, 2);
        this.delayMillis = delayMillis;
    }

    @Override
    public void doCalculate() {
        updatePointStateAsync(1, getPointState(0), delayMillis);
    }
}
