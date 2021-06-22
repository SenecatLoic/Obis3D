package com.geosis.api.loader;

import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiObservationResponse;

import java.util.concurrent.CompletableFuture;

public abstract class LoaderSpecies {

    public static LoaderSpecies createLoaderSpecies(){
        return new HttpLoaderSpecies();
    }

    public abstract CompletableFuture<Object> getNames(String name);

    public abstract ApiObservationResponse getObservations(String geoHash,String name);

}
