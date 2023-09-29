package androidapi.model.userdid;

/**
 * Created by Amir Hossein on 8/16/2017.
 */
public class DoingDetail {
    private int user_id;
    private int order_id;
    private long media_pk;
    private long page_pk;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public long getPage_pk() {
        return page_pk;
    }

    public void setPage_pk(long page_pk) {
        this.page_pk = page_pk;
    }

    public long getMedia_pk() {
        return media_pk;
    }

    public void setMedia_pk(long media_pk) {
        this.media_pk = media_pk;
    }
}
