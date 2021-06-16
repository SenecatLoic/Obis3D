package com.geosis.app.exception;

import javafx.scene.control.Alert;

public class EmptyException extends Exception{

    public EmptyException(){

    }

    public void sendAlert(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Nous n'avons pas de données disponibles pour votre recherche");
        alert.show();

    }

}
