package ru.llogic.core.element;

import ru.llogic.core.CalculationManager;

/**
 * @author tolmalev
 */
public class XorElement extends LogicElement {
    public XorElement(CalculationManager manager, int inputsCount) {
        super(manager, inputsCount, false, (a, b) -> a ^ b);
    }
}
