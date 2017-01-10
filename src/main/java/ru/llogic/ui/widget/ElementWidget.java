package ru.llogic.ui.widget;

import javafx.scene.layout.Region;
import ru.llogic.core.Element;

/**
 * @author tolmalev
 */
public abstract class ElementWidget<T extends Element> extends Region {
    protected final T element;

    protected ElementWidget(T element) {
        this.element = element;

        getStylesheets().add("ru/llogic/ui/main.css");
        getStylesheets().add("ru/llogic/ui/widget/default_elements.css");
        getStyleClass().add("element");

        getStyleClass().add(getClass().getSimpleName());
    }
}
