package ru.llogic.ui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author tolmalev
 */
public class LLogicMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        UiController uiController = new UiController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main2.fxml"));
        loader.setController(uiController);
        Parent root = loader.load();

        uiController.setRoot(root);

        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        uiController.addTab(null);

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

//        AtomicInteger i = new AtomicInteger();


//        addTab(mainTabPanel);

//        mainTabPanel.setOnMouseClicked(event -> {
//            if ((event.getTarget().equals(mainTabPanel) || event.getTarget() instanceof StackPane)
//                    && event.getButton().equals(MouseButton.PRIMARY)
//                    && event.getClickCount() == 2)
//            {
//                try {
//                    mainTabPanel.getTabs().add(FXMLLoader.load(getClass().getResource("edit_panel.fxml")));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private void addTab(TabPane mainTabPanel) throws IOException {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
