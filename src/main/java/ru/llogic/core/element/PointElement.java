package ru.llogic.core.element;

import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.EmptyElementSettings;
import ru.llogic.core.Point;

/**
 * @author tolmalev
 */
public class PointElement extends Element<EmptyElementSettings> {
    public PointElement(CalculationManager manager) {
        super(manager, 1, EmptyElementSettings.class, new EmptyElementSettings());
    }

    @Override
    public void doCalculate() {
        //do nothing
    }

    public Point getPoint() {
        return getPoints().get(0);
    }
}
