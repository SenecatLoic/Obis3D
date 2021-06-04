package com.geosis.api.loader;

import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiObservationResponse;

import java.util.List;

public abstract class LoaderSpecies {

    public static LoaderSpecies createLoaderSpecies(){
        return new LoaderSpeciesFile();
    }

    public abstract ApiNameResponse getNames(String name);

    public abstract ApiObservationResponse getObservations(String geoHash,String name);

}
