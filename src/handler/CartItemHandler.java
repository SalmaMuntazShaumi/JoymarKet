package handler;

import database.DBConnection;
import model.CartItem;
import model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemHandler {
    private static CartItemHandler instance;
    
    private CartItemHandler() {}
    
    public static CartItemHandler getInstance() {
        if (instance == null) {
            instance = new CartItemHandler();
        }
        return instance;
    }
    
    public boolean addCartItem(String idCustomer, String idProduct, int count) {
        // Check if item already exists
        CartItem existing = getCartItem(idCustomer, idProduct);
        
        if (existing != null) {
            // Update existing item
            int newCount = existing.getCount() + count;
            return updateCartItem(idCustomer, idProduct, newCount);
        } else {
            // Add new item
            String query = "INSERT INTO CartItem (idCustomer, idProduct, count) VALUES (?, ?, ?)";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, idCustomer);
                stmt.setString(2, idProduct);
                stmt.setInt(3, count);
                
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error adding cart item: " + e.getMessage());
                return false;
            }
        }
    }
    
    public boolean updateCartItem(String idCustomer, String idProduct, int count) {
        String query = "UPDATE CartItem SET count = ? WHERE idCustomer = ? AND idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, count);
            stmt.setString(2, idCustomer);
            stmt.setString(3, idProduct);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteCartItem(String idCustomer, String idProduct) {
        String query = "DELETE FROM CartItem WHERE idCustomer = ? AND idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            stmt.setString(2, idProduct);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting cart item: " + e.getMessage());
            return false;
        }
    }
    
    public CartItem getCartItem(String idCustomer, String idProduct) {
        String query = "SELECT * FROM CartItem WHERE idCustomer = ? AND idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            stmt.setString(2, idProduct);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Product product = ProductHandler.getInstance().getProduct(idProduct);
                return new CartItem(
                    rs.getString("idCustomer"),
                    rs.getString("idProduct"),
                    rs.getInt("count"),
                    product
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart item: " + e.getMessage());
        }
        return null;
    }
    
    public List<CartItem> getCartItemsByCustomer(String idCustomer) {
        List<CartItem> cartItems = new ArrayList<>();
        String query = "SELECT * FROM CartItem WHERE idCustomer = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            ResultSet rs = stmt.executeQuery();
            
            ProductHandler productHandler = ProductHandler.getInstance();
            
            while (rs.next()) {
                String productId = rs.getString("idProduct");
                Product product = productHandler.getProduct(productId);
                
                CartItem item = new CartItem(
                    rs.getString("idCustomer"),
                    productId,
                    rs.getInt("count"),
                    product
                );
                cartItems.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart items: " + e.getMessage());
        }
        return cartItems;
    }
    
    public boolean clearCustomerCart(String idCustomer) {
        String query = "DELETE FROM CartItem WHERE idCustomer = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        }
    }
    
    public double getCartTotal(String idCustomer) {
        List<CartItem> items = getCartItemsByCustomer(idCustomer);
        double total = 0;
        
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        
        return total;
    }
    
    public int getCartItemCount(String idCustomer) {
        String query = "SELECT SUM(count) as total FROM CartItem WHERE idCustomer = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart count: " + e.getMessage());
        }
        return 0;
    }
}