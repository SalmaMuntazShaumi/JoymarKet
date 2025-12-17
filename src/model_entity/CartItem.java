package model_entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class CartItem {

    private String idCustomer;
    private String idProduct;
    private int count;
    private Product product;

    // === UI ONLY (checkbox) ===
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    public CartItem() {}

    public CartItem(String idCustomer, String idProduct, int count, Product product) {
        this.idCustomer = idCustomer;
        this.idProduct = idProduct;
        this.count = count;
        this.product = product;
    }

    // ===== GETTER SETTER =====
    public String getIdCustomer() { return idCustomer; }
    public String getIdProduct() { return idProduct; }
    public int getCount() { return count; }
    public Product getProduct() { return product; }

    public void setCount(int count) { this.count = count; }

    // ===== CHECKBOX SUPPORT =====
    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean v) {
        selected.set(v);
    }

    // ===== CALCULATED =====
    public double getTotalPrice() {
        return product != null ? product.getPrice() * count : 0;
    }
}