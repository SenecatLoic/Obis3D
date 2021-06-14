package com.geosis.app;

import com.geosis.api.object.ZoneSpecies;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;


import javafx.scene.shape.Rectangle;
import java.util.List;

public class Legend {

    /**
     * Rend la légende invisible
     * @param colors
     * @param labels
     */
    public static void setInvisible(List<Rectangle> colors, List<Label> labels){

        for (Rectangle colorPane : colors) {
            colorPane.setVisible(false);
        }

        for (Label label : labels) {
            label.setText("");
        }

    }

    /**
     * Détermine la légende selon le nombre de signalements
     * @param minNbSignals
     * @param maxNbSignals
     * @param colors
     * @param labels
     */
    public static void setLegend (int minNbSignals, int maxNbSignals, List<Rectangle> colors, List<Label> labels){

        for (Rectangle paneColor : colors) {
            paneColor.setVisible(true);
        }

        int interval = (maxNbSignals - minNbSignals) / 8;


        labels.get(0).setText("De " + minNbSignals + " à " + (minNbSignals + interval) + " signalements");

        for(int i = 1; i < labels.size(); i++){
            labels.get(i).setText("De " + (minNbSignals + i * interval + 1) + " à " + ((minNbSignals + (i + 1) *  interval)) + " signalements");
        }
    }

    /**
     * Détermine la Color selon le nombre de signalements
     * @param zoneSpecies
     * @param minNbSignals
     * @param maxNbSignals
     * @return Color
     */
    public static Color setColor (ZoneSpecies zoneSpecies, int minNbSignals, int maxNbSignals, List<Rectangle> colorsPane){

        int interval = (maxNbSignals - minNbSignals) / 8;

        int nbSignals = zoneSpecies.getNbSignals();

        if (nbSignals <= minNbSignals + interval && nbSignals >= minNbSignals) {
            return (Color) colorsPane.get(0).getFill();
        } else if (nbSignals <= minNbSignals + 2 * interval && nbSignals > nbSignals + interval) {
            return (Color) colorsPane.get(1).getFill();
        } else if (nbSignals <= minNbSignals + 3 * interval && nbSignals > minNbSignals + 2 * interval) {
            return (Color) colorsPane.get(2).getFill();
        } else if (nbSignals <= minNbSignals + 4 * interval && nbSignals > minNbSignals + 3 * interval) {
            return (Color) colorsPane.get(3).getFill();
        } else if (nbSignals <= minNbSignals + 5 * interval && nbSignals > minNbSignals + 4 * interval) {
            return (Color) colorsPane.get(4).getFill();
        } else if (nbSignals <= minNbSignals + 6 * interval && nbSignals > minNbSignals + 5 * interval) {
            return (Color) colorsPane.get(5).getFill();
        } else if (nbSignals <= minNbSignals + 7 * interval && nbSignals > minNbSignals + 6 * interval) {
            return (Color) colorsPane.get(6).getFill();
        } else if (nbSignals <= maxNbSignals && nbSignals > minNbSignals + 7 * interval) {
            return (Color) colorsPane.get(7).getFill();
        } else {
            return null;
        }

    }


}
