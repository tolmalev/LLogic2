package ru.llogic.core.element;

import ru.llogic.core.CalculationManager;

/**
 * @author tolmalev
 */
public class OrElement extends LogicElement {
    public OrElement(CalculationManager manager, int inputsCount) {
        super(manager, inputsCount, false, (a, b) -> a || b);
    }
}
