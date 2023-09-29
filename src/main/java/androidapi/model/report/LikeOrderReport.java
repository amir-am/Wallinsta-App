package androidapi.model.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;

@Entity
@Table(name = "like_order_report")
public class LikeOrderReport {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "order_id")
    private int orderId;
    @Column(name = "user_id")
    private int userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private String text;
    @Column(name = "temporary_text", length = 2048)
    private byte[] temporaryText;

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        String str = "";
        try {
            str = new String(temporaryText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getTemporaryText() {
        return temporaryText;
    }

    public void setTemporaryText() {
        if (text.length() < 2048) {
            temporaryText = text.substring(0, text.length()).getBytes();
        } else {
            temporaryText = text.substring(0, 2048).getBytes();
        }
    }
}
