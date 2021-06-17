package com.geosis.app;

import com.geosis.api.loader.LoaderSpecies;
import com.geosis.api.loader.LoaderZoneSpecies;
import com.geosis.api.object.Observation;
import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiObservationResponse;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import com.geosis.app.controlTools.Graph;
import com.geosis.app.controlTools.Legend;
import com.geosis.app.controlTools.ProgressBarWindow;
import com.geosis.app.controlTools.ToggleSwitchRotation;
import com.geosis.app.earth.Earth;
import com.geosis.app.exception.EmptyException;
import com.geosis.app.exception.InputException;
import com.geosis.app.geometryTools.GeometryTools;
import com.geosis.app.geometryTools.ZoneControls;
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
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import javafx.geometry.Point2D;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import sample.ludovic.vimont.*;

public class Controller implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private VBox vbox, vboxResults;

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

    private boolean search;

    /**
     * Permet de savoir si on est dans un état où il faut afficher les observations
     */
    private boolean searchObservation;

    private ArrayList<Observation> observations;
    private ZoneControls zoneControls;

    public Controller() {
        search = false;
        searchObservation = false;

        zoneControls = new ZoneControls();
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {

        //Create lists for the legend
        colorsPane = Arrays.asList(color1, color2, color3, color4, color5, color6, color7, color8);
        labels = Arrays.asList(labelColor1, labelColor2, labelColor3, labelColor4, labelColor5, labelColor6, labelColor7, labelColor8);

        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();
        earth = Earth.createEarth(this, root3D, anchorPane);

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

                ApiNameResponse apiNameResponse = loader.getNames(scientificName.getText());

                nameSearch = apiNameResponse.getData();

                ObservableList<String> names = FXCollections.observableArrayList(nameSearch);

                listView.setItems(names);

                labelName1.setText("Scientific names");

                if(scientificName.getText().isEmpty()){
                    labelName1.setText("Results");
                }

                //System.out.println(apiNameResponse.getData());
            }
        });


        scientificName.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {

            ApiNameResponse apiNameResponse = loader.getNames(scientificName.getText());

            nameSearch = apiNameResponse.getData();

            ObservableList<String> names = FXCollections.observableArrayList(nameSearch);

            listView.setItems(names);

            labelName1.setText("Scientific names");

            if(scientificName.getText().isEmpty()){
                labelName1.setText("Results");
            }


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

        // mettre à jour le Textfield scientificName en sélectionnant l'espèce + appui sur ENTREE
        listView.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){
                String nameClicked = listView.getSelectionModel().getSelectedItem();
                scientificName.setText(nameClicked);
                labelName1.setText("Results");
            }
        });

        listView.setOnMouseClicked(event -> {

            scientificName.setText(listView.getSelectionModel().getSelectedItem());
            // mettre à jour le Textfield scientificName en double cliquant
            if(event.getClickCount() == 2){
                String nameClicked = listView.getSelectionModel().getSelectedItem();
                scientificName.setText(nameClicked);
                labelName1.setText("Results");
                try {
                    afficheZoneByName(listView.getSelectionModel().getSelectedItem());

                    search = true;
                } catch (EmptyException e) {
                    e.printStackTrace();
                }
            }

        });

        root3D.addEventHandler(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isControlDown()) {

                // Récupérer les coordonnées à l'endroit du clic
                PickResult pickResult = event.getPickResult();
                Point3D spaceCoord = pickResult.getIntersectedPoint();

                Point2D point2D = GeometryTools.spaceCoordToGeoCoord(spaceCoord);
                Location loc = new Location("selected",point2D.getX(),point2D.getY());

                /*System.out.println(point.getX());
                System.out.println(point.getY());
                System.out.println(GeoHashHelper.getGeohash(loc));*/
                if(zoneControls.isInEarth(point2D.getX(),point2D.getY())){
                    ApiObservationResponse response = loader.getObservations(GeoHashHelper.getGeohash(loc),scientificName.getText());

                    observations = response.getData();

                    if(response.getCode() == 200){
                        ArrayList<String> names = new ArrayList<>();
                        for (Observation ob: observations) {
                            names.add(ob.getScientificName());
                            nameSearch.add(ob.getScientificName());
                        }
                        listView.setItems(FXCollections.observableArrayList(names));
                    }
                    labelName1.setText("Observations");
                    searchObservation = true;
                }else{
                    ApiObservationResponse response = loader.getObservations(GeoHashHelper.getGeohash(loc),null);

                    if(response.getCode() == 200){
                        ArrayList<String> names = new ArrayList<>();

                        for (Observation ob: response.getData()) {
                            names.add(ob.getScientificName());
                            nameSearch.add(ob.getScientificName());
                        }
                        listView.setItems(FXCollections.observableArrayList(names));
                    }
                    labelName1.setText("Scientific names");
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
                search = true;
                actionBtnSearch(s);
            } catch (InputException e) {
                e.sendAlert();
            } catch (EmptyException e){
                e.sendAlert();
            }

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

        btnBreak.setOnAction(actionEvent -> {

            //TODO actionBtnBreak()

        });

        btnStop.setOnAction(actionEvent -> {

            //TODO actionBtnStop()

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
        labelName1.setText("Results");

        // supprime tous les anciens polygones sur le globe
        while (earth.getChildren().size() > 1) {
            earth.getChildren().remove(1);
        }

        // supprimer le graphe d'avant s'il y en a un
        if (vbox.getChildren().get(vbox.getChildren().size() - 1) instanceof AreaChart) {
            vbox.getChildren().remove(vbox.getChildren().size() - 1);
        }

        Legend.setInvisible(colorsPane, labels);

        // supprimer la progressBar
        if (!vboxResults.getChildren().isEmpty()){
            vboxResults.getChildren().remove(vboxResults.getChildren().size() - 1);
        }

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

        while (earth.getChildren().size() > 1) {
            earth.getChildren().remove(1);
        }

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
     * @see ProgressBarWindow#createProgressBarWindow(Pane, Task)
     * @see #afficheZoneByName(String)
     * @see #afficheZoneByTime(String, int, int)
     * @param apiZoneSpeciesResponse
     */
    public void displayZone(ApiZoneSpeciesResponse apiZoneSpeciesResponse) throws EmptyException{
        int minNbSignals = apiZoneSpeciesResponse.getNbSignalsMin();
        int maxNbSignals = apiZoneSpeciesResponse.getNbSignalsMax();
        zoneControls.clear();
        // défini les paramètres pour la progressBar
        float pas = (float) apiZoneSpeciesResponse.getData().size() / 100;
        finalProgression = 1;

        while (earth.getChildren().size() > 1) {
            earth.getChildren().remove(1);
        }

        final Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if(minNbSignals != Integer.MAX_VALUE && maxNbSignals != Integer.MIN_VALUE){

                    Platform.runLater(() -> {
                        Legend.setLegend(minNbSignals, maxNbSignals, colorsPane, labels);
                    });

                    for (ZoneSpecies zoneSpecies : apiZoneSpeciesResponse.getData()) {
                        zoneControls.addZone(zoneSpecies.getZone());
                        Platform.runLater(() -> {
                            GeometryTools.addPolygon(earth, zoneSpecies.getZone().getCoords(), Legend.getColor(zoneSpecies, minNbSignals, maxNbSignals, colorsPane));
                            //GeometryTools.addBoxHistogramme(earth, zoneSpecies.getZone().getCoords(),GeometryTools.getHeightBox(zoneSpecies, minNbSignals, maxNbSignals, colorsPane), Legend.getColor(zoneSpecies, minNbSignals, maxNbSignals, colorsPane));
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

        ProgressBarWindow.createProgressBarWindow((Pane)vboxResults, task);

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
     * @see com.geosis.api.loader.LoaderZoneSpecies#getZoneSpeciesByTime(String, int, int)
     *
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

}