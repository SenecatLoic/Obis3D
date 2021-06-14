package com.geosis.app;

import com.geosis.api.response.ApiZoneSpeciesResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ProgressBarWindow {

    private static ProgressBar progressBar = new ProgressBar();
    private static Label progressLabel = new Label();
    private static final double EPSILON = 0.005;
    private static Stage newWindow = new Stage();

    /**
     * Création de la fenêtre progressBar
     * @see com.geosis.app.Controller#displayZone(ApiZoneSpeciesResponse)
     * @param task
     */
    public static void createProgressBarWindow(Task task){

        progressBar.progressProperty().bind(task.progressProperty());
        progressLabel.textProperty().bind(task.progressProperty().asString());

        progressBar.progressProperty().addListener(observable -> {
            progressBar.setStyle("-fx-accent: #59BAFF;");
            // change la couleur quand la progressBar est complétée puis la fermer automatiquement
            if (progressBar.getProgress() >= 1 - EPSILON) {
                progressLabel.textProperty().unbind();
                progressLabel.setText("DONE");
                progressBar.setStyle("-fx-accent: forestgreen;");
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    newWindow.close();
                });
            }
        });

        // créer la nouvelle fenêtre
        StackPane loadingPane = new StackPane();

        progressBar.setPrefSize(150, 30);

        loadingPane.getChildren().addAll(progressBar, progressLabel);

        Scene secondScene = new Scene(loadingPane, 270, 80);

        newWindow.setTitle("Loading");
        newWindow.setScene(secondScene);

        newWindow.show();

    }

}
