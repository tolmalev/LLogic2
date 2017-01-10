package ru.llogic.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author tolmalev
 */
public class LLogicMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        UiController uiController = new UiController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
