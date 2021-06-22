package com.geosis.api;

import com.ludovic.vimont.*;

public class GPSConvert {

    public GPSConvert(){

    }

    public static String coordGPStoGeoHash(double latitude, double longitude){
        Location location = new Location("GeoHash", latitude, longitude);
        return GeoHashHelper.getGeohash(location);
    }

    public static void main(String[] args){

        System.out.println(coordGPStoGeoHash(35, 40));

    }

}
