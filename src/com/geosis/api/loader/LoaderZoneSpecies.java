package com.geosis.api.loader;

import com.geosis.api.response.ApiZoneSpeciesResponse;

import java.util.Date;

public abstract class LoaderZoneSpecies {

    public static LoaderZoneSpecies createLoaderSpecies(){
        return new HttpLoaderZoneSpecies();
    }

    public abstract ApiZoneSpeciesResponse getZoneSpeciesByName(String name);

    public abstract ApiZoneSpeciesResponse getZoneSpeciesByTime(String name, Date dateStart, Date dateEnd);
    public abstract ApiZoneSpeciesResponse getZoneSpeciesByInterval(String name, Date dateStart, Double interval,int nbIntervals);
}
