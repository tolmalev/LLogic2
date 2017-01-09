package ru.llogic.core.element;

import ru.llogic.core.CalculationManager;

/**
 * @author tolmalev
 */
public class AndElement extends LogicElement {
    public AndElement(CalculationManager manager, int inputsCount) {
        super(manager, inputsCount, true, (a, b) -> a && b);
    }
}
