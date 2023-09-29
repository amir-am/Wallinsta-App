package androidapi.model.main;

public class VersionDetail {
    private boolean isOK;
    private boolean force;
    private String message;

    public VersionDetail(boolean isOK, boolean force, String message) {
        this.isOK = isOK;
        this.force = force;
        this.message = message;
    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
