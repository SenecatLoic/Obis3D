package com.geosis.app;

import com.geosis.api.loader.LoaderSpecies;
import com.geosis.api.loader.LoaderZoneSpecies;
import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import com.geosis.app.exception.EmptyException;
import com.geosis.app.exception.InputException;
import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;

import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.geosis.app.GeometryTools.*;
import com.geosis.app.exception.InputException;

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

    @FXML
    private ListView<String> listView;

    private Group earth;
    private int yearStartInt = Integer.MIN_VALUE;
    private int yearEndInt = Integer.MAX_VALUE;
    ArrayList<String> nameSearch = new ArrayList<>();
    boolean nameExist = false;

    // Create Graph
    private final LineChart.Series series = new LineChart.Series<>();
    private NumberAxis xAxis = new NumberAxis(0, 0, 5);
    private NumberAxis yAxis = new NumberAxis(0, 0, 0);
    private AreaChart chart = new AreaChart(xAxis, yAxis);
    private int minY = 0;
    private int maxY = 0;
    private int tickUnit = 0;

    int finalCurrentYear;


    public Controller() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {

        listView.setVisible(false);

        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();
        createEarth(root3D);

        //Rotate the earth
        ToggleSwitchRotation toggleSwitchRotation = new ToggleSwitchRotation(earth, 25);
        toggleSwitchRotation.setTranslateX(120);
        toggleSwitchRotation.setTranslateY(12);
        anchorPane.getChildren().addAll(toggleSwitchRotation);

        LoaderSpecies loader = LoaderSpecies.createLoaderSpecies();
        LoaderZoneSpecies loaderZoneSpecies = LoaderZoneSpecies.createLoaderSpecies();

        scientificName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                ApiNameResponse apiNameResponse = loader.getNames(newValue);

                nameSearch = apiNameResponse.getData();

                ObservableList<String> names = FXCollections.observableArrayList(nameSearch);

                listView.setVisible(true);

                listView.setItems(names);

                //System.out.println(apiNameResponse.getData());
            }
        });

        listView.setOnMouseClicked(event -> {
            // mettre à jour le Textfield scientificName en sélectionnant l'espèce + appui sur ENTREE
            listView.addEventHandler(KeyEvent.KEY_PRESSED, eventKey -> {
                if(eventKey.getCode() == KeyCode.ENTER){
                    String nameClicked = listView.getSelectionModel().getSelectedItem();
                    scientificName.setText(nameClicked);
                }
            });
            // mettre à jour le Textfield scientificName en double cliquant
            if(event.getClickCount() == 2){
                String nameClicked = listView.getSelectionModel().getSelectedItem();
                scientificName.setText(nameClicked);
            }
        });

        root3D.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isControlDown()) {
                PickResult pickResult = event.getPickResult();
                Point3D spaceCoord = pickResult.getIntersectedPoint();
                System.out.println(spaceCoord);
                Point2D point2D = GeometryTools.spaceCoordToGeoCoord(spaceCoord);
                System.out.println(point2D.getX() + "    " + point2D.getY());
                Point3D space = geoCoordTo3dCoord((float) point2D.getX(), (float) point2D.getY());
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
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isShiftDown()) {
                PickResult pickResult = event.getPickResult();
                Point3D spaceCoord = pickResult.getIntersectedPoint();
                System.out.println(spaceCoord);
                Point2D point2D = GeometryTools.spaceCoordToGeoCoord(spaceCoord);
                System.out.println(point2D.getX() + "    " + point2D.getY());
                Point3D space = geoCoordTo3dCoord((float)point2D.getX(), (float) point2D.getY());
                System.out.println(space);

                Box box = new Box(0.1, 2, 0.1);
                final PhongMaterial sphereMaterial = new PhongMaterial();
                sphereMaterial.setDiffuseColor(new Color(1, 0, 0, 0.3));
                box.setMaterial(sphereMaterial);


                //box.setRotationAxis(new Point3D(1, 1, (Math.cos(Math.toRadians(point2D.getY())) + Math.sin(Math.toRadians(point2D.getY()))) / -Math.tan(Math.toRadians(point2D.getX()))));
                box.setRotationAxis(new Point3D(0, 1, 0));
                box.setRotate(point2D.getY());

                box.setRotationAxis(new Point3D(1, 0, 0));
                box.setRotate(point2D.getX());


                box.setTranslateX(spaceCoord.getX());
                box.setTranslateY(spaceCoord.getY());
                box.setTranslateZ(spaceCoord.getZ());

                earth.getChildren().add(box);

            }
        });

        btnBreak.setOnAction(actionEvent -> {

            for(int lat = -90; lat <= 90; lat+=30){
                for(int lon = -180; lon <= 180; lon+=30) {

                    Box box = new Box(0.1, 0.1, 0.5);
                    final PhongMaterial sphereMaterial = new PhongMaterial();
                    sphereMaterial.setDiffuseColor(new Color(1, 0, 0, 0.3));
                    box.setMaterial(sphereMaterial);

                    box.setRotationAxis(new Point3D(1, 0, 0));
                    box.setRotate(lat);

                    box.setRotationAxis(new Point3D(0, 1, 0));
                    box.setRotate(lon);



                    Point3D spaceCoord = geoCoordTo3dCoord(lat, lon);

                    box.setTranslateX(spaceCoord.getX());
                    box.setTranslateY(spaceCoord.getY());
                    box.setTranslateZ(spaceCoord.getZ());

                    earth.getChildren().add(box);
                }

            }

        });

        btnReset.setOnAction(actionEvent -> {
            actionBtnReset();
        });

        btnClose.setOnAction(actionEvent -> {
            Platform.exit();
        });

        btnSearch.setOnAction(actionEvent -> {

            String s = scientificName.getText();

            try {
                actionBtnSearch(s);
            } catch (InputException | EmptyException e) {
                // todo faire qqch ?
            }

        });

        btnStop.setOnAction(actionEvent -> {



        });

        btnStart.setOnAction(actionEvent -> {

            String s = scientificName.getText();

            try {
                actionBtnStart(loaderZoneSpecies, s);
            } catch (InputException | EmptyException e) {
                // todo faire qqch ?
            }

        });

    }

    /**
     * Début de l'affiche d'une évolution
     * @see #createPoint(LoaderZoneSpecies, String, int, int, int)
     * @param loaderZoneSpecies
     * @param name
     * @throws InputException
     */
    public void actionBtnStart(LoaderZoneSpecies loaderZoneSpecies, String name) throws InputException, EmptyException{

        // vérifie si le contenu du textfield existe dans les data
        for (String n : nameSearch) {
            if (n.equalsIgnoreCase(name)) {
                nameExist = true;
            }
        }

        if (nameSearch.isEmpty() || !nameExist) {
            throw new InputException("Le nom scientifique n'est pas valide");
        } else if (!yearStart.getText().isEmpty() && !yearEnd.getText().isEmpty()) {
            try {
                yearStartInt = Integer.parseInt(yearStart.getText());
                yearEndInt = Integer.parseInt(yearEnd.getText());

                double nbRep = (((double) yearEndInt - (double) yearStartInt) / 5);

                ArrayList<CompletableFuture<Object>> completableFutures = loaderZoneSpecies.getZoneSpeciesByInterval(name, 3, yearStartInt, 5, (int) Math.ceil(nbRep));

                for (CompletableFuture<Object> zone : completableFutures) {

                    Task<ApiZoneSpeciesResponse> pollDatabaseTask = new Task<>() {
                        @Override
                        public ApiZoneSpeciesResponse call() {
                            ApiZoneSpeciesResponse zoneSpeciesResponse = null;

                            try {
                                zoneSpeciesResponse = (ApiZoneSpeciesResponse) zone.get(10, TimeUnit.SECONDS);
                                ApiZoneSpeciesResponse finalZoneSpeciesResponse = zoneSpeciesResponse;

                                //nécessaire pour modifier un element javafx
                                Platform.runLater(() -> {
                                    int currentYear = yearStartInt + 5 * finalCurrentYear;
                                    while (earth.getChildren().size() > 1) {
                                        earth.getChildren().remove(1);
                                    }
                                    try {
                                        displayZone(finalZoneSpeciesResponse);
                                    } catch (EmptyException e) {
                                        // todo faire qqch ?
                                    }
                                    createPoint(loaderZoneSpecies, name, currentYear, yearStartInt, yearEndInt);
                                    finalCurrentYear +=1;
                                });

                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                            return zoneSpeciesResponse;
                        }
                    };
                    createPoint(loaderZoneSpecies, name, yearEndInt, yearStartInt, yearEndInt);

                    Thread getItemsThread = new Thread(pollDatabaseTask);
                    getItemsThread.setDaemon(true);
                    getItemsThread.start();
                }

            } catch (NumberFormatException e) {
                throw new InputException("L'année n'est pas valide");
            }
        } else {
            throw new InputException("L'année n'est pas valide");
        }

    }


    /**
     * Crée un nouveau point sur un graphe créé pour
     * @see #createGraph(String, int, int, int, int, XYChart.Series)
     * @param loaderZoneSpecies
     * @param name
     * @param currentYear
     */
    public void createPoint(LoaderZoneSpecies loaderZoneSpecies, String name, int currentYear, int yearStart, int yearEnd){

        ApiZoneSpeciesResponse apiZoneSpeciesResponse = loaderZoneSpecies.getZoneSpeciesByTime(name, currentYear, currentYear);

        int value = 0;
        for (ZoneSpecies zoneSpecies : apiZoneSpeciesResponse.getData()) {
            value += zoneSpecies.getNbSignals();
        }
        if (value > maxY) {
            maxY = value;
        }
        if (value < minY) {
            minY = value;
        }

        final LineChart.Data data = new LineChart.Data(currentYear, value);
        series.getData().add(data);

        createGraph(name, yearStart, yearEnd, minY, maxY, series);

    }

    /**
     * Crée le graphique
     * @see #createGraph(String, int, int, int, int, XYChart.Series)
     * @param name
     * @param yearStart
     * @param yearEnd
     * @param minY
     * @param maxY
     * @param series
     */
    public void createGraph(String name, int yearStart, int yearEnd, int minY, int maxY, XYChart.Series series) {

        // supprimer le graphe d'avant s'il y en a un
        if (vbox.getChildren().get(vbox.getChildren().size() - 1) instanceof AreaChart) {
            vbox.getChildren().remove(vbox.getChildren().size() - 1);
        }

        // Création des séries.
        final int minX = yearStart;
        final int maxX = yearEnd;

        // Création du graphique.
        xAxis.setLowerBound(yearStart);
        xAxis.setUpperBound(yearEnd);
        xAxis.setLabel("Year");

        yAxis.setLabel("Number of signalements");
        tickUnit = (maxY  -minY) / 8;
        yAxis.setLowerBound(minY);
        yAxis.setUpperBound(maxY);
        yAxis.setTickUnit(tickUnit);

        chart.setTitle(name);
        chart.setLegendVisible(false);
        chart.setMaxHeight(290);
        chart.getData().setAll(series);
        Node line = series.getNode().lookup(".chart-series-area-line");
        line.setStyle("-fx-stroke-width: 2px; -fx-stroke:  #59BAFF; -fx-fill:  rgba("+89+","+186+","+255+","+0.5+");");

        vbox.getChildren().add(chart);

    }

    /**
     * Supprime tous les résultats/ entrées de l'interface
     */
    public void actionBtnReset() {

        scientificName.setText("");
        yearStart.setText("");
        yearEnd.setText("");

        // supprime tous les anciens polygones sur le globe
        while (earth.getChildren().size() > 1) {
            earth.getChildren().remove(1);
        }

        // supprimer le graphe d'avant s'il y en a un
        if (vbox.getChildren().get(vbox.getChildren().size() - 1) instanceof AreaChart) {
            vbox.getChildren().remove(vbox.getChildren().size() - 1);
        }

        color1.setVisible(false);
        color2.setVisible(false);
        color3.setVisible(false);
        color4.setVisible(false);
        color5.setVisible(false);
        color6.setVisible(false);
        color7.setVisible(false);
        color8.setVisible(false);

        labelColor1.setText("");
        labelColor2.setText("");
        labelColor3.setText("");
        labelColor4.setText("");
        labelColor5.setText("");
        labelColor6.setText("");
        labelColor7.setText("");
        labelColor8.setText("");


        // Todo suppr les résultats

    }

    /**
     * Action du bouton Search
     * @throws InputException
     * @param name
     * @see #afficheZoneByName(String)
     * @see #afficheZoneByTime(String, int, int)
     */
    public void actionBtnSearch(String name) throws InputException, EmptyException {

        // vérifie si le contenu du textfield existe dans les data
        for (String n : nameSearch) {
            if (n.equalsIgnoreCase(name)) {
                nameExist = true;
            }
        }

        if (nameSearch.isEmpty() || !nameExist) {
            throw new InputException("Le nom scientifique n'est pas valide");
        } else {
            // supprime tous les anciens polygones sur le globe
            while (earth.getChildren().size() > 1) {
                earth.getChildren().remove(1);
            }

            if (yearStart.getText().isEmpty() && yearEnd.getText().isEmpty()) {
                afficheZoneByName(name);
            } else {
                try {
                    yearStartInt = Integer.parseInt(yearStart.getText());
                } catch (NumberFormatException e) {
                    throw new InputException("L'année n'est pas valide");

                }
                try {
                    yearEndInt = Integer.parseInt(yearEnd.getText());
                } catch (NumberFormatException e) {
                    throw new InputException("L'année n'est pas valide");
                }
                afficheZoneByTime(name, yearStartInt, yearEndInt);
            }
        }
    }

    /**
     * Affiche zone
     * @throws EmptyException
     * @see #afficheZoneByName(String)
     * @see #afficheZoneByTime(String, int, int) 
     * @param apiZoneSpeciesResponse
     */
    public void displayZone(ApiZoneSpeciesResponse apiZoneSpeciesResponse) throws EmptyException{

        int minNbSignals = apiZoneSpeciesResponse.getNbSignalsMin();
        int maxNbSignals = apiZoneSpeciesResponse.getNbSignalsMax();

        if(minNbSignals != Integer.MAX_VALUE && maxNbSignals != Integer.MIN_VALUE){
            setLegend(minNbSignals, maxNbSignals);

            for (ZoneSpecies zoneSpecies : apiZoneSpeciesResponse.getData()) {
                GeometryTools.addPolygon(earth, zoneSpecies.getZone().getCoords(), setColor(zoneSpecies, minNbSignals, maxNbSignals));
            }
        } else {
            throw new EmptyException();
        }


    }

    /**
     * Affiche les zones d'une espèce
     * @throws EmptyException
     * @see #displayZone(ApiZoneSpeciesResponse) 
     * @see com.geosis.api.loader.HttpLoaderZoneSpecies#getZoneSpeciesByName(String)
     * @param name
     */
    public void afficheZoneByName (String name) throws EmptyException {

        LoaderZoneSpecies loaderZoneSpecies = LoaderZoneSpecies.createLoaderSpecies();

        ApiZoneSpeciesResponse apiZoneSpeciesResponse = loaderZoneSpecies.getZoneSpeciesByName(name);
        displayZone(apiZoneSpeciesResponse);
    }

    /**
     * Affiche les zones d'une espèce entre 2 années
     * @throws EmptyException
     * @see #displayZone(ApiZoneSpeciesResponse) 
     * @see com.geosis.api.loader.HttpLoaderZoneSpecies#getZoneSpeciesByTime(String, int, int)
     * @param name
     * @param anneeStart
     * @param anneeEnd
     */
    public void afficheZoneByTime (String name,int anneeStart, int anneeEnd) throws EmptyException {

        LoaderZoneSpecies loaderZoneSpecies = LoaderZoneSpecies.createLoaderSpecies();

        ApiZoneSpeciesResponse apiZoneSpeciesResponse = loaderZoneSpecies.getZoneSpeciesByTime(name, anneeStart, anneeEnd);
        displayZone(apiZoneSpeciesResponse);
    }

    /**
     * Détermine la légende selon le nombre de signalements
     * @param minNbSignals
     * @param maxNbSignals
     */
    public void setLegend ( int minNbSignals, int maxNbSignals){

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

    /**
     * Détermine la Color selon le nombre de signalements
     * @param zoneSpecies
     * @param minNbSignals
     * @param maxNbSignals
     * @return Color
     */
    public Color setColor (ZoneSpecies zoneSpecies,int minNbSignals, int maxNbSignals){

        int interval = (maxNbSignals - minNbSignals) / 8;

        int nbSignals = zoneSpecies.getNbSignals();

        if (nbSignals <= minNbSignals + interval && nbSignals >= minNbSignals) {
            return (Color) color1.getFill();
        } else if (nbSignals <= minNbSignals + 2 * interval && nbSignals > nbSignals + interval) {
            return (Color) color2.getFill();
        } else if (nbSignals <= minNbSignals + 3 * interval && nbSignals > minNbSignals + 2 * interval) {
            return (Color) color3.getFill();
        } else if (nbSignals <= minNbSignals + 4 * interval && nbSignals > minNbSignals + 3 * interval) {
            return (Color) color4.getFill();
        } else if (nbSignals <= minNbSignals + 5 * interval && nbSignals > minNbSignals + 4 * interval) {
            return (Color) color5.getFill();
        } else if (nbSignals <= minNbSignals + 6 * interval && nbSignals > minNbSignals + 5 * interval) {
            return (Color) color6.getFill();
        } else if (nbSignals <= minNbSignals + 7 * interval && nbSignals > minNbSignals + 6 * interval) {
            return (Color) color7.getFill();
        } else if (nbSignals <= maxNbSignals && nbSignals > minNbSignals + 7 * interval) {
            return (Color) color8.getFill();
        } else {
            return null;
        }

    }

    /**
     * Animation qui tourne parent selon l'axe y
     * @param parent
     * @param rotationSpeed
     * @return AnimationTimer
     */
    public static AnimationTimer animationTimerRotate(Group parent, double rotationSpeed){

        final long startNanoTime = System.nanoTime();
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;
                parent.setRotationAxis(new Point3D(0,1,0));
                parent.setRotate(rotationSpeed * t);
            }
        };

        return animationTimer;
    }

    /**
     * Crée l'objet Earth
     * @see com.geosis.app.Earth
     * @see CameraManager
     * @param root3D
     */
    public void createEarth (Group root3D){
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

        SubScene subScene = new SubScene(root3D, 500, 500, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        //subScene.setFill(Color.GRAY);
        subScene.translateXProperty().setValue(25);
        subScene.translateYProperty().setValue(25);
        anchorPane.getChildren().addAll(subScene);
    }
}