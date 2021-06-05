package com.geosis.api.loader;

import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiObservationResponse;

public abstract class LoaderSpecies {

    public static LoaderSpecies createLoaderSpecies(){
        return new HttpLoaderSpecies();
    }

    public abstract ApiNameResponse getNames(String name);

    public abstract ApiObservationResponse getObservations(String geoHash,String name);

}
