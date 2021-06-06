package obis3D;

import java.util.ArrayList;
import java.util.List;

public class APIZoneSpeciesReponse extends APIReponse{

    private List<ZoneSpecies> zoneSpeciesData = new ArrayList<>();

    public APIZoneSpeciesReponse(int code, String message){
        super(code, message);
    }

    public void addZoneSpeciesData(ZoneSpecies zoneSpecies){
        this.zoneSpeciesData.add(zoneSpecies);
    }

}
