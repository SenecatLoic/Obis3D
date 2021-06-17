package com.geosis.app;

import com.geosis.app.earth.Earth;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) {

        try{
            controller = new Controller();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("interface.fxml"));
            loader.setController(controller);

            Pane root = loader.load();

            Scene scene = new Scene(root, 1000, 670, false,SceneAntialiasing.BALANCED);

            primaryStage.setMinWidth(1021);
            primaryStage.setMinHeight(670);
            primaryStage.setTitle("OBIS 3D");
            primaryStage.setScene(scene);
            scene.getStylesheets().add("com/geosis/app/styleSheet.css");
            primaryStage.show();
            primaryStage.widthProperty().addListener((old,oldVal,newVal)->{
                Earth.setSizeDiffX((newVal.intValue() - oldVal.intValue())/2);
            });

            primaryStage.heightProperty().addListener((old,oldVal,newVal)->{
                Earth.setSizeDiffY((newVal.intValue() - oldVal.intValue())/2);
            });
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}
