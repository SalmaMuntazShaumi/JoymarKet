package model;

/**
 * CartItem Model Class
 * Represents a single item in the shopping cart
 * Contains relationship between Customer, Product, and quantity
 */
public class CartItem {
    private String idCustomer;
    private String idProduct;
    private int count;
    
    // Constructor
    public CartItem(String idCustomer, String idProduct, int count) {
        this.idCustomer = idCustomer;
        this.idProduct = idProduct;
        this.count = count;
    }
    
    // Default Constructor
    public CartItem() {
    }
    
    // Getters and Setters
    public String getIdCustomer() {
        return idCustomer;
    }
    
    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }
    
    public String getIdProduct() {
        return idProduct;
    }
    
    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    /**
     * Creates a new CartItem record in the database
     * return true if successful, false otherwise
     */
//    public static boolean createCartItem(String idCustomer, String idProduct, int count) {
//        String query = "INSERT INTO cartitem (idCustomer, idProduct, count) VALUES (?, ?, ?)";
//        return Database.executeUpdate(query, idCustomer, idProduct, count);
//    }
    
    /**
     * Updates the count of an existing cart item
     * return true if successful, false otherwise
     */
//    public static boolean updateCartItem(String idCustomer, String idProduct, int count) {
//        String query = "UPDATE cartitem SET count = ? WHERE idCustomer = ? AND idProduct = ?";
//        return Database.executeUpdate(query, count, idCustomer, idProduct);
//    }
    
    /**
     * Deletes a cart item from the database
     * true if successful, false otherwise
     */
//    public static boolean deleteCartItem(String idCustomer, String idProduct) {
//        String query = "DELETE FROM cartitem WHERE idCustomer = ? AND idProduct = ?";
//        return Database.executeUpdate(query, idCustomer, idProduct);
//    }
    
    /**
     * Gets a specific cart item
     * @param idCustomer Customer ID
     * @param idProduct Product ID
     * @return CartItem object or null if not found
     */
//    public static CartItem getCartItem(String idCustomer, String idProduct) {
//        String query = "SELECT * FROM cartitem WHERE idCustomer = ? AND idProduct = ?";
//        return Database.executeQuerySingle(query, CartItem.class, idCustomer, idProduct);
//    }
}
