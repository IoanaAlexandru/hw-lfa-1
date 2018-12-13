import java.io.IOException;

class ImpException extends IOException {
    private String msg;

    public ImpException(String type, int line) {
        super(type + " " + line);
        msg = type + " " + line;
    }

    public String getMsg() {
        return msg;
    }
}