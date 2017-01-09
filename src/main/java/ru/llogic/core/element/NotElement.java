package ru.llogic.core.element;

import ru.llogic.core.CalculationManager;

/**
 * @author tolmalev
 */
public class NotElement extends LogicElement {
    public NotElement(CalculationManager manager) {
        super(manager, 1, true, (a, b) -> a ^ b);
    }
}
