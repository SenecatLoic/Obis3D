package com.geosis.api.response;

import java.util.ArrayList;

public class ApiNameResponse extends ApiResponse{
    private ArrayList<String> data;

    public ApiNameResponse(){
        data = new ArrayList<>();
    }

    public void addName(String name){
        data.add(name);
    }

    public ArrayList<String> getData() {
        return data;
    }
}
