package com.geosis.api.loader;

import com.geosis.api.JsonTools;
import com.geosis.api.object.Zone;
import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class JsonLoaderZoneSpecies extends LoaderZoneSpecies{

    private String fileName;

    public JsonLoaderZoneSpecies(String fileName){
        this.fileName = fileName;
    }

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByName(String name) {
        ApiZoneSpeciesResponse apiZoneSpeciesResponse = new ApiZoneSpeciesResponse();
        try(Reader reader = new FileReader(fileName)){
            BufferedReader br = new BufferedReader(reader);
            String jsonText = JsonTools.readAll(br);
            JSONObject jsonRoot = new JSONObject(jsonText);
            createZoneSpeciesResponse(jsonRoot,apiZoneSpeciesResponse,name);
        }catch (IOException e){

        }

        return apiZoneSpeciesResponse;
    }

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByTime(String name, int anneeStart, int anneeEnd) {
        return null;
    }

    @Override
    public ArrayList<CompletableFuture<Object>> getZoneSpeciesByInterval(String name, int precision, int yearStart, int interval, int nbIntervals) {
        return null;
    }
}
