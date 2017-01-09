package ru.llogic.core.element;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.llogic.core.CalculationManager;
import ru.llogic.core.Element;
import ru.llogic.core.PointState;

/**
 * @author tolmalev
 */
public class SimpleIoElement extends Element {
    private int inputsCount;

    private final List<Function<List<PointState>, PointState>> outputCalculators;

    public SimpleIoElement(
            CalculationManager manager,
            int inputsCount,
            List<Function<List<PointState>, PointState>> outputCalculators)
    {
        super(manager, inputsCount + outputCalculators.size());

        this.inputsCount = inputsCount;
        this.outputCalculators = outputCalculators;
    }

    protected void addInput() {
        addPoint(inputsCount);
        inputsCount++;
    }

    protected List<PointState> getInputStates() {
        return getPointStates().subList(0, inputsCount);
    }

    @Override
    public void doCalculate() {
        List<PointState> inputs = getInputStates();

        List<PointState> outputs = calculateOutputs(inputs);

        int i = 0;
        for (PointState state: outputs) {
            updatePointState(inputsCount + i, state);
            i++;
        }
    }

    protected List<PointState> calculateOutputs(List<PointState> inputs) {
        return outputCalculators.stream().map(calculator -> calculator.apply(inputs)).collect(Collectors.toList());
    }
}