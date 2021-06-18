package com.geosis.app.controlTools;

import com.geosis.api.response.ApiZoneSpeciesResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * ProgressBar selon l'avancement de la création des polygones/ boxHistogramme
 */

public class ProgressBarWindow {

    private static ProgressBar progressBar = new ProgressBar();
    private static Label progressLabel = new Label();
    private static StackPane loadingPane;
    private static final double EPSILON = 0.005;
    private static Pane parent;
    //private static Stage newWindow = new Stage();

    /**
     * Création de la fenêtre progressBar
     * @see com.geosis.app.Controller#displayZone(ApiZoneSpeciesResponse,boolean)
     * @param task
     */
    public static void createProgressBarWindow(Pane parent, Task task){

        progressBar.progressProperty().bind(task.progressProperty());
        progressLabel.textProperty().bind(task.progressProperty().asString());
        ProgressBarWindow.parent = parent;
        progressBar.progressProperty().addListener(observable -> {
            progressBar.setStyle("-fx-accent: #59BAFF;");
            // change la couleur quand la progressBar est complétée puis la fermer automatiquement
            if (progressBar.getProgress() >= 1 - EPSILON) {
                progressLabel.textProperty().unbind();
                progressLabel.setText("DONE");
                progressBar.setStyle("-fx-accent: forestgreen;");
            }
        });

        // créer la nouvelle fenêtre
        loadingPane = new StackPane();
        loadingPane.setPrefWidth(240);
        loadingPane.setTranslateY(-45);

        progressBar.setPrefSize(170, 30);

        loadingPane.getChildren().addAll(progressBar, progressLabel);

        parent.getChildren().add(loadingPane);

        //TODO virer newWindow si plus besoin
        /*
        Scene secondScene = new Scene(loadingPane, 270, 80);

        newWindow.setTitle("Loading");
        newWindow.setScene(secondScene);

        newWindow.show();
         */

    }

    public static void pause(){
        progressBar.setStyle("-fx-accent: orange;");
    }

    public static void delete(){
        parent.getChildren().remove(loadingPane);
    }

}
