package androidapi.model.userdid;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Amir Hossein on 8/14/2017.
 */
@Entity
@Table(name = "user_commented")
public class UserCommented {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "media_pk")
    private long mediaPk;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "commented")
    private boolean commented;
    @Column(name = "comment_date")
    private Date commentDate;
    @Column(name = "uncomment_date")
    private Date uncommentDate;
    @Column(name = "order_id")
    private int orderId;

    public UserCommented() {
    }

    public UserCommented(long mediaPk, int userId, int orderId) {
        this.mediaPk = mediaPk;
        this.userId = userId;
        this.orderId = orderId;
        commented = true;
        commentDate = Calendar.getInstance().getTime();
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

    public boolean isCommented() {
        return commented;
    }

    public void setCommented(boolean commented) {
        this.commented = commented;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public Date getUncommentDate() {
        return uncommentDate;
    }

    public void setUncommentDate(Date uncommentDate) {
        this.uncommentDate = uncommentDate;
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
