package ru.llogic.ui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.print.Doc;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

/**
 * @author tolmalev
 */
public class UiController {
    private static final Logger loger = Logger.getLogger(UiController.class.getName());

    private Node root;
    private TabPane mainTabPanel;

    private Map<Tab, DocumentManager> documents = new ConcurrentHashMap<>();
    private DocumentManager activeDocument;

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
        } catch (IOException e) {
            loger.warning("Failed to create tab: " + e.getLocalizedMessage());
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
