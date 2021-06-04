package obis3D;

public class ZoneSpecies {

    private String name;
    private int nbSignals;
    private Zone zone;

    public ZoneSpecies(String name, int nbSignals, Zone zone){
        this.name = name;
        this.nbSignals = nbSignals;
        this.zone = zone;
    }

    public String getName(){
        return this.name;
    }

    public int getNbSignals(){
        return this.nbSignals;
    }

    public Zone getZone(){
        return this.zone;
    }


}
