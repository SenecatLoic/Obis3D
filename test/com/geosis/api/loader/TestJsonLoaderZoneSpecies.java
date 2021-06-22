package com.geosis.api.loader;
import com.geosis.api.response.ApiNameResponse;
import static org.junit.Assert.*;

import com.geosis.api.response.ApiZoneSpeciesResponse;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TestJsonLoaderZoneSpecies {

    @Test
    public void testGetZoneSpeciesByName(){
        JsonLoaderZoneSpecies jsonLoaderZoneSpecies = new JsonLoaderZoneSpecies("resources/Selachii.json");

        ApiZoneSpeciesResponse response = jsonLoaderZoneSpecies.getZoneSpeciesByName("Selachii");

        assertEquals(4511,response.getData().size());
    }
}
