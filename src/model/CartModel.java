package model;

import model_entity.CartItem;
import model_entity.Product;
import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartModel {
    
    // === Database Operations (CRUD) ===
    
    public static boolean addToCart(String idCustomer, String idProduct, int count) {
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
    
    public static boolean updateCartItem(String idCustomer, String idProduct, int count) {
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
    
    public static boolean deleteCartItem(String idCustomer, String idProduct) {
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
    
    public static CartItem getCartItem(String idCustomer, String idProduct) {
        String query = "SELECT * FROM CartItem WHERE idCustomer = ? AND idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            stmt.setString(2, idProduct);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Product product = ProductModel.getProductById(idProduct);
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
    
    public static List<CartItem> getCartItemsByCustomer(String idCustomer) {
        List<CartItem> cartItems = new ArrayList<>();
        String query = "SELECT * FROM CartItem WHERE idCustomer = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String productId = rs.getString("idProduct");
                Product product = ProductModel.getProductById(productId);
                
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
    
    public static boolean clearCustomerCart(String idCustomer) {
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
    
    public static double calculateCartTotal(String idCustomer) {
        List<CartItem> items = getCartItemsByCustomer(idCustomer);
        double total = 0;
        
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        
        return total;
    }
    
    public static int getCartItemCount(String idCustomer) {
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
    public static boolean removeFromCart(String idCustomer, String idProduct) {
        return deleteCartItem(idCustomer, idProduct);
    }

}