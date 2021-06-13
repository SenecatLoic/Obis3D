package com.geosis.app;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * @see <a href="https://gist.github.com/TheItachiUchiha/12e40a6f3af6e1eb6f75">Code initial</a>
 */

public class ToggleSwitchRotation extends HBox {

    private final Label label = new Label();
    private final Button button = new Button();

    AnimationTimer animationTimer;

    private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty switchOnProperty() { return switchedOn; }

    private void init(Group parent, double rotationSpeed) {

        label.setText("OFF");
        label.setFont(new Font("Consolas", 11));
        label.setStyle("-fx-font-weight: bold;");
        button.setStyle("-fx-background-radius: 30;");
        button.setFocusTraversable(false);

        getChildren().addAll(label, button);
        button.setOnAction((e) -> {
            switchedOn.set(!switchedOn.get());
        });
        label.setOnMouseClicked((e) -> {
            switchedOn.set(!switchedOn.get());
        });
        setStyle();
        bindProperties();

        label.toFront();
    }

    private void setStyle() {
        //Default Width
        setWidth(50);
        label.setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #C0392B; -fx-text-fill:black; -fx-background-radius: 30;");
        setAlignment(Pos.CENTER_LEFT);
    }

    private void bindProperties() {
        label.prefWidthProperty().bind(widthProperty().divide(2));
        label.prefHeightProperty().bind(heightProperty());
        button.prefWidthProperty().bind(widthProperty().divide(2));
        button.prefHeightProperty().bind(heightProperty());
    }

    public ToggleSwitchRotation(Group parent, double rotationSpeed) {
        init(parent, rotationSpeed);
        switchedOn.addListener((a,b,valueChange) -> {
            if (valueChange) {
                label.setText("ON");
                setStyle("-fx-background-color: #27AE60; -fx-background-radius: 30;");
                button.toFront();

                animationTimer = Controller.animationTimerRotate(parent, rotationSpeed);
                animationTimer.start();
            }
            else {
                label.setText("OFF");
                setStyle("-fx-background-color: #C0392B; -fx-background-radius: 30;");
                label.toFront();

                animationTimer.stop();
            }
        });
    }



}