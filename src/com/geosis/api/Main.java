package com.geosis.api;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

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
