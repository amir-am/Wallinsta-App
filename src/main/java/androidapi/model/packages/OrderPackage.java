package androidapi.model.packages;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Amir Hossein on 7/24/2017.
 */
@Entity
@Table(name = "order_pack")
public class OrderPackage {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "type")
    private PackageType type;
    @Column(name = "count")
    private int count;
    @Column(name = "diamond")
    private int diamond;
    @Column(name = "active")
    private boolean active = true;
    @Column(name = "dis_diamond")
    private int discountedDiamond;

    public OrderPackage() {
    }

    public PackageType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDiamond(int cost) {
        this.diamond = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDiscountedDiamond() {
        return discountedDiamond;
    }

    public void setDiscountedDiamond(int discountedDiamond) {
        this.discountedDiamond = discountedDiamond;
    }
}
