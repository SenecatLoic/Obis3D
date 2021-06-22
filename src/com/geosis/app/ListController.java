package com.geosis.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ListController implements Initializable {

    @FXML
    ListView<String> list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list.setOnMouseClicked(mouseEvent -> {
            list.getSelectionModel().getSelectedItem();
        });
    }

    public void setNames(ArrayList<String> names){
        ObservableList<String> content = FXCollections.observableArrayList(names);
        list.setItems(content);
    }
}
