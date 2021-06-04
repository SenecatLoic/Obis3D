package obis3D;

public class Observation {

    private String scientificName;
    private String order;
    private String superClass;
    private String recordedBy;
    private String species;

    public Observation(String scientificName, String order, String superClass, String recordedBy, String species){
        this.scientificName = scientificName;
        this.order = order;
        this.superClass = superClass;
        this.recordedBy = recordedBy;
        this.species = species;
    }

    public String getScientificName(){
        return this.scientificName;
    }

    public String getOrder(){
        return this.order;
    }

    public String getSuperClass(){
        return this.superClass;
    }

    public String getRecordedBy(){
        return this.recordedBy;
    }

    public String getSpecies(){
        return this.species;
    }

}
