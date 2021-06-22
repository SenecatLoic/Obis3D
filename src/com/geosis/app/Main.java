package com.geosis.app;

import com.geosis.api.loader.JsonLoaderZoneSpecies;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import com.geosis.app.earth.Earth;
import com.geosis.app.exception.EmptyException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.File;

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

            primaryStage.setMinWidth(1071);
            primaryStage.setMinHeight(670);
            primaryStage.setTitle("OBIS 3D");
            primaryStage.setScene(scene);
            //icon
            File file = new File("resources/icon.jpg");
            primaryStage.getIcons().add(new Image(file.toURI().toURL().toString()));

            scene.getStylesheets().add("com/geosis/app/styleSheet.css");
            primaryStage.show();
            primaryStage.widthProperty().addListener((old,oldVal,newVal)->{
                Earth.setSizeDiffX((newVal.intValue() - oldVal.intValue())/2);
            });

            primaryStage.heightProperty().addListener((old,oldVal,newVal)->{
                Earth.setSizeDiffY((newVal.intValue() - oldVal.intValue())/2);
            });

            JsonLoaderZoneSpecies jsonLoaderZoneSpecies = new JsonLoaderZoneSpecies("resources/Selachii.json");
            ApiZoneSpeciesResponse response = jsonLoaderZoneSpecies.getZoneSpeciesByName("Selachii");
            try{
                controller.displayZone(response,false);
                controller.setTextFieldSearch("Selachii");
            }catch (EmptyException e){
                //on fait rien
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

}
