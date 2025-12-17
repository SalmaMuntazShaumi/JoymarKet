package model_entity;

public class OrderDetail {
    private String idOrder;
    private String idProduct;
    private int qty;
    private double price;

    public OrderDetail() {}

    public OrderDetail(String idOrder, String idProduct, int qty, double price) {
        this.idOrder = idOrder;
        this.idProduct = idProduct;
        this.qty = qty;
        this.price = price;
    }

    // Getters & Setters
    public String getIdOrder() { return idOrder; }
    public void setIdOrder(String idOrder) { this.idOrder = idOrder; }

    public String getIdProduct() { return idProduct; }
    public void setIdProduct(String idProduct) { this.idProduct = idProduct; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
