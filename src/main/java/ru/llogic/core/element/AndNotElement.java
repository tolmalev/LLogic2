package ru.llogic.core.element;

import java.util.List;
import java.util.stream.Collectors;

import ru.llogic.core.CalculationManager;
import ru.llogic.core.PointState;

/**
 * @author tolmalev
 */
public class AndNotElement extends AndElement {
    public AndNotElement(CalculationManager manager, int inputsCount) {
        super(manager, inputsCount);
    }

    @Override
    protected List<PointState> calculateOutputs(List<PointState> inputs) {
        return super.calculateOutputs(inputs)
                .stream()
                .map(ps -> ps == PointState.HIGH ? PointState.LOW : PointState.HIGH)
                .collect(Collectors.toList());
    }
}
