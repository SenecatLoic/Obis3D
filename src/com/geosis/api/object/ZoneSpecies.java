package com.geosis.api.object;

public class ZoneSpecies{

    private Zone zone;

    private String name;

    private int nbSignals;

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Zone getZone(){
        return this.zone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNbSignals(int nbSignals) {
        this.nbSignals = nbSignals;
    }
}
