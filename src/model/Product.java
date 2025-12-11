package model;

public class Product {
    private String idProduct;
    private String name;
    private double price;
    private int stock;
    private String category;

    public Product(String idProduct, String name, double price, int stock, String category) {
        this.idProduct = idProduct;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // Getters
    public String getIdProduct() { return idProduct; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getCategory() { return category; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return String.format("%s - %s (Rp%,.0f) - Stock: %d", 
            idProduct, name, price, stock);
    }
}