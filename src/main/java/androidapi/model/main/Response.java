package androidapi.model.main;


public class Response<T> {
    private T data;

    private boolean error;
    private int messageType = 0;
    private String message = "";

    public Response(T data) {
        this.data = data;
        error = false;
    }

    public Response(T data, String message) {
        this.data = data;
        this.message = message;
        error = false;
    }

    public Response(String message) {
        this.message = message;
        error = false;
    }

    public T getData() {
        return data;
    }

    public boolean isError() {
        return error;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
