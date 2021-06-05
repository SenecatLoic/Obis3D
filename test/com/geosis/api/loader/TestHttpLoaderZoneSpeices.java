package com.geosis.api.loader;

import com.geosis.api.response.ApiZoneSpeciesResponse;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Calendar;
import java.util.Date;

public class TestHttpLoaderZoneSpeices {

    @Test
    public void testGetZoneSpeciesByName(){
        LoaderZoneSpecies loader = LoaderZoneSpecies.createLoaderSpecies();

        ApiZoneSpeciesResponse zoneSpecies = loader.getZoneSpeciesByName("Delphinidae");

        assertEquals(zoneSpecies.getCode(),200);
        assertEquals(4099,zoneSpecies.getData().size());

    }

    @Test
    public void testGetZoneSpeciesByTime(){
        LoaderZoneSpecies loader = LoaderZoneSpecies.createLoaderSpecies();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();
        cal.set(Calendar.YEAR, 2021);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        ApiZoneSpeciesResponse zoneSpecies = loader.getZoneSpeciesByTime("Delphinidae",start,end);

        assertEquals(zoneSpecies.getCode(),200);
        assertEquals(967,zoneSpecies.getData().size());

    }
}
