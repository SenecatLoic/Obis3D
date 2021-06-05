package com.geosis.api.loader;

import com.geosis.api.Request;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiObservationResponse;
import org.json.JSONArray;

public class HttpLoaderSpecies extends LoaderSpecies{

    private final String url = "https://api.obis.org/v3/";

    @Override
    public ApiNameResponse getNames(String name) {
        ApiNameResponse response = new ApiNameResponse();

        JSONArray result = new JSONArray(Request.readJsonFromUrl(url + "taxon/complete/verbose/" + name,response));

        for (int i = 0; i < result.length(); i++) {

            response.addName(result.getJSONObject(i).getString("scientificName"));
        }

        return response;
    }

    @Override
    public ApiObservationResponse getObservations(String geoHash, String name) {
        return null;
    }
}
