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
}
