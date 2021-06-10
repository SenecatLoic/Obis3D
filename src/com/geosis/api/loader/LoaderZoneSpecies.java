package com.geosis.api.loader;

import com.geosis.api.response.ApiZoneSpeciesResponse;

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
}
