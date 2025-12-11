package controller;

import handler.CartItemHandler;
import handler.ProductHandler;
import model.CartItem;
import model.Product;
import java.util.List;

public class CartController {
    private static CartController instance;
    private final CartItemHandler cartHandler;
    private final ProductHandler productHandler;
    
    private CartController() {
        cartHandler = CartItemHandler.getInstance();
        productHandler = ProductHandler.getInstance();
    }
    
    public static CartController getInstance() {
        if (instance == null) {
            instance = new CartController();
        }
        return instance;
    }
    
    public String addToCart(String idCustomer, String idProduct, int quantity) {
        if (quantity <= 0) {
            return "Quantity must be greater than 0";
        }
        
        Product product = productHandler.getProduct(idProduct);
        if (product == null) {
            return "Product not found";
        }
        
        if (quantity > product.getStock()) {
            return "Not enough stock available. Available: " + product.getStock();
        }
        
        CartItem existingItem = cartHandler.getCartItem(idCustomer, idProduct);
        if (existingItem != null) {
            int newQuantity = existingItem.getCount() + quantity;
            if (newQuantity > product.getStock()) {
                int available = product.getStock() - existingItem.getCount();
                return "Can only add " + available + " more items";
            }
        }
        
        if (!cartHandler.addCartItem(idCustomer, idProduct, quantity)) {
            return "Failed to add item to cart";
        }
        
        return "";
    }
    
    public String updateCartItem(String idCustomer, String idProduct, int newQuantity) {
        if (newQuantity <= 0) {
            return "Quantity must be greater than 0";
        }
        
        Product product = productHandler.getProduct(idProduct);
        if (product == null) {
            return "Product not found";
        }
        
        if (newQuantity > product.getStock()) {
            return "Only " + product.getStock() + " items available in stock";
        }
        
        CartItem existingItem = cartHandler.getCartItem(idCustomer, idProduct);
        if (existingItem == null) {
            return "Item not found in cart";
        }
        
        if (!cartHandler.updateCartItem(idCustomer, idProduct, newQuantity)) {
            return "Failed to update cart item";
        }
        
        return "";
    }
    
    public String removeFromCart(String idCustomer, String idProduct) {
        CartItem existingItem = cartHandler.getCartItem(idCustomer, idProduct);
        if (existingItem == null) {
            return "Item not found in cart";
        }
        
        if (!cartHandler.deleteCartItem(idCustomer, idProduct)) {
            return "Failed to remove item from cart";
        }
        
        return "";
    }
    
    public List<CartItem> getCustomerCart(String idCustomer) {
        return cartHandler.getCartItemsByCustomer(idCustomer);
    }
    
    public double getCartTotal(String idCustomer) {
        return cartHandler.getCartTotal(idCustomer);
    }
    
    public String clearCart(String idCustomer) {
        if (!cartHandler.clearCustomerCart(idCustomer)) {
            return "Failed to clear cart";
        }
        return "";
    }
    
    public int getCartItemCount(String idCustomer) {
        return cartHandler.getCartItemCount(idCustomer);
    }
}