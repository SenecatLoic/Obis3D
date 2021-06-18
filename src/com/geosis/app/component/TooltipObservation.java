package com.geosis.app.component;

import com.geosis.api.object.Observation;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

public class TooltipObservation extends Tooltip {

    private final String[] categoriesName = {"Scientific Name","Order","Recorded By","Species","Super Class"};

    public TooltipObservation(Observation observation){
        String[] datas = {observation.getScientificName(),observation.getOrder(),observation.getRecordedBy(),
        observation.getSpecies(),observation.getSuperClass()};
        StringBuilder builder = new StringBuilder();
        String currentData;
        for (int i = 0; i < categoriesName.length; i++) {
            if(datas[i] == null){
                currentData = "Unknown";
            }else{
                currentData = datas[i];
            }
            builder.append(String.format("%s : %s \n",categoriesName[i],currentData));
        }

        setText(builder.toString());
        setPrefHeight(130);
        setStyle("-fx-font-size: 16px; ");
        setShowDelay(Duration.seconds(0));
        setHideDelay(Duration.seconds(0));

        setWrapText(true);
        setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
    }
}
