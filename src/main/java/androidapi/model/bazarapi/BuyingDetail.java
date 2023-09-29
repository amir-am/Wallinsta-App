package androidapi.model.bazarapi;

/**
 * Created by Amir Hossein on 8/24/2017.
 */
public class BuyingDetail {
    private String purchaseToken;
    private String productId;

    public BuyingDetail() {
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
}
