package com.geosis.api.loader;

import com.geosis.api.response.ApiZoneSpeciesResponse;

import java.util.Date;

public abstract class LoaderZoneSpecies {

    public static LoaderZoneSpecies createLoaderSpecies(){
        return new HttpLoaderZoneSpecies();
    }

    public abstract ApiZoneSpeciesResponse getZoneSpeciesByName(String name);

    public abstract ApiZoneSpeciesResponse getZoneSpeciesByTime(String name, int anneeStart, int anneeEnd);
    public abstract ApiZoneSpeciesResponse getZoneSpeciesByInterval(String name,int precision, Date dateStart, int interval,int nbIntervals);
}
