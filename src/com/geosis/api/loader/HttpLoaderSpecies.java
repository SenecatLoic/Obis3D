package com.geosis.api.loader;

import com.geosis.api.Request;
import com.geosis.api.object.Observation;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiObservationResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class HttpLoaderSpecies extends LoaderSpecies{

    private final String url = "https://api.obis.org/v3/";

    @Override
    public ApiNameResponse getNames(String name) {
        ApiNameResponse response = new ApiNameResponse();

        try{
            String json = Request.readJsonFromUrl(url + "taxon/complete/verbose/" +
                    URLEncoder.encode(name, StandardCharsets.UTF_8),response).get(10, TimeUnit.SECONDS);

            if(response.getCode() == 404){
                return response;
            }

            JSONArray result = new JSONArray(json);

            for (int i = 0; i < result.length(); i++) {

                response.addName(result.getJSONObject(i).getString("scientificName"));
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public ApiObservationResponse getObservations(String geoHash, String name) {
        ApiObservationResponse response = new ApiObservationResponse();
        String param = "occurrence?geometry=" + geoHash;


        if(name != null){
            param += "&scientificname=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
        }
        try {
            String json = Request.readJsonFromUrl(url + param,response).get(10, TimeUnit.SECONDS);

            if(response.getCode() == 404){
                return response;
            }
            JSONObject result = new JSONObject(json);

            JSONArray array = result.getJSONArray("results");

            for (int i = 0; i < array.length(); i++){
                Observation observation = new Observation();

                observation.setScientificName(array.getJSONObject(i).getString("scientificName"));
                observation.setOrder(array.getJSONObject(i).getString("order"));
                observation.setSpecies(array.getJSONObject(i).getString("species"));
                observation.setRecordedBy(array.getJSONObject(i).getString("recordedBy"));
                observation.setSuperClass(array.getJSONObject(i).getString("superclass"));

                response.addObservation(observation);
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }

        return response;
    }
}
