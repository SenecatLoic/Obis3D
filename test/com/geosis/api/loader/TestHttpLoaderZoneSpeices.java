package com.geosis.api.loader;

import com.geosis.api.object.ZoneSpecies;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

        ApiZoneSpeciesResponse zoneSpecies = loader.getZoneSpeciesByTime("Delphinidae",2015,2021);

        assertEquals(zoneSpecies.getCode(),200);
        assertEquals(967,zoneSpecies.getData().size());

    }

    @Test
    public void testGetZoneSpeciesByInterval(){
        LoaderZoneSpecies loader = LoaderZoneSpecies.createLoaderSpecies();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();

        ArrayList<CompletableFuture<Object>> zoneSpecies = loader.getZoneSpeciesByInterval("Delphinidae",3,2015 ,5,3);

        assertEquals(3,zoneSpecies.size());
        System.out.println("ezafaz");
        for (CompletableFuture<Object> zone: zoneSpecies) {
            try {
                ApiZoneSpeciesResponse zoneSpeciesResponse = (ApiZoneSpeciesResponse) zone.get(10, TimeUnit.SECONDS);
                System.out.println(zoneSpeciesResponse.getData().size());
            }catch (Exception e){

            }
        }

        //assertEquals(zoneSpecies.getCode(),200);
        //assertEquals(5454,zoneSpecies.getData().get(0).getNbSignals());
    }
}
