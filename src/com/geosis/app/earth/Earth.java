package com.geosis.app.earth;

import com.geosis.app.CameraManager;
import com.geosis.app.Controller;
import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;

import java.net.URL;

public class Earth {

    private static Group earth;
    private static SubScene subScene;

    /**
     * Crée l'objet Earth
     * @see com.geosis.app.earth
     * @see CameraManager
     * @param root3D
     */
    public static Group createEarth (Controller controller, Group root3D, AnchorPane anchorPane){
        // Load geometry
        ObjModelImporter objImporter = new ObjModelImporter();
        try {
            URL modelUrl = controller.getClass().getResource("Earth/earth.obj");
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

        subScene = new SubScene(root3D, 492, 497, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        //subScene.setFill(Color.GREY);
        subScene.translateYProperty().setValue(25);

        anchorPane.getChildren().addAll(subScene);

        return earth;
    }

    /**
     * Méthode qui change la position de la planète en fonction d'un écart
     * @param width la différence de largeur
     */
    public static void setSizeDiffX(int width){
        subScene.translateXProperty().setValue(subScene.translateXProperty().getValue() + width);
    }

    /**
     * Méthode qui change la position de la planète
     * @param height La différence de longueur
     */
    public static void setSizeDiffY(int height){
        subScene.translateYProperty().setValue(subScene.translateYProperty().getValue() + height);
    }



}
