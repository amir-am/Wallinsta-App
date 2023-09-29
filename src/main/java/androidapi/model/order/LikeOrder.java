package androidapi.model.order;

import androidapi.model.instagramapi.InstagramMedia;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Calendar;

/**
 * Created by Amir Hossein on 8/3/2017.
 */
@Entity
@Table(name = "like_order", indexes = {@Index(name = "like_index", columnList = "status")})
public class LikeOrder {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @OneToOne
    @JoinColumn(name = "media")
    private InstagramMedia media;
    @Column(name = "count")
    private int count;
    @Column(name = "done")
    private int done;
    @Column(name = "failed")
    private int failed=0;
    @Column(name = "status")
    private OrderType status = OrderType.RUNNING;
    @Column(name = "registration_date")
    private Calendar registrationDate;
    @Column(name = "execution_date")
    private Calendar executionDate;
    @Column(name = "user_id")
    private int userId;

    public LikeOrder() {
        registrationDate = Calendar.getInstance();
    }

    public LikeOrder(InstagramMedia media, int count, int userId) {
        this.media = media;
        this.count = count;
        this.userId = userId;
        registrationDate = Calendar.getInstance();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InstagramMedia getMedia() {
        return media;
    }

    public void setMedia(InstagramMedia media) {
        this.media = media;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public OrderType getStatus() {
        return status;
    }

    public void setStatus(OrderType status) {
        this.status = status;
    }

    public Calendar getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Calendar registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Calendar getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Calendar executionDate) {
        this.executionDate = executionDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
