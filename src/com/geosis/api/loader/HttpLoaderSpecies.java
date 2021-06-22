package com.geosis.api.loader;

import com.geosis.api.Request;
import com.geosis.api.object.Observation;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiObservationResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class HttpLoaderSpecies extends LoaderSpecies{

    private final String url = "https://api.obis.org/v3/";

    @Override
    public CompletableFuture<Object> getNames(String name) {

        ApiNameResponse response = new ApiNameResponse();
        CompletableFuture<Object> result = null;
        try{
            result = Request.readJsonFromUrl(url + "taxon/complete/verbose/" +
                name.replace(" ","%20"),response).thenApply(rest ->{
                    JSONArray array = new JSONArray(rest);
                    for (int i = 0; i < array.length(); i++) {

                        response.addName(array.getJSONObject(i).getString("scientificName"));
                    }
                    return response;
            });
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ApiObservationResponse getObservations(String geoHash, String name) {
        ApiObservationResponse response = new ApiObservationResponse();
        String param = "occurrence?size=1000&geometry=" + geoHash;


        if(name != null){
            param += "&scientificname=" + name.replace(" ","%20");
        }

        try {
            String json = Request.readJsonFromUrl(url + param, response).get(30, TimeUnit.SECONDS);

            if (response.getCode() == 404) {
                return response;
            }
            JSONObject result = new JSONObject(json);

            JSONArray array = result.getJSONArray("results");

            for (int i = 0; i < array.length(); i++) {
                Observation observation = new Observation();

                if (array.getJSONObject(i).has("scientificName")) {
                    observation.setScientificName(array.getJSONObject(i).getString("scientificName"));
                }

                if (array.getJSONObject(i).has("order")) {
                    observation.setOrder(array.getJSONObject(i).getString("order"));
                }

                if (array.getJSONObject(i).has("species")) {
                    observation.setSpecies(array.getJSONObject(i).getString("species"));
                }
                if (array.getJSONObject(i).has("recordedBy")) {
                    observation.setRecordedBy(array.getJSONObject(i).getString("recordedBy"));
                }

                if (array.getJSONObject(i).has("superclass")) {
                    observation.setSuperClass(array.getJSONObject(i).getString("superclass"));
                }

                response.addObservation(observation);

            }
        }catch (InterruptedException e ){
            response.setMessage("interupt " + e.getMessage());
        }catch (ExecutionException v){
            response.setMessage("excec " + v.getMessage());
        }catch(TimeoutException t){
            response.setMessage("timeout " + t.getMessage());
        }

        return response;
    }
}
