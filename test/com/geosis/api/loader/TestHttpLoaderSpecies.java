package com.geosis.api.loader;

import com.geosis.api.response.ApiNameResponse;
import static org.junit.Assert.*;

import com.geosis.api.response.ApiObservationResponse;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TestHttpLoaderSpecies {

    @Test
    public void testGetNames() {
        LoaderSpecies loader = LoaderSpecies.createLoaderSpecies();

        CompletableFuture<Object> names = loader.getNames("Delp");

        try{
            ApiNameResponse response = (ApiNameResponse) names.get(10, TimeUnit.SECONDS);

            assertEquals(response.getCode(),200);
            assertEquals(19,response.getData().size());

            assertEquals("Delphacidae",response.getData().get(0));
        }catch (Exception e){

        }

    }

    @Test
    public void testGetObservations(){
        LoaderSpecies loader = LoaderSpecies.createLoaderSpecies();

        ApiObservationResponse observations = loader.getObservations("x1c",null);

        assertEquals(1000,observations.getData().size());

        observations = loader.getObservations("spd","Manta birostris");

        assertEquals(observations.getCode(),200);
        assertEquals(3,observations.getData().size());

        assertEquals("Myliobatiformes",observations.getData().get(0).getOrder());
    }
}
