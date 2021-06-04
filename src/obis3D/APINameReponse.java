package obis3D;

import java.util.ArrayList;
import java.util.List;

public class APINameReponse extends APIReponse{

    private List<String> nameData = new ArrayList<>();

    public APINameReponse(int code, String message){
        super(code, message);
    }

    public void addNameData(String name){
        this.nameData.add(name);
    }



}
