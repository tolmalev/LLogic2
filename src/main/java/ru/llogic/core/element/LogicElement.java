package ru.llogic.core.element;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.llogic.core.CalculationManager;
import ru.llogic.core.Point;
import ru.llogic.core.PointState;

/**
 * @author tolmalev
 */
public abstract class LogicElement extends SimpleIoElement {
    private final boolean base;
    private final BiFunction<Boolean, Boolean, Boolean> fn;

    public LogicElement(CalculationManager manager,
            int inputsCount,
            boolean base,
            BiFunction<Boolean, Boolean, Boolean> fn)
    {
        super(manager, inputsCount, Arrays.asList((Function<List<PointState>, PointState>) inputs -> {
            boolean res = base;
            for (boolean input : inputs.stream().map(PointState::asBool).collect(Collectors.toList())) {
                res = fn.apply(res, input);
            }
            return res ? PointState.HIGH : PointState.LOW;
        }));

        this.base = base;
        this.fn = fn;
    }

    protected List<Boolean> getInputStatesAsBool() {
        return getInputStates()
                .stream()
                .map(PointState::asBool)
                .collect(Collectors.toList());
    }

    public Point getOutputPoint() {
        return getOutputPoints().get(0);
    }
}
