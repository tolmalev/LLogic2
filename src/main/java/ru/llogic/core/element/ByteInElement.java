package ru.llogic.core.element;

import java.util.List;

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
public class ByteInElement extends Element<ByteInElement.ByteInElementSettings> {
    private static final Logger logger = LogManager.getLogger(ByteInElement.class);

    public ByteInElement(CalculationManager manager) {
        this(manager, 0);
    }

    public ByteInElement(CalculationManager manager, int startValue) {
        super(manager, 8, ByteInElementSettings.class, new ByteInElementSettings(startValue));
    }

    @Override
    public void doCalculate() {
        int n = settings.getValue();
        for(int i = 0; i < 8; i++) {
            updatePointState(i, n % 2 == 0 ? PointState.LOW : PointState.HIGH);
            n /= 2;
        }
    }

    public List<Point> getOutputPoints() {
        return getPoints();
    }

    public void setValue(int value) {
        setSettings(new ByteInElementSettings(value));

        addToCalculationQueue();

        logger.debug("Element " + this + "output switched to " + value);
    }

    public static class ByteInElementSettings implements ElementSettings {
        private final int value;

        public ByteInElementSettings(int value) {
            if (value < 0 || value > 255) {
                throw new IllegalArgumentException("Value must bound " + value + "[0, 255]");
            }
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
