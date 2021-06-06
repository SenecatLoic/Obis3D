package obis3D;

public class APIReponse {

    private int code;
    private String message;

    public APIReponse(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
