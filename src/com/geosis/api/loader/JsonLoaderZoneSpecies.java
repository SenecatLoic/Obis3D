package com.geosis.api.loader;

import com.geosis.api.JsonTools;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import org.json.JSONObject;

import java.io.*;

public class JsonLoaderZoneSpecies {

    private String fileName;

    public JsonLoaderZoneSpecies(String fileName){
        this.fileName = fileName;
    }

    public ApiZoneSpeciesResponse getZoneSpecies(String name) {
        ApiZoneSpeciesResponse apiZoneSpeciesResponse = new ApiZoneSpeciesResponse();
        try(Reader reader = new FileReader("Selachii.json")){
            BufferedReader br = new BufferedReader(reader);
            String jsonText = JsonTools.readAll(br);
            JSONObject jsonRoot = new JSONObject(jsonText);


        }catch (IOException e){

        }

        return apiZoneSpeciesResponse;
    }

}
