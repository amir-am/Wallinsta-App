package androidapi.model.userdid;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Amir Hossein on 8/14/2017.
 */
@Entity
@Table(name = "user_followed")
public class UserFollowed {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "page_pk")
    private long pagePk;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "followed")
    private boolean followed;
    @Column(name = "follow_date")
    private Date followDate;
    @Column(name = "unfollow_date")
    private Date unfollowDate;
    @Column(name = "order_id")
    private int orderId;
    private String userName;

    public UserFollowed() {
    }

    public UserFollowed(long pagePk, int userId, int orderId) {
        this.pagePk = pagePk;
        this.userId = userId;
        this.orderId=orderId;
        followed = true;
        followDate = Calendar.getInstance().getTime();
    }

    public long getPagePk() {
        return pagePk;
    }

    public void setPagePk(long pagePk) {
        this.pagePk = pagePk;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public Date getFollowDate() {
        return followDate;
    }

    public void setFollowDate(Date followDate) {
        this.followDate = followDate;
    }

    public Date getUnfollowDate() {
        return unfollowDate;
    }

    public void setUnfollowDate(Date unfollowDate) {
        this.unfollowDate = unfollowDate;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
