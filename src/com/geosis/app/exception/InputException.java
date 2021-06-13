package com.geosis.app.exception;

import javafx.scene.control.Alert;

public class InputException extends Exception {

    private String messageErreur;

    public InputException(String messageErreur){

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(messageErreur);
        alert.show();

    }

    public String getMessageErreur(){
        return this.messageErreur;
    }

}
