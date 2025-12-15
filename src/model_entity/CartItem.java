package model_entity;


/**
 * CartItem Entity Class
 * Pure data structure without business logic
 */
public class CartItem {
    private String idCustomer;
    private String idProduct;
    private int count;
    private Product product; // Reference to product details
    
    // Constructors
    public CartItem() {}
    
    public CartItem(String idCustomer, String idProduct, int count) {
        this.idCustomer = idCustomer;
        this.idProduct = idProduct;
        this.count = count;
    }
    
    public CartItem(String idCustomer, String idProduct, int count, Product product) {
        this.idCustomer = idCustomer;
        this.idProduct = idProduct;
        this.count = count;
        this.product = product;
    }
    
    // Getters and Setters
    public String getIdCustomer() { return idCustomer; }
    public void setIdCustomer(String idCustomer) { this.idCustomer = idCustomer; }
    
    public String getIdProduct() { return idProduct; }
    public void setIdProduct(String idProduct) { this.idProduct = idProduct; }
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    // Simple calculated getter (masih boleh karena hanya transformasi data)
    public double getTotalPrice() {
        if (product != null) {
            return product.getPrice() * count;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        if (product != null) {
            return String.format("%s x %d = Rp%,.0f", 
                product.getName(), count, getTotalPrice());
        }
        return String.format("Product: %s x %d", idProduct, count);
    }
}