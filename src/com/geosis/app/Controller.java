package com.geosis.app;

import com.geosis.api.loader.LoaderSpecies;
import com.geosis.api.loader.LoaderZoneSpecies;
import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import javafx.geometry.Point2D;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vbox;

    @FXML
    private TextField scientificName;

    @FXML
    private Button btnSearch, btnStart, btnBreak, btnStop, btnReset, btnClose;

    @FXML
    private TextField yearStart, yearEnd;

    @FXML
    private Rectangle color1, color2, color3, color4, color5, color6, color7, color8;

    @FXML
    private Label labelColor1, labelColor2, labelColor3, labelColor4, labelColor5, labelColor6, labelColor7, labelColor8;

    // for earth
    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;

    private Group earth;
    private int yearStartInt = Integer.MIN_VALUE;
    private int yearEndInt = Integer.MAX_VALUE;
    ArrayList<String> nameSearch = new ArrayList<>();
    boolean nameExist = false;

    public Controller(){ }

    @Override
    public void initialize(URL location, ResourceBundle resource) {

        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();
        createEarth(root3D);

        LoaderSpecies loader = LoaderSpecies.createLoaderSpecies();
        LoaderZoneSpecies loaderZoneSpecies = LoaderZoneSpecies.createLoaderSpecies();


        root3D.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isControlDown())
            {
                PickResult pickResult = event.getPickResult();
                Point3D spaceCoord = pickResult.getIntersectedPoint();
                System.out.println(spaceCoord);
                Point2D point2D = point3DtoGeoCoord(spaceCoord);
                System.out.println(point2D.getX() + "    " + point2D.getY());
                Point3D space = geoCoordTo3dCoord((float)point2D.getX(), (float) point2D.getY());
                System.out.println(space);

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


        scientificName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                ApiNameResponse apiNameResponse = loader.getNames(newValue);

                nameSearch = apiNameResponse.getData();

                System.out.println(apiNameResponse.getData());
            }
        });

        btnClose.setOnAction(actionEvent -> {
            Platform.exit();
        });

        btnReset.setOnAction(actionEvent -> {
            actionBtnReset();
        });

        btnSearch.setOnAction(actionEvent -> {

            String s = scientificName.getText();

            actionBtnSearch(s);

        });
