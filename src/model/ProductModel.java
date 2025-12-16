package model;

import model_entity.Product;
import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductModel {
    
    // === Database Operations (CRUD) ===
    
    // Get all products
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
    
    // Get product by ID
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
    
    // Update product stock
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
    
    // Decrease product stock (for checkout)
    public static boolean decreaseProductStock(String idProduct, int quantity) {
        Product product = getProductById(idProduct);
        if (product == null || !product.canFulfillOrder(quantity)) {
            return false;
        }
        
        int newStock = product.getStock() - quantity;
        return updateProductStock(idProduct, newStock);
    }
    
    // Restock product (add stock)
    public static boolean restockProduct(String idProduct, int additionalStock) {
        if (additionalStock <= 0) {
            return false;
        }
        
        Product product = getProductById(idProduct);
        if (product == null) {
            return false;
        }
        
        int newStock = product.getStock() + additionalStock;
        return updateProductStock(idProduct, newStock);
    }
    
    // Search products
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
    
    // Get products by category
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
    
    // Get all categories
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
    
    // Add new product
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
    
    // Update product details (all fields)
    public static boolean updateProductDetails(Product product) {
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
            System.err.println("Error updating product details: " + e.getMessage());
            return false;
        }
    }
    
    // Delete product
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
    
    // Get low stock products
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
    
    // Check if product is in any customer cart
    public static boolean isProductInCart(String idProduct) {
        String query = "SELECT COUNT(*) FROM CartItem WHERE idProduct = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idProduct);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking product in cart: " + e.getMessage());
        }
        return false;
    }
    
    // Generate unique product ID
    public static String generateProductId() {
        String prefix = "PROD";
        
        // Format: PROD + YYMM + 4 digit random
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String datePart = now.format(java.time.format.DateTimeFormatter.ofPattern("yyMM"));
        String randomPart = String.format("%04d", (int)(Math.random() * 10000));
        
        // Cek apakah ID sudah ada
        String generatedId = prefix + datePart + randomPart;
        
        // Jika sudah ada, generate lagi (max 10 attempts)
        int attempts = 0;
        while (getProductById(generatedId) != null && attempts < 10) {
            randomPart = String.format("%04d", (int)(Math.random() * 10000));
            generatedId = prefix + datePart + randomPart;
            attempts++;
        }
        
        return generatedId;
    }
    
    // Get total number of products
    public static int getTotalProducts() {
        String query = "SELECT COUNT(*) FROM Product";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total products: " + e.getMessage());
        }
        return 0;
    }
    
    // Get total value of inventory
    public static double getInventoryValue() {
        String query = "SELECT SUM(price * stock) FROM Product";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting inventory value: " + e.getMessage());
        }
        return 0.0;
    }
    
    // Get out of stock products
    public static List<Product> getOutOfStockProducts() {
        return getLowStockProducts(0);
    }
    
    // Update product category
    public static boolean updateProductCategory(String oldCategory, String newCategory) {
        String query = "UPDATE Product SET category = ? WHERE category = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, newCategory);
            stmt.setString(2, oldCategory);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product category: " + e.getMessage());
            return false;
        }
    }
    
    // Get products with pagination
    public static List<Product> getProductsWithPagination(int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String query = "SELECT * FROM Product ORDER BY name LIMIT ? OFFSET ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
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
            System.err.println("Error getting products with pagination: " + e.getMessage());
        }
        return list;
    }
    
    // Get products sorted by price
    public static List<Product> getProductsSortedByPrice(boolean ascending) {
        List<Product> list = new ArrayList<>();
        String order = ascending ? "ASC" : "DESC";
        String query = "SELECT * FROM Product ORDER BY price " + order;
        
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
            System.err.println("Error getting products sorted by price: " + e.getMessage());
        }
        return list;
    }
    
    // Get products sorted by stock
    public static List<Product> getProductsSortedByStock(boolean ascending) {
        List<Product> list = new ArrayList<>();
        String order = ascending ? "ASC" : "DESC";
        String query = "SELECT * FROM Product ORDER BY stock " + order;
        
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
            System.err.println("Error getting products sorted by stock: " + e.getMessage());
        }
        return list;
    }
    
    // Get product statistics
    public static class ProductStats {
        public int totalProducts;
        public int outOfStock;
        public int lowStock;
        public double totalValue;
        public String mostExpensiveProduct;
        public String cheapestProduct;
    }
    
    public static ProductStats getProductStatistics() {
        ProductStats stats = new ProductStats();
        
        try (Connection conn = DBConnection.getConnection()) {
            // Total products
            String query1 = "SELECT COUNT(*) FROM Product";
            try (PreparedStatement stmt = conn.prepareStatement(query1);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.totalProducts = rs.getInt(1);
                }
            }
            
            // Out of stock
            String query2 = "SELECT COUNT(*) FROM Product WHERE stock = 0";
            try (PreparedStatement stmt = conn.prepareStatement(query2);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.outOfStock = rs.getInt(1);
                }
            }
            
            // Low stock (<= 10)
            String query3 = "SELECT COUNT(*) FROM Product WHERE stock > 0 AND stock <= 10";
            try (PreparedStatement stmt = conn.prepareStatement(query3);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.lowStock = rs.getInt(1);
                }
            }
            
            // Total inventory value
            String query4 = "SELECT SUM(price * stock) FROM Product";
            try (PreparedStatement stmt = conn.prepareStatement(query4);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.totalValue = rs.getDouble(1);
                }
            }
            
            // Most expensive product
            String query5 = "SELECT name FROM Product WHERE price = (SELECT MAX(price) FROM Product)";
            try (PreparedStatement stmt = conn.prepareStatement(query5);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.mostExpensiveProduct = rs.getString(1);
                }
            }
            
            // Cheapest product
            String query6 = "SELECT name FROM Product WHERE price = (SELECT MIN(price) FROM Product)";
            try (PreparedStatement stmt = conn.prepareStatement(query6);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.cheapestProduct = rs.getString(1);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting product statistics: " + e.getMessage());
        }
        
        return stats;
    }
}