package androidapi.model.userdid;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Amir Hossein on 8/14/2017.
 */
@Entity
@Table(name = "user_liked")
public class UserLiked {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "media_pk")
    private long mediaPk;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "liked")
    private boolean liked;
    @Column(name = "like_date")
    private Date likeDate;
    @Column(name = "unlike_date")
    private Date unlikeDate;
    @Column(name = "order_id")
    private int orderId;

    public UserLiked() {
    }

    public UserLiked(long mediaPk, int userId, int orderId) {
        this.mediaPk = mediaPk;
        this.userId = userId;
        this.orderId = orderId;
        liked = true;
        likeDate = Calendar.getInstance().getTime();
    }

    public long getMediaPk() {
        return mediaPk;
    }

    public void setMediaPk(long mediaPk) {
        this.mediaPk = mediaPk;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Date getLikeDate() {
        return likeDate;
    }

    public void setLikeDate(Date likeDate) {
        this.likeDate = likeDate;
    }

    public Date getUnlikeDate() {
        return unlikeDate;
    }

    public void setUnlikeDate(Date unlikeDate) {
        this.unlikeDate = unlikeDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
