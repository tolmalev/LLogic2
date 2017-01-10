package ru.llogic.ui.widget;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Region;
import ru.llogic.core.Element;

/**
 * @author tolmalev
 */
public abstract class ElementWidget<T extends Element> extends Region {
    protected final T element;

    protected ElementWidget(T element) {
        this.element = element;

//        FXMLLoader loader = new FXMLLoader();
//        loader.setController(this);
//        try {
//            getChildren().add(loader.load(getClass().getResource("element.fxml")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        getStylesheets().add("ru/llogic/ui/main.css");
        getStylesheets().add("ru/llogic/ui/widget/default_elements.css");
        getStyleClass().add("element");

        getStyleClass().add(getClass().getSimpleName());
    }
}
