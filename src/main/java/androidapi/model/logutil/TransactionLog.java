package androidapi.model.logutil;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Calendar;

/**
 * Created by Amir Hossein on 8/20/2017.
 */
@Entity
@Table(name = "transaction_log", indexes = {@Index(name = "log_index", columnList = "type")})
public class TransactionLog {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "amount")
    private int amount;
    @Column(name = "date")
    private Calendar date;
    @Column(name = "type")
    private LogType type;

    public TransactionLog() {
    }

    public TransactionLog(int userId, int amount, LogType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        date = Calendar.getInstance();
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }
}
