package com.geosis.api.loader;

import com.geosis.api.Request;
import com.geosis.api.object.Zone;
import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class HttpLoaderZoneSpecies extends LoaderZoneSpecies{

    private final String url = "https://api.obis.org/v3/";

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByName(String name) {

        ApiZoneSpeciesResponse response = new ApiZoneSpeciesResponse();

        try{
            /*System.out.println(url + "occurrence/grid/3?scientificname="
                    + name.replace(" ","%20"));*/
            String json = Request.readJsonFromUrl(url + "occurrence/grid/3?scientificname="
                    + name.replace(" ","%20"),response).get(10, TimeUnit.SECONDS);

            //System.out.println(json);

            if(response.getCode() == 404){
                return response;
            }

            JSONObject result = new JSONObject(json);

            createZoneSpeciesResponse(result,response,name);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByTime(String name, int anneeStart, int anneeEnd) {
        ApiZoneSpeciesResponse response = new ApiZoneSpeciesResponse();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, anneeStart);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date dateStart = cal.getTime();
        cal.set(Calendar.YEAR, anneeEnd);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        Date dateEnd = cal.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try{
            String param = String.format("occurrence/grid/3?scientificname=%s&" +
                    "startdate=%s&enddate=%s",name.replace(" ","%20"),formatter.format(dateStart),formatter.format(dateEnd));
            String json = Request.readJsonFromUrl(url + param,response).get(10, TimeUnit.SECONDS);

            if(response.getCode() == 404){
                return response;
            }

            JSONObject result = new JSONObject(json);

            createZoneSpeciesResponse(result,response,name);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }

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

            zoneSpecies.setZone(zone);
            zoneSpecies.setName(name);
            zoneSpecies.setNbSignals(current.getJSONObject("properties").getInt("n"));

            response.addZoneSpecies(zoneSpecies);
        }
    }

    @Override
    public ArrayList<CompletableFuture<Object>> getZoneSpeciesByInterval(String name,int precision, int yearStart, int interval, int nbIntervals) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<CompletableFuture<Object>> listRequest = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yearStart);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startTmp = cal.getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(startTmp);
        c.add(Calendar.YEAR, interval);
        Date end;

        try {
            //on fait toute les requêtes
            for (int i = 0; i <= nbIntervals; i++) {
                ApiZoneSpeciesResponse response = new ApiZoneSpeciesResponse();
                end = c.getTime();

                String param = String.format("occurrence/grid/%d?scientificname=%s&" +
                                "startdate=%s&enddate=%s",precision,name.replace(" ","%20"),
                        formatter.format(startTmp),formatter.format(end));

                c.add(Calendar.YEAR, interval);

                listRequest.add(Request.readJsonFromUrl(url + param,response).thenApply(rest -> {
                    JSONObject result = new JSONObject(rest);
                    createZoneSpeciesResponse(result,response,name);
                    return response;
                }));

            }
        }catch (Exception e){

        }

        return listRequest;
    }
}
