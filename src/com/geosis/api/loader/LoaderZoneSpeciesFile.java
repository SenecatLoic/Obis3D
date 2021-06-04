package com.geosis.api.loader;

import com.geosis.api.response.ApiZoneSpeciesResponse;

import java.util.Date;

public class LoaderZoneSpeciesFile extends LoaderZoneSpecies{
    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByName(String name) {
        return null;
    }

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByTime(String name, Date dateStart, Date dateEnd) {
        return null;
    }

    @Override
    public ApiZoneSpeciesResponse getZoneSpeciesByInterval(String name, Date dateStart, Double interval, int nbIntervals) {
        return null;
    }
}
