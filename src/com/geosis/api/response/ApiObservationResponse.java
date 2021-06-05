package com.geosis.api.response;

import com.geosis.api.object.Observation;

import java.util.ArrayList;

public class ApiObservationResponse extends ApiResponse {
    private ArrayList<Observation> data;

    public ApiObservationResponse(){
        data = new ArrayList<>();
    }

    public void addObservation(Observation observation){
        data.add(observation);
    }

    public ArrayList<Observation> getData() {
        return data;
    }
}
