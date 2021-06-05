package com.geosis.api.loader;

import com.geosis.api.Request;
import com.geosis.api.object.Zone;
import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpLoaderZoneSpecies extends LoaderZoneSpecies{

    private final String url = "https://api.obis.org/v3/";

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByName(String name) {

        ApiZoneSpeciesResponse response = new ApiZoneSpeciesResponse();
        JSONObject result = new JSONObject(Request.readJsonFromUrl(url +
                "occurrence/grid/3?scientificname=" + name,response));

        createZoneSpeciesResponse(result,response,name);

        return response;
    }

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByTime(String name, Date dateStart, Date dateEnd) {
        ApiZoneSpeciesResponse response = new ApiZoneSpeciesResponse();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String param = String.format("occurrence/grid/3?scientificname=%s&" +
                "startdate=%s&enddate=%s",name,formatter.format(dateStart),formatter.format(dateEnd));

        JSONObject result = new JSONObject(Request.readJsonFromUrl(url + param,response));

        createZoneSpeciesResponse(result,response,name);

        return response;
    }

    private void createZoneSpeciesResponse(JSONObject result,ApiZoneSpeciesResponse response,String name){
        JSONArray array = result.getJSONArray("features");

        for (int i = 0; i < array.length(); i++) {
            ZoneSpecies zoneSpecies = new ZoneSpecies();
            JSONObject current = array.getJSONObject(i);
            JSONArray coordinates = current.getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);

            Zone zone = new Zone();
            for (int j = 0; j < coordinates.length() ; j++) {
                zone.addCoords(coordinates.getJSONArray(j).getDouble(0),coordinates.getJSONArray(j).getDouble(1));
            }

            zoneSpecies.setName(name);
            zoneSpecies.setNbSignals(current.getJSONObject("properties").getInt("n"));

            response.addZoneSpecies(zoneSpecies);
        }
    }

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByInterval(String name, Date dateStart, Double interval, int nbIntervals) {
        return null;
    }
}
