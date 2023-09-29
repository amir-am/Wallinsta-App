package androidapi.model.logutil;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by Amir Hossein on 9/4/2017.
 */
@Entity
@Table(name = "purchase_log")
public class PurchaseLog {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "purchase_token")
    private String purchaseToken;
    @Column(name = "product_id")
    private String productId;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "date")
    private Calendar date;
    @Column(name = "state")
    private int state;

    public PurchaseLog() {
    }

    public PurchaseLog(String purchaseToken, String productId, int userId, int state) {
        this.purchaseToken = purchaseToken;
        this.productId = productId;
        this.userId = userId;
        date = Calendar.getInstance();
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
