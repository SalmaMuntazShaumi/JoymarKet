package model;

import model_entity.Product;
import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductModel {
    
    // === Database Operations (CRUD) ===
    
    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM Product ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                );
                list.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all products: " + e.getMessage());
        }
        return list;
    }
    
    public static Product getProductById(String idProduct) {
        String query = "SELECT * FROM Product WHERE idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idProduct);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Product(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting product by ID: " + e.getMessage());
        }
        return null;
    }
    
    public static boolean updateProductStock(String idProduct, int newStock) {
        String query = "UPDATE Product SET stock = ? WHERE idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, newStock);
            stmt.setString(2, idProduct);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product stock: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean decreaseProductStock(String idProduct, int quantity) {
        Product product = getProductById(idProduct);
        if (product == null || !product.canFulfillOrder(quantity)) {
            return false;
        }
        
        int newStock = product.getStock() - quantity;
        return updateProductStock(idProduct, newStock);
    }
    
    public static List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM Product WHERE name LIKE ? OR category LIKE ? OR idProduct LIKE ? ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchTerm = "%" + keyword + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                );
                list.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
        return list;
    }
    
    public static List<Product> getProductsByCategory(String category) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM Product WHERE category = ? ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                );
                list.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error getting products by category: " + e.getMessage());
        }
        return list;
    }
    
    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM Product ORDER BY category";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }
        return categories;
    }
    
    public static boolean addProduct(Product product) {
        String query = "INSERT INTO Product (idProduct, name, price, stock, category) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, product.getIdProduct());
            stmt.setString(2, product.getName());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());
            stmt.setString(5, product.getCategory());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean updateProduct(Product product) {
        String query = "UPDATE Product SET name = ?, price = ?, stock = ?, category = ? WHERE idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getCategory());
            stmt.setString(5, product.getIdProduct());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteProduct(String idProduct) {
        String query = "DELETE FROM Product WHERE idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idProduct);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }
    
    public static List<Product> getLowStockProducts(int threshold) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT * FROM Product WHERE stock <= ? ORDER BY stock";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, threshold);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getString("idProduct"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category")
                );
                list.add(product);
            }
        } catch (SQLException e) {
            System.err.println("Error getting low stock products: " + e.getMessage());
        }
        return list;
    }
}