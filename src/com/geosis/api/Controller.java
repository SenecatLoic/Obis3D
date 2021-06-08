package com.geosis.api;

import com.geosis.api.loader.HttpLoaderSpecies;
import com.geosis.api.loader.LoaderSpecies;
import com.geosis.api.loader.LoaderZoneSpecies;
import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import javafx.geometry.Point2D;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
    private TextField anneeDeb1, anneeFin1;

    @FXML
    private Rectangle color1, color2, color3, color4, color5, color6, color7, color8;

    @FXML
    private Label labelColor1, labelColor2, labelColor3, labelColor4, labelColor5, labelColor6, labelColor7, labelColor8;

    // for earth
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;

    private Group earth;
    private int anneeStart = Integer.MIN_VALUE;
    private int anneeEnd = Integer.MAX_VALUE;

    ArrayList<String> nameSearch = new ArrayList<>();
    boolean nameExist = false;

    public Controller(){ }

    @Override
    public void initialize(URL location, ResourceBundle resource) {

        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();
        createEarth(root3D);

        LoaderSpecies loader = LoaderSpecies.createLoaderSpecies();

        /*
        root3D.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isControlDown())
            {
                PickResult pickResult = event.getPickResult();
                System.out.println(pickResult.getIntersectedPoint());
                Point3D spaceCoord = pickResult.getIntersectedPoint();

                Sphere sphere = new Sphere(0.05);
                final PhongMaterial sphereMaterial = new PhongMaterial();
                sphereMaterial.setSpecularColor(Color.RED);
                sphereMaterial.setDiffuseColor(Color.YELLOW);
                sphere.setMaterial(sphereMaterial);

                sphere.setTranslateX(spaceCoord.getX());
                sphere.setTranslateY(spaceCoord.getY());
                sphere.setTranslateZ(spaceCoord.getZ());

                earth.getChildren().add(sphere);

            }
        });
         */

        scientificName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                ApiNameResponse apiNameResponse = loader.getNames(newValue);

                nameSearch = apiNameResponse.getData();

                System.out.println(apiNameResponse.getData());

            }
        });


        btnSearch.setOnAction(actionEvent -> {
            String s = scientificName.getText();

            // vérifie si le contenu du textfield existe dans les data
            for (String name : nameSearch) {
                if(name.equalsIgnoreCase(s)){
                    nameExist = true;
                }
            }

            if(nameSearch.isEmpty() || !nameExist){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Le nom scientifique n'est pas valide");
                alert.show();
            }
            else {
                // supprime tous les anciens polygones sur le globe
                while(earth.getChildren().size() > 1){
                    earth.getChildren().remove(1);
                }

                if(anneeDeb1.getText().isEmpty() && anneeFin1.getText().isEmpty()){
                    afficheZoneByName(s);
                }
                else{
                    try{
                        anneeStart = Integer.parseInt(anneeDeb1.getText());
                    }
                    catch (NumberFormatException e){

                    }
                    try{
                        anneeEnd = Integer.parseInt(anneeFin1.getText());
                    }
                    catch (NumberFormatException e){

                    }
                    afficheZoneByTime(s, anneeStart, anneeEnd);

                }
            }

        });

    }

    public void afficheZoneByName(String name){

        LoaderZoneSpecies loaderZoneSpecies = LoaderZoneSpecies.createLoaderSpecies();

        ApiZoneSpeciesResponse apiZoneSpeciesResponse = loaderZoneSpecies.getZoneSpeciesByName(name);

        int minNbSignals = apiZoneSpeciesResponse.getNbSignalsMin();
        int maxNbSignals = apiZoneSpeciesResponse.getNbSignalsMax();

        setLegend(minNbSignals, maxNbSignals);

        for (ZoneSpecies zoneSpecies : apiZoneSpeciesResponse.getData()) {
            System.out.println(zoneSpecies.getNbSignals());
            addPolygon(earth, zoneSpecies.getZone().getCoords(), setColor(zoneSpecies, minNbSignals, maxNbSignals));
        }
    }

    public void afficheZoneByTime(String name, int anneeStart, int anneeEnd){

        LoaderZoneSpecies loaderZoneSpecies = LoaderZoneSpecies.createLoaderSpecies();

        ApiZoneSpeciesResponse apiZoneSpeciesResponse = loaderZoneSpecies.getZoneSpeciesByTime(name, anneeStart, anneeEnd);

        int minNbSignals = apiZoneSpeciesResponse.getNbSignalsMin();
        int maxNbSignals = apiZoneSpeciesResponse.getNbSignalsMax();

        setLegend(minNbSignals, maxNbSignals);

        for (ZoneSpecies zoneSpecies : apiZoneSpeciesResponse.getData()) {
            addPolygon(earth, zoneSpecies.getZone().getCoords(), setColor(zoneSpecies, minNbSignals, maxNbSignals));
        }

    }

    public void setLegend(int minNbSignals, int maxNbSignals){

        int interval = (maxNbSignals - minNbSignals) / 8;

        labelColor1.setText("De " + minNbSignals + " à " + (minNbSignals + interval) + " signalements");
        labelColor2.setText("De " + (minNbSignals + interval + 1) + " à " + (minNbSignals + 2 * interval) + " signalements");
        labelColor3.setText("De " + (minNbSignals + 2 * interval + 1) + " à " + (minNbSignals + 3 * interval) + " signalements");
        labelColor4.setText("De " + (minNbSignals + 3 * interval + 1) + " à " + (minNbSignals + 4 * interval) + " signalements");
        labelColor5.setText("De " + (minNbSignals + 4 * interval + 1) + " à " + (minNbSignals + 5 * interval) + " signalements");
        labelColor6.setText("De " + (minNbSignals + 5 * interval + 1) + " à " + (minNbSignals + 6 * interval) + " signalements");
        labelColor7.setText("De " + (minNbSignals + 6 * interval + 1) + " à " + (minNbSignals + 7 * interval) + " signalements");
        labelColor8.setText("De " + (minNbSignals + 7 * interval + 1) + " à " + (maxNbSignals) + " signalements");

    }

    public Color setColor(ZoneSpecies zoneSpecies, int minNbSignals, int maxNbSignals){

        int interval = (maxNbSignals - minNbSignals) / 8;

        int nbSignals = zoneSpecies.getNbSignals();

         if(nbSignals <= minNbSignals + interval && nbSignals >= minNbSignals){
             return (Color)color1.getFill();
         }
         else if(nbSignals <= minNbSignals + 2 * interval && nbSignals > nbSignals + interval){
             return (Color)color2.getFill();
         }
         else if(nbSignals <= minNbSignals + 3 * interval && nbSignals > minNbSignals + 2 * interval){
             return (Color)color3.getFill();
         }
         else if(nbSignals <= minNbSignals + 4 * interval && nbSignals > minNbSignals + 3 * interval){
             return (Color)color4.getFill();
         }
         else if(nbSignals <= minNbSignals + 5 * interval && nbSignals > minNbSignals + 4 * interval){
             return (Color)color5.getFill();
         }
         else if(nbSignals <= minNbSignals + 6 * interval && nbSignals > minNbSignals + 5 * interval){
             return (Color)color6.getFill();
         }
         else if(nbSignals <= minNbSignals + 7 * interval && nbSignals >= minNbSignals + 6 * interval + 1){
             return (Color)color7.getFill();
         }
         else if(nbSignals < maxNbSignals && nbSignals > minNbSignals + 7 * interval + 1){
             return (Color)color8.getFill();
         }
         else {
             return null;
         }

    }

    public void addPolygon(Group parent, Point2D[] coords, Color color){

        final TriangleMesh triangleMesh = new TriangleMesh();

        Point3D coord1 = geoCoordTo3dCoord((float)coords[0].getX(), (float)coords[0].getY());
        Point3D coord2 = geoCoordTo3dCoord((float)coords[1].getX(), (float)coords[1].getY());
        Point3D coord3 = geoCoordTo3dCoord((float)coords[2].getX(), (float)coords[2].getY());
        Point3D coord4 = geoCoordTo3dCoord((float)coords[3].getX(), (float)coords[3].getY());
        Point3D coord5 = geoCoordTo3dCoord((float)coords[4].getX(), (float)coords[4].getY());

        final float[] points = {
                (float)coord1.getX(), (float) coord1.getY(), (float)coord1.getZ(),
                (float)coord2.getX(), (float) coord2.getY(), (float)coord2.getZ(),
                (float)coord3.getX(), (float) coord3.getY(), (float)coord3.getZ(),
                (float)coord4.getX(), (float) coord4.getY(), (float)coord4.getZ(),
                (float)coord5.getX(), (float) coord5.getY(), (float)coord5.getZ(),
        };

        final float[] texCoords = {
                1, 1,
                1, 0,
                1, -1,
                0, 1,
                0, 0
        };

        final int[] faces = {
                0, 0, 1, 1, 2, 2,
                0, 0, 2, 2, 3, 3,
                0, 0, 3, 3, 4, 4
        };

        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().setAll(texCoords);
        triangleMesh.getFaces().setAll(faces);

        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);

        final MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);
        // permet de voir les faces avant et arrière des formes
        meshView.setCullFace(CullFace.NONE);
        parent.getChildren().addAll(meshView);
    }

    public static Point3D geoCoordTo3dCoord(float lat, float lon) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)),
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor)),
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor)));
    }


    public void createEarth(Group root3D){
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
        subScene.translateYProperty().setValue(25);
        anchorPane.getChildren().addAll(subScene);
    }


}
