package com.geosis.api;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.control.Button;
import javafx.scene.control.*;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vbox;

    @FXML
    private Pane paneInterval;

    @FXML
    private Pane paneEvolution;

    @FXML
    private TextField scientificName;

    @FXML
    private Button btnSearch;

    @FXML
    private TextField anneeDeb1;

    // for earth
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;

    public Group earth;

    public Controller(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resource){

/*
        RangeSlider rangeSlider = new RangeSlider(1900, 2021, 1900, 2021);
        rangeSlider.setShowTickLabels(true);
        rangeSlider.setShowTickMarks(true);
        rangeSlider.setMajorTickUnit(25);
        rangeSlider.setBlockIncrement(10);
        rangeSlider.setPrefWidth(280);

        paneInterval.getChildren().add(rangeSlider);
 */


        btnSearch.setOnAction(actionEvent -> {
            String s = scientificName.getText();
            System.out.println(s);
        });


        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();

        // Load geometry
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = this.getClass().getResource("Earth/earth.obj");
            objImporter.read(modelUrl);
        } catch (
                ImportException e) {
            // handle exception
            System.out.println(e.getMessage());
        }
        MeshView[] meshViews = objImporter.getImport();
        earth = new Group(meshViews);

        root3D.getChildren().add(earth);
        root3D.setFocusTraversable(true);

        // Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);
        new CameraManager(camera, anchorPane, root3D);

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);

        SubScene subScene = new SubScene(root3D,500, 500, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        //subScene.setFill(Color.GRAY);
        subScene.translateXProperty().setValue(25);
        subScene.translateYProperty().setValue(100);
        anchorPane.getChildren().addAll(subScene);

    }




}
