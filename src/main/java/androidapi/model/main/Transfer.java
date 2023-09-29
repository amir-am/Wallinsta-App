package androidapi.model.main;

public class Transfer {
    private int userId;
    private long destinationPk;
    private int count;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getDestinationPk() {
        return destinationPk;
    }

    public void setDestinationPk(long destinationPk) {
        this.destinationPk = destinationPk;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
