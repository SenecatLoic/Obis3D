package com.geosis.api.object;

public class Observation {

    private String scientificName;

    private String order;

    private String superClass;

    private String recordedBy;

    private String species;


    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getOrder() {
        return order;
    }

    public String getSuperClass() {
        return superClass;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public String getSpecies() {
        return species;
    }
}
