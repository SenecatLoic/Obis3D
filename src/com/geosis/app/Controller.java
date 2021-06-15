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
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.chart.*;
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

import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.geosis.app.GeometryTools.*;
import com.geosis.api.loader.*;
import javafx.stage.Stage;

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
    private Label labelName1;

    @FXML
    private Label labelColor1, labelColor2, labelColor3, labelColor4, labelColor5, labelColor6, labelColor7, labelColor8;

    @FXML
    private ListView<String> listView;

    private List<Rectangle> colorsPane= new ArrayList<>();
    private List<Label> labels = new ArrayList<>();

    private Group earth;
    private int yearStartInt = Integer.MIN_VALUE;
    private int yearEndInt = Integer.MAX_VALUE;
    private ArrayList<String> nameSearch = new ArrayList<>();
    private boolean nameExist = false;

    private int finalCurrentYear;
    private float finalProgression;


    public Controller() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {

        listView.setVisible(false);

        //Create lists for the legend
        colorsPane = Arrays.asList(color1, color2, color3, color4, color5, color6, color7, color8);
        labels = Arrays.asList(labelColor1, labelColor2, labelColor3, labelColor4, labelColor5, labelColor6, labelColor7, labelColor8);

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

                labelName1.setText("Scientific names");

                if(scientificName.getText().isEmpty()){
                    listView.setVisible(false);
                    labelName1.setText("Results");
                }

                //System.out.println(apiNameResponse.getData());
            }
        });

        scientificName.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                String s = scientificName.getText();

                try {
                    actionBtnSearch(s);
                } catch (InputException e) {
                    e.sendAlert();
                } catch (EmptyException e) {
                    e.sendAlert();
                }
            }
        });



        listView.setOnMouseClicked(event -> {
            // mettre à jour le Textfield scientificName en sélectionnant l'espèce + appui sur ENTREE
            listView.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                if(keyEvent.getCode() == KeyCode.ENTER){
                    String nameClicked = listView.getSelectionModel().getSelectedItem();
                    scientificName.setText(nameClicked);
                    listView.setVisible(false);
                    labelName1.setText("Results");
                }
            });
            // mettre à jour le Textfield scientificName en double cliquant
            if(event.getClickCount() == 2){
                String nameClicked = listView.getSelectionModel().getSelectedItem();
                scientificName.setText(nameClicked);
                listView.setVisible(false);
                labelName1.setText("Results");
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
            } catch (InputException e) {
                e.sendAlert();
            } catch (EmptyException e){
                e.sendAlert();
            }

        });

        btnStop.setOnAction(actionEvent -> {



        });

        btnStart.setOnAction(actionEvent -> {

            String s = scientificName.getText();

            try {
                actionBtnStart(loaderZoneSpecies, s);
            } catch (InputException e) {
                e.sendAlert();
            } catch (EmptyException e){
                e.sendAlert();
            }

        });

    }

    /**
     * Début de l'affiche d'une évolution
     * @see Graph#initGraph()
     * @see Graph#createPoint(Pane, LoaderZoneSpecies, String, int, int, int)
     * @see #displayZone(ApiZoneSpeciesResponse)
     * @see com.geosis.api.loader.LoaderZoneSpecies#getZoneSpeciesByInterval(String, int, int, int, int)
     * @param loaderZoneSpecies
     * @param name
     * @throws InputException
     */
    public void actionBtnStart(LoaderZoneSpecies loaderZoneSpecies, String name) throws InputException, EmptyException{

        nameExist = false;

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

                finalCurrentYear = 0;
                Graph.initGraph();

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
                                        // on ne veut pas créer d'alerte
                                    }
                                    Graph.createPoint(vbox, loaderZoneSpecies, name, currentYear, yearStartInt, yearEndInt);
                                    finalCurrentYear +=1;
                                });

                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                            return zoneSpeciesResponse;
                        }
                    };
                    Graph.createPoint(vbox, loaderZoneSpecies, name, yearEndInt, yearStartInt, yearEndInt);

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

        Legend.setInvisible(colorsPane, labels);

        // Todo suppr les résultats

    }

    /**
     * Action du bouton Search
     * @throws InputException
     * @throws EmptyException
     * @param name
     * @see #afficheZoneByName(String)
     * @see #afficheZoneByTime(String, int, int)
     */
    public void actionBtnSearch(String name) throws InputException, EmptyException {

        nameExist = false;

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
                    yearEndInt = Integer.parseInt(yearEnd.getText());

                    afficheZoneByTime(name, yearStartInt, yearEndInt);

                } catch (NumberFormatException e) {
                    throw new InputException("L'année n'est pas valide");

                }
            }
        }
    }

    /**
     * Affiche zone
     * @throws EmptyException
     * @see ProgressBarWindow#createProgressBarWindow(Task)
     * @see #afficheZoneByName(String)
     * @see #afficheZoneByTime(String, int, int) 
     * @param apiZoneSpeciesResponse
     */
    public void displayZone(ApiZoneSpeciesResponse apiZoneSpeciesResponse) throws EmptyException{

        int minNbSignals = apiZoneSpeciesResponse.getNbSignalsMin();
        int maxNbSignals = apiZoneSpeciesResponse.getNbSignalsMax();

        // défini les paramètres pour la progressBar
        float pas = (float) apiZoneSpeciesResponse.getData().size() / 100;
        finalProgression = 1;

        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if(minNbSignals != Integer.MAX_VALUE && maxNbSignals != Integer.MIN_VALUE){

                    Platform.runLater(() -> {
                        Legend.setLegend(minNbSignals, maxNbSignals, colorsPane, labels);
                    });

                    for (ZoneSpecies zoneSpecies : apiZoneSpeciesResponse.getData()) {

                        Platform.runLater(() -> {
                            GeometryTools.addPolygon(earth, zoneSpecies.getZone().getCoords(), Legend.setColor(zoneSpecies, minNbSignals, maxNbSignals, colorsPane));
                            updateProgress(finalProgression, apiZoneSpeciesResponse.getData().size());
                            finalProgression += 1;
                        });
                        Thread.sleep((long)0.05);
                    }

                } else {
                    throw new EmptyException();
                }
                return null;
            }
        };

        ProgressBarWindow.createProgressBarWindow(task);

        final Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * Affiche les zones d'une espèce
     * @throws EmptyException
     * @see #displayZone(ApiZoneSpeciesResponse) 
     * @see com.geosis.api.loader.LoaderZoneSpecies#getZoneSpeciesByName(String)
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
     * @see com.geosis.api.loader.LoaderZoneSpecies#getZoneSpeciesByTime(String, int, int)
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