package com.geosis.api.loader;

import com.geosis.api.response.ApiNameResponse;
import static org.junit.Assert.*;

import com.geosis.api.response.ApiObservationResponse;
import org.junit.Test;

public class TestHttpLoaderSpecies {

    @Test
    public void testGetNames(){
        LoaderSpecies loader = LoaderSpecies.createLoaderSpecies();

        ApiNameResponse names = loader.getNames("Delp");

        assertEquals(names.getCode(),200);
        assertEquals(19,names.getData().size());

        assertEquals("Delphacidae",names.getData().get(0));
    }

    @Test
    public void testGetObservations(){
        LoaderSpecies loader = LoaderSpecies.createLoaderSpecies();

        ApiObservationResponse observations = loader.getObservations("spd","Manta birostris");

        assertEquals(observations.getCode(),200);
        assertEquals(3,observations.getData().size());

        assertEquals("Myliobatiformes",observations.getData().get(0).getOrder());
    }
}
