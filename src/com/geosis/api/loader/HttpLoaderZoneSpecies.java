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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HttpLoaderZoneSpecies extends LoaderZoneSpecies{

    private final String url = "https://api.obis.org/v3/";

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByName(String name) {

        ApiZoneSpeciesResponse response = new ApiZoneSpeciesResponse();

        try{
            String json = Request.readJsonFromUrl(url + "occurrence/grid/3?scientificname="
                    + URLEncoder.encode(name, StandardCharsets.UTF_8),response).get(10, TimeUnit.SECONDS);

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
    public ApiZoneSpeciesResponse getZoneSpeciesByTime(String name, Date dateStart, Date dateEnd) {
        ApiZoneSpeciesResponse response = new ApiZoneSpeciesResponse();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try{
            String param = String.format("occurrence/grid/3?scientificname=%s&" +
                    "startdate=%s&enddate=%s",URLEncoder.encode(name, StandardCharsets.UTF_8),formatter.format(dateStart),formatter.format(dateEnd));
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

            zoneSpecies.setName(name);
            zoneSpecies.setNbSignals(current.getJSONObject("properties").getInt("n"));

            response.addZoneSpecies(zoneSpecies);
        }
    }

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByInterval(String name,int precision, Date dateStart, int interval, int nbIntervals) {
        ApiZoneSpeciesResponse response = new ApiZoneSpeciesResponse();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Date startTmp = dateStart;

        try {

            ArrayList<CompletableFuture<String>> listRequest = new ArrayList<>();
            //on fait toute les requêtes
            for (int i = 1; i <= nbIntervals; i++) {
                Calendar c = Calendar.getInstance();
                c.setTime(startTmp);
                c.add(Calendar.YEAR, interval);
                Date tmp = c.getTime();

                String param = String.format("occurrence/grid/%d?scientificname=%s&" +
                                "startdate=%s&enddate=%s",precision,URLEncoder.encode(name, StandardCharsets.UTF_8),
                        formatter.format(startTmp),formatter.format(tmp));

                startTmp = tmp;
                listRequest.add(Request.readJsonFromUrl(url + param,response));
            }

            for (CompletableFuture<String> request : listRequest) {
                JSONObject result = new JSONObject(request.get(10, TimeUnit.SECONDS));
                createZoneSpeciesResponse(result,response,name);
            }


        }catch (Exception e){
            System.out.println(e.getMessage());
            response.setMessage(e.getMessage());
        }


        return response;
    }
}
