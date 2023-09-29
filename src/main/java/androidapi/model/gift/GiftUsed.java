package androidapi.model.gift;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "gift_used")
public class GiftUsed {
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "user_id")
    private int userId;
    @OneToOne
    @JoinColumn(name = "gift_code")
    private GiftCode giftCode;
    @Column(name = "use_date")
    private Calendar useDate;

    public GiftUsed() {
        useDate = Calendar.getInstance();
    }

    public GiftUsed(int userId, GiftCode giftCode) {
        this.userId = userId;
        this.giftCode = giftCode;
        useDate = Calendar.getInstance();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public GiftCode getGiftCode() {
        return giftCode;
    }

    public void setGiftCode(GiftCode giftCode) {
        this.giftCode = giftCode;
    }

    public Calendar getUseDate() {
        return useDate;
    }

    public void setUseDate(Calendar useDate) {
        this.useDate = useDate;
    }
}
