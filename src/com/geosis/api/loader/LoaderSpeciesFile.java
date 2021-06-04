package com.geosis.api.loader;

import com.geosis.api.response.ApiNameResponse;
import com.geosis.api.response.ApiObservationResponse;

public class LoaderSpeciesFile extends LoaderSpecies {
    @Override
    public ApiNameResponse getNames(String name) {
        return null;
    }

    @Override
    public ApiObservationResponse getObservations(String geoHash, String name) {
        return null;
    }
}
