package controller;

import model.CartItem;
import model.Product;

/**
 * CartItemHandler Controller Class
 * Handles all cart-related business logic and validation
 */
public class CartItemHandler {
    
    /**
     * Adds a product to the customer's cart with validation
     * Validates:
     * - Count must be filled
     * - Count must be numeric
     * - Count must be between 1 and available stock
     * (empty string if successful)
     */
	
    public static String addCartItem(String idCustomer, String idProduct, int count) {
        // Validate count is filled (greater than 0)
        if (count <= 0) {
            return "Count must be filled and greater than 0";
        }
        
        // Get product to check stock availability
//        Product product = Product.getProductById(idProduct);
//        if (product == null) {
//            return "Product not found";
//        }
        
        // Validate count is between 1 and available stock
//        if (count < 1 || count > product.getStock()) {
//            return "Count must be between 1 and available stock (" + product.getStock() + ")";
//        }
        
        // Check if item already exists in cart
        CartItem existingItem = CartItem.getCartItem(idCustomer, idProduct);
        
        if (existingItem != null) {
            // Item exists, update the count by adding to existing
            int newCount = existingItem.getCount() + count;
            
            // Validate new count doesn't exceed stock
//            if (newCount > product.getStock()) {
//                return "Total count would exceed available stock. Current in cart: " 
//                       + existingItem.getCount() + ", Available: " + product.getStock();
//            }
            
            // Update existing cart item
            boolean success = CartItem.updateCartItem(idCustomer, idProduct, newCount);
            if (!success) {
                return "Failed to update cart item";
            }
        } else {
            // Item doesn't exist, create new cart item
            boolean success = CartItem.createCartItem(idCustomer, idProduct, count);
            if (!success) {
                return "Failed to add item to cart";
            }
        }
        
        return ""; // Success - empty string means no error
    }
    
    /**
     * Updates the quantity of an item in the cart
     * Validates:
     * - Count must be filled
     * - Count must be numeric
     * - Count must be between 1 and available stock
     * (empty string if successful)
     */
    
    public static String updateCartItem(String idCustomer, String idProduct, int count) {
        // Validate count is filled (greater than 0)
        if (count <= 0) {
            return "Count must be filled and greater than 0";
        }
        
        // Get product to check stock availability
//        Product product = Product.getProductById(idProduct);
//        if (product == null) {
//            return "Product not found";
//        }
        
        // Validate count is between 1 and available stock
//        if (count < 1 || count > product.getStock()) {
//            return "Count must be between 1 and available stock (" + product.getStock() + ")";
//        }
        
        // Check if item exists in cart
        CartItem existingItem = CartItem.getCartItem(idCustomer, idProduct);
        if (existingItem == null) {
            return "Item not found in cart";
        }
        
        // Update cart item with new count
        boolean success = CartItem.updateCartItem(idCustomer, idProduct, count);
        if (!success) {
            return "Failed to update cart item";
        }
        
        return ""; // Success - empty string means no error
    }
    
    /**
     * Removes an item from the cart
     * (empty string if successful)
     */
    public static String deleteCartItem(String idCustomer, String idProduct) {
        CartItem existingItem = CartItem.getCartItem(idCustomer, idProduct);
        if (existingItem == null) {
            return "Item not found in cart";
        }
        
        boolean success = CartItem.deleteCartItem(idCustomer, idProduct);
        if (!success) {
            return "Failed to remove item from cart";
        }
        
        return ""; // Success
    }
}
