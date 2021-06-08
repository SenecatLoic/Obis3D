package com.geosis.api.object;

import javafx.geometry.Point2D;

public class Zone {
    /**
     * X Latitude, Y Longitude
     */
    private Point2D[] coords;
    private int index;

    public Zone(){
        coords = new Point2D[5];
        index = 0;
    }

    public void addCoords(double lat, double lon){
        coords[index] = new Point2D(lat,lon);
        index++;
    }

    public Point2D[] getCoords(){
        return this.coords;
    }

}
