package androidapi.model.main;

import org.hibernate.annotations.GenericGenerator;

import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@javax.persistence.Table(name = "user")
public class User {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "diamond")
    private int diamond = 15;
    @Column(name = "create_date")
    private Calendar createDate;
    @Column(name = "last_view_date")
    private Calendar lastViewDate;
    @Column(name = "app_ver")
    private int appVersion;
    @Column(name = "page_pk")
    private long pk;
    @Column(name = "block")
    private boolean block = false;

    public User() {
        createDate = Calendar.getInstance();
        lastViewDate = createDate;
    }

    public int getId() {
        return id;
    }

    public int getDiamond() {
        return diamond;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public long getPk() {
        return pk;
    }

    public void setPk(long pk) {
        this.pk = pk;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public Calendar getLastViewDate() {
        return lastViewDate;
    }

    public void setLastViewDate(Calendar lastViewDate) {
        this.lastViewDate = lastViewDate;
    }
}
