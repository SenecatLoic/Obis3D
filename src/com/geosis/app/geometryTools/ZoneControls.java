package com.geosis.app.geometryTools;

import com.geosis.api.object.Zone;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class ZoneControls {

    /**
     * GeoHash chargé
     */
    private List<Point2D[]> geoHashLoad;

    public ZoneControls(){
        geoHashLoad = new ArrayList<>();
    }

    public void addZone(Zone zone){
        geoHashLoad.add(zone.getCoords());
    }

    public void reset(){
        geoHashLoad = new ArrayList<>();
    }

    public boolean isInEarth(double lat, double lon){

        for(Point2D[] points: geoHashLoad){
            if(((points[0].getX() <= lon && points[1].getX() >= lon) ||
                    (points[0].getX() >= lon && points[1].getX() <= lon)) &&
                            ((points[0].getY() <= lat && points[2].getY() >= lat) ||
                    (points[0].getY() >= lat && points[2].getY() <= lat))){

                return true;
            }
        }

        return false;
    }

    public void clear(){
        geoHashLoad.clear();
    }
}