/*
        btnStop.setOnAction(actionEvent -> {

            String s = scientificName.getText();

            if (!yearStart.getText().isEmpty() && !yearEnd.getText().isEmpty()) {
                try {
                    yearStartInt = Integer.parseInt(yearStart.getText());
                    yearEndInt = Integer.parseInt(yearEnd.getText());

                    createGraph(loaderZoneSpecies, s, yearStartInt, yearEndInt);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("L'année pour l'évolution n'est pas valide");
                    alert.show();
                }
            }
        });
*/
        btnStart.setOnAction(actionEvent -> {

            String s = scientificName.getText();

            //System.out.println(s);

            // vérifie si le contenu du textfield existe dans les data
            for (String n : nameSearch) {
                if (n.equalsIgnoreCase(s)) {
                    nameExist = true;
                }
            }

            if (nameSearch.isEmpty() || !nameExist) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Le nom scientifique n'est pas valide");
                alert.show();
            } else if (!yearStart.getText().isEmpty() && !yearEnd.getText().isEmpty()) {
                try {
                    yearStartInt = Integer.parseInt(yearStart.getText());
                    yearEndInt = Integer.parseInt(yearEnd.getText());

                    float nbRep = (((float) yearEndInt - (float) yearStartInt ) / 5) + 1;

                    int i = 0;
                    int j = 5;

                    while (nbRep > 1) {

                        // supprime tous les anciens polygones sur le globe
                        while (earth.getChildren().size() > 1) {
                            earth.getChildren().remove(1);
                        }

                        System.out.println(s + "   " + (yearStartInt + i) + "   " + (yearStartInt + j));

                        afficheZoneByTime(s, yearStartInt + i, yearStartInt + j);

                        i += 5;
                        j += 5;
                        nbRep--;
                    }
                }
                catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("L'année pour l'évolution n'est pas valide");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("L'année évolution est vide. Merci de la compléter");
                alert.show();
            }

        });

    }

    public void createGraph(LoaderZoneSpecies loaderZoneSpecies, String name, int anneeStart, int anneeEnd){

        ApiZoneSpeciesResponse apiZoneSpeciesResponse = loaderZoneSpecies.getZoneSpeciesByTime(name, anneeStart, anneeEnd);

        System.out.println(apiZoneSpeciesResponse.getNbSignalsMax());

        // Création des séries.
        final int minX = anneeStart;
        final int maxX = anneeEnd;
        int minY = apiZoneSpeciesResponse.getNbSignalsMin();
        int maxY = apiZoneSpeciesResponse.getNbSignalsMax();

        final LineChart.Series series  = new LineChart.Series<>();
        for (int x = minX ; x <= maxX ; x+=5) {
            int value = 0;
            ApiZoneSpeciesResponse apiZoneSpeciesResponseX = loaderZoneSpecies.getZoneSpeciesByTime(name, x, x);
            for (ZoneSpecies zoneSpecies : apiZoneSpeciesResponseX.getData()) {
                value += zoneSpecies.getNbSignals();
            }
            if(value > maxY){maxY = value;}
            if(value < minY){minY = value;}
            final LineChart.Data data = new LineChart.Data(x, value);
            series.getData().add(data);
        }

        // Création du graphique.
        final NumberAxis xAxis = new NumberAxis(minX, maxX, 5);
        xAxis.setLabel("Year");
        final NumberAxis yAxis = new NumberAxis(minY, maxY, (maxY - minY) / 8);
        yAxis.setLabel("Number of signalements");
        final LineChart chart = new LineChart(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setMaxHeight(300);
        chart.getData().setAll(series);
        vbox.getChildren().add(chart);

    }

    public void actionBtnReset(){

        // supprime tous les anciens polygones sur le globe
        while (earth.getChildren().size() > 1) {
            earth.getChildren().remove(1);
        }

        scientificName.setText("");
        yearStart.setText("");
        yearEnd.setText("");


        // Todo suppr les résultats + graph

    }

    public void actionBtnSearch(String name){

        // vérifie si le contenu du textfield existe dans les data
        for (String n : nameSearch) {
            if(n.equalsIgnoreCase(name)){
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

            if(yearStart.getText().isEmpty() && yearEnd.getText().isEmpty()){
                afficheZoneByName(name);
            }
            else{
                try{
                    yearStartInt = Integer.parseInt(yearStart.getText());
                }
                catch (NumberFormatException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("L'année n'est pas valide");
                    alert.show();
                }
                try{
                    yearEndInt = Integer.parseInt(yearEnd.getText());
                }
                catch (NumberFormatException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("L'année n'est pas valide");
                    alert.show();
                }
                afficheZoneByTime(name, yearStartInt, yearEndInt);
            }
        }
    }

    public void afficheZoneByName(String name){

        //ProgressBar progressBar = new ProgressBar();

        LoaderZoneSpecies loaderZoneSpecies = LoaderZoneSpecies.createLoaderSpecies();

        ApiZoneSpeciesResponse apiZoneSpeciesResponse = loaderZoneSpecies.getZoneSpeciesByName(name);

        int minNbSignals = apiZoneSpeciesResponse.getNbSignalsMin();
        int maxNbSignals = apiZoneSpeciesResponse.getNbSignalsMax();

        setLegend(minNbSignals, maxNbSignals);

        for (ZoneSpecies zoneSpecies : apiZoneSpeciesResponse.getData()) {
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

        color1.setVisible(true);
        color2.setVisible(true);
        color3.setVisible(true);
        color4.setVisible(true);
        color5.setVisible(true);
        color6.setVisible(true);
        color7.setVisible(true);
        color8.setVisible(true);

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

    /**
     * Convertir des Coordonnées (méthode du tutoriel)
     * @param lat
     * @param lon
     * @return Point3D créé à partir de latitude et longitude
     */
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

    public static Point2D point3DtoGeoCoord(Point3D point3D){

        float latitude = (float) Math.acos(point3D.getZ());
        float longitude = (float) Math.atan2(point3D.getY(), point3D.getX());

        return new Point2D(latitude, longitude);

    }

}
