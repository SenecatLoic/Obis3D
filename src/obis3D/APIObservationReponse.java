package obis3D;

import java.util.ArrayList;
import java.util.List;

public class APIObservationReponse extends APIReponse{

    private List<Observation> observationData = new ArrayList<>();

    public APIObservationReponse(int code, String message){
        super(code, message);
    }

    public void addObservationData(Observation observation){
        this.observationData.add(observation);
    }

}
