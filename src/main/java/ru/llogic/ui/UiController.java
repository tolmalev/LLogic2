package ru.llogic.ui;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.llogic.core.element.AndElement;
import ru.llogic.core.element.InElement;
import ru.llogic.core.element.PointElement;

/**
 * @author tolmalev
 */
public class UiController {
    private static final Logger loger = LogManager.getLogger(UiController.class);

    private Node root;
    private TabPane mainTabPanel;

    private Map<Tab, DocumentManager> documents = new ConcurrentHashMap<>();
    private DocumentManager activeDocument;

    @FXML
    public void activateSelector(ActionEvent event) {
        if (activeDocument != null) {
            activeDocument.activateSelector();
        }
    }

    @FXML
    public void activateAddAndElement(ActionEvent event) {
        if (activeDocument != null) {
            activeDocument.activateAddAndElement();
        }
    }

    @FXML
    public void activateConnect(ActionEvent event) {
        if (activeDocument != null) {
            activeDocument.activateConnect();
        }
    }

    @FXML
    public void activateAddByteIn(ActionEvent event) {
        if (activeDocument != null) {
            activeDocument.activateAddByteIn();
        }
    }

    @FXML
    public void addTab(ActionEvent event) {
        Pane editPane = null;
        try {
            editPane = FXMLLoader.load(getClass().getResource("edit_panel.fxml"));
            Tab tab = new Tab("Untitled document", editPane);

            DocumentManager documentManager = new DocumentManager(this, editPane);

            documents.put(tab, documentManager);

            mainTabPanel.getTabs().add(tab);

            if (activeDocument != null) {
                activeDocument.setActive(false);
            }
            activeDocument = documentManager;
            activeDocument.setActive(true);

            mainTabPanel.getSelectionModel().select(tab);

            AndElement and1 = documentManager.addAndElement(100, 50);
            AndElement and2 = documentManager.addAndElement(150, 150);

            InElement in1 = documentManager.addInElement(20, 50);
            InElement in2 = documentManager.addInElement(20, 100);

            PointElement pe = documentManager.addPointElement(100, 300);

            documentManager.getCalculationManager()
                    .addConnection(in1.getOutputPoint(), and1.getInputPoints().get(0));
            documentManager.getCalculationManager()
                    .addConnection(in2.getOutputPoint(), and1.getInputPoints().get(1));

            documentManager.getCalculationManager().addConnection(in2.getOutputPoint(), pe.getPoint());
            documentManager.getCalculationManager().addConnection(pe.getPoint(), and2.getInputPoints().get(1));

            documentManager.getCalculationManager()
                    .addConnection(and1.getOutputPoint(), and2.getInputPoints().get(0));
        } catch (IOException e) {
            loger.error("Failed to create tab", e);
        }
    }

    public void setRoot(Node root) {
        this.root = root;

        mainTabPanel = (TabPane) root.lookup("#main_tab_panel");
        mainTabPanel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                Tab tab = observable.getValue();

                if (activeDocument != null) {
                    activeDocument.setActive(false);
                }
                activeDocument = documents.get(tab);
                activeDocument.setActive(true);
            }
        });
    }
}
