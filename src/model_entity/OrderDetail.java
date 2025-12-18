package model_entity;

public class OrderDetail {
    private String idOrder;
    private String idProduct;
    private String productName;
    private int qty;
    private double price;
    private double subtotal;
    
    public OrderDetail() {}
    
    public OrderDetail(String idOrder, String idProduct, int qty) {
        this.idOrder = idOrder;
        this.idProduct = idProduct;
        this.qty = qty;
    }
    
    // Getters and Setters
    public String getIdOrder() { return idOrder; }
    public void setIdOrder(String idOrder) { this.idOrder = idOrder; }
    
    public String getIdProduct() { return idProduct; }
    public void setIdProduct(String idProduct) { this.idProduct = idProduct; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public double getSubtotal() { return qty * price; }
}