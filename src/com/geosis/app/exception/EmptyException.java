package com.geosis.app.exception;

import javafx.scene.control.Alert;

/**
 * Exeption si la recherche est valide mais qu'il n'y a aucune donnée disponible
 */

public class EmptyException extends Exception{

    public EmptyException(){

    }

    public void sendAlert(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Nous n'avons pas de données disponibles pour votre recherche");
        alert.show();

    }

}
