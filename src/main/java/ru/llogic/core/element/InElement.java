package ru.llogic.core.element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.ElementSettings;
import ru.llogic.core.Point;
import ru.llogic.core.PointState;

/**
 * @author tolmalev
 */
public class InElement extends Element<InElement.InElementSettings> {
    private static final Logger logger = LogManager.getLogger(InElement.class);

    public InElement(CalculationManager manager) {
        super(manager, 1, InElementSettings.class, new InElementSettings(PointState.LOW));
    }

    @Override
    public void doCalculate() {
        updatePointState(0, settings.pointState);
    }

    public Point getOutputPoint() {
        return getPoints().get(0);
    }

    public void switchState() {
        PointState newState = getSettings().getPointState() == PointState.HIGH ? PointState.LOW : PointState.HIGH;
        setSettings(new InElementSettings(newState));

        logger.debug("Element " + this + "output switched to " + newState);
    }

    public static class InElementSettings implements ElementSettings {
        private final PointState pointState;

        public InElementSettings(PointState pointState) {
            this.pointState = pointState;
        }

        public PointState getPointState() {
            return pointState;
        }
    }
}
