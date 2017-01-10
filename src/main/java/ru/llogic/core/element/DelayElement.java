package ru.llogic.core.element;

import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.ElementSettings;

/**
 * @author tolmalev
 */
public class DelayElement extends Element<DelayElement.DelayElementSettings> {
    public DelayElement(CalculationManager manager, long delayMillis) {
        super(manager, 2, DelayElementSettings.class, new DelayElementSettings(delayMillis));
    }

    @Override
    public void doCalculate() {
        updatePointStateAsync(1, getPointState(0), settings.delayMillis);
    }

    public static final class DelayElementSettings implements ElementSettings {
        public final long delayMillis;

        public DelayElementSettings(long delayMillis) {
            this.delayMillis = delayMillis;
        }
    }
}
