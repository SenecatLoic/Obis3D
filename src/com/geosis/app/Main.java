package com.geosis.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {

    private Controller controller = new Controller();

    @Override
    public void start(Stage primaryStage) {

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("interface.fxml"));
            loader.setController(controller);
            Pane root = loader.load();
            Scene scene = new Scene(root, 1000, 670, false,SceneAntialiasing.BALANCED);

            primaryStage.setTitle("OBIS 3D");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}
