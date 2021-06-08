package com.geosis.api.response;

import com.geosis.api.object.ZoneSpecies;

import java.util.ArrayList;

public class ApiZoneSpeciesResponse extends ApiResponse{

    private ArrayList<ZoneSpecies> data;

    public ApiZoneSpeciesResponse(){
        data = new ArrayList<>();
    }

    public void addZoneSpecies(ZoneSpecies zoneSpecies){
        data.add(zoneSpecies);
    }

    public ArrayList<ZoneSpecies> getData() {
        return data;
    }

    public int getNbSignalsMax(){
        int max = Integer.MIN_VALUE;
        for (ZoneSpecies zoneSpecies : data) {
            if(zoneSpecies.getNbSignals() > max){
                max = zoneSpecies.getNbSignals();
            }
        }
        return max;
    }

    public int getNbSignalsMin(){
        int min = Integer.MAX_VALUE;
        for (ZoneSpecies zoneSpecies : data) {
            if(zoneSpecies.getNbSignals() < min){
                min = zoneSpecies.getNbSignals();
            }
        }
        return min;
    }

}
