package com.geosis.app.exception;

import javafx.scene.control.Alert;

/**
 * Exception sur l'entrée de l'utilisateur (format incorrect ou nom scientifique inexistant par exemple)
 */

public class InputException extends Exception {

    private String messageErreur;

    public InputException(String messageErreur){

        this.messageErreur = messageErreur;

    }

    public void sendAlert(){

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(messageErreur);
        alert.show();

    }

}
