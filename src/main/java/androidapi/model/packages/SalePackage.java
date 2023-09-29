package androidapi.model.packages;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by Amir Hossein on 7/24/2017.
 */
@Entity
@Table(name = "sale_pack")
public class SalePackage {
    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
    @Column(name = "id")
    private int id;
    @Column(name = "diamond")
    private int diamond;
    @Column(name = "price")
    private int price;
    @Column(name = "dis_price")
    private int discountedPrice;
    @Column(name = "active")
    private boolean active;
    @Column(name = "sku")
    private String sku;

    public SalePackage() {
    }

    public SalePackage(int diamond, int price) {
        this.diamond = diamond;
        this.price = price;
        this.active = true;
        discountedPrice = price;
    }

    public int getDiamond() {
        return diamond;
    }

    public int getPrice() {
        return price;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
