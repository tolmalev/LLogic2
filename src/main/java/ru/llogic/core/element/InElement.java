package ru.llogic.core.element;

import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.ElementSettings;
import ru.llogic.core.PointState;

/**
 * @author tolmalev
 */
public class InElement extends Element<InElement.InElementSettings> {
    protected InElement(CalculationManager manager) {
        super(manager, 1, InElementSettings.class, new InElementSettings(PointState.LOW));
    }

    @Override
    public void doCalculate() {
        updatePointState(0, settings.pointState);
    }

    public static class InElementSettings implements ElementSettings {
        private final PointState pointState;

        public InElementSettings(PointState pointState) {
            this.pointState = pointState;
        }
    }
}