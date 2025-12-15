package model;

import model_entity.Customer;
import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerModel {
    
    // Create customer
	public static Customer createCustomer(Customer customer) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Generate ID otomatis
            String generatedId = UserModel.generateUserId("customer");
            customer.setIdUser(generatedId);
            
            // Create user record
            if (!UserModel.createUserRecord(customer)) {
                conn.rollback();
                return null;
            }
            
            // Create customer record
            String query = "INSERT INTO Customer (idCustomer, balance) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, customer.getIdUser());
                stmt.setDouble(2, customer.getBalance());
                stmt.executeUpdate();
            }
            
            conn.commit();
            return customer; // Return customer dengan ID yang sudah digenerate
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            System.err.println("Error creating customer: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    // Get full customer details
    public static Customer getCustomerById(String idCustomer) {
        String query = "SELECT u.*, c.balance FROM User u " +
                      "JOIN Customer c ON u.idUser = c.idCustomer " +
                      "WHERE u.idUser = ? AND u.role = 'customer'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setIdUser(rs.getString("idUser"));
                customer.setFullName(rs.getString("fullName"));
                customer.setEmail(rs.getString("email"));
                customer.setPassword(rs.getString("password"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setGender(rs.getString("gender"));
                customer.setBalance(rs.getDouble("balance"));
                return customer;
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer: " + e.getMessage());
        }
        return null;
    }
    // Top-up balance
    public static boolean topUpBalance(String idCustomer, double amount) {
        String query = "UPDATE Customer SET balance = balance + ? WHERE idCustomer = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, amount);
            stmt.setString(2, idCustomer);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error topping up balance: " + e.getMessage());
            return false;
        }
    }
    
    // Deduct balance
    public static boolean deductBalance(String idCustomer, double amount) {
        Customer customer = getCustomerById(idCustomer);
        if (customer == null || !customer.canAfford(amount)) {
            return false;
        }
        
        String query = "UPDATE Customer SET balance = balance - ? WHERE idCustomer = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, amount);
            stmt.setString(2, idCustomer);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deducting balance: " + e.getMessage());
            return false;
        }
    }
    
    // Get customer balance
    public static double getCustomerBalance(String idCustomer) {
        String query = "SELECT balance FROM Customer WHERE idCustomer = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer balance: " + e.getMessage());
        }
        return 0.0;
    }
    
    // Get all customers
    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT u.*, c.balance FROM User u " +
                      "JOIN Customer c ON u.idUser = c.idCustomer " +
                      "WHERE u.role = 'customer' ORDER BY u.fullName";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setIdUser(rs.getString("idUser"));
                customer.setFullName(rs.getString("fullName"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getString("phone"));
                customer.setAddress(rs.getString("address"));
                customer.setGender(rs.getString("gender"));
                customer.setBalance(rs.getDouble("balance"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all customers: " + e.getMessage());
        }
        return customers;
    }
}