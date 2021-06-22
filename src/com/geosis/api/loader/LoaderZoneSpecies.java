package com.geosis.api.loader;

import com.geosis.api.object.Zone;
import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class LoaderZoneSpecies {

    public static LoaderZoneSpecies createLoaderSpecies(){
        return new HttpLoaderZoneSpecies();
    }

    public abstract ApiZoneSpeciesResponse getZoneSpeciesByName(String name);

    public abstract ApiZoneSpeciesResponse getZoneSpeciesByTime(String name, int anneeStart, int anneeEnd);
    public abstract ArrayList<CompletableFuture<Object>> getZoneSpeciesByInterval(String name, int precision, int yearStart, int interval, int nbIntervals);

    protected void createZoneSpeciesResponse(JSONObject result, ApiZoneSpeciesResponse response, String name){
        JSONArray array = result.getJSONArray("features");

        for (int i = 0; i < array.length(); i++) {
            ZoneSpecies zoneSpecies = new ZoneSpecies();
            JSONObject current = array.getJSONObject(i);
            JSONArray coordinates = current.getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);

            Zone zone = new Zone();
            for (int j = 0; j < coordinates.length() ; j++) {
                zone.addCoords(coordinates.getJSONArray(j).getDouble(0),coordinates.getJSONArray(j).getDouble(1));
            }

            zoneSpecies.setZone(zone);
            zoneSpecies.setName(name);
            zoneSpecies.setNbSignals(current.getJSONObject("properties").getInt("n"));

            response.addZoneSpecies(zoneSpecies);
        }
    }
}
