package handler;

import database.DBConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserHandler {
    private static UserHandler instance;
    
    private UserHandler() {}
    
    public static UserHandler getInstance() {
        if (instance == null) {
            instance = new UserHandler();
        }
        return instance;
    }
    
    // Get user by ID (returns appropriate subclass based on role)
    public User getUser(String idUser) {
        String query = "SELECT * FROM User WHERE idUser = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idUser);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                
                if ("admin".equals(role)) {
                    return getAdminByIdUser(idUser);
                } else if ("customer".equals(role)) {
                    return getCustomerByIdUser(idUser);
                } else if ("courier".equals(role)) {
                    return getCourierByIdUser(idUser);
                } else {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting user: " + e.getMessage());
        }
        return null;
    }
    
    // Get user by email (for login)
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM User WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                String idUser = rs.getString("idUser");
                
                if ("admin".equals(role)) {
                    return getAdminByIdUser(idUser);
                } else if ("customer".equals(role)) {
                    return getCustomerByIdUser(idUser);
                } else if ("courier".equals(role)) {
                    return getCourierByIdUser(idUser);
                } else {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting user by email: " + e.getMessage());
        }
        return null;
    }
    
    // Login method
    public User login(String email, String password) {
        String query = "SELECT * FROM User WHERE email = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                String idUser = rs.getString("idUser");
                
                if ("admin".equals(role)) {
                    return getAdminByIdUser(idUser);
                } else if ("customer".equals(role)) {
                    return getCustomerByIdUser(idUser);
                } else if ("courier".equals(role)) {
                    return getCourierByIdUser(idUser);
                } else {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
        return null;
    }
    
    public boolean registerCustomer(Customer customer) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if email already exists
            if (getUserByEmail(customer.getEmail()) != null) {
                return false;
            }
            
            // Insert into User table with gender
            String userQuery = "INSERT INTO User (idUser, fullName, email, password, phone, address, role, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                userStmt.setString(1, customer.getIdUser());
                userStmt.setString(2, customer.getFullName());
                userStmt.setString(3, customer.getEmail());
                userStmt.setString(4, customer.getPassword());
                userStmt.setString(5, customer.getPhone());
                userStmt.setString(6, customer.getAddress());
                userStmt.setString(7, "customer");
                userStmt.setString(8, customer.getGender());
                
                userStmt.executeUpdate();
            }
            
            // Insert into Customer table
            String customerQuery = "INSERT INTO Customer (idCustomer, balance) VALUES (?, ?)";
            
            try (PreparedStatement customerStmt = conn.prepareStatement(customerQuery)) {
                customerStmt.setString(1, customer.getIdUser()); // idCustomer = idUser
                customerStmt.setDouble(2, customer.getBalance());
                
                customerStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Error registering customer: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean registerCourier(Courier courier) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if email already exists
            if (getUserByEmail(courier.getEmail()) != null) {
                return false;
            }
            
            // Insert into User table with gender
            String userQuery = "INSERT INTO User (idUser, fullName, email, password, phone, address, role, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                userStmt.setString(1, courier.getIdUser());
                userStmt.setString(2, courier.getFullName());
                userStmt.setString(3, courier.getEmail());
                userStmt.setString(4, courier.getPassword());
                userStmt.setString(5, courier.getPhone());
                userStmt.setString(6, courier.getAddress());
                userStmt.setString(7, "courier");
                userStmt.setString(8, courier.getGender());
                
                userStmt.executeUpdate();
            }
            
            // Insert into Courier table
            String courierQuery = "INSERT INTO Courier (idCourier, vehicleType, vehiclePlate) VALUES (?, ?, ?)";
            
            try (PreparedStatement courierStmt = conn.prepareStatement(courierQuery)) {
                courierStmt.setString(1, courier.getIdUser()); // idCourier = idUser
                courierStmt.setString(2, courier.getVehicleType());
                courierStmt.setString(3, courier.getVehiclePlate());
                
                courierStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Error registering courier: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean registerAdmin(Admin admin) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if email already exists
            if (getUserByEmail(admin.getEmail()) != null) {
                return false;
            }
            
            // Insert into User table with gender
            String userQuery = "INSERT INTO User (idUser, fullName, email, password, phone, address, role, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                userStmt.setString(1, admin.getIdUser());
                userStmt.setString(2, admin.getFullName());
                userStmt.setString(3, admin.getEmail());
                userStmt.setString(4, admin.getPassword());
                userStmt.setString(5, admin.getPhone());
                userStmt.setString(6, admin.getAddress());
                userStmt.setString(7, "admin");
                userStmt.setString(8, admin.getGender());
                
                userStmt.executeUpdate();
            }
            
            // Insert into Admin table
            String adminQuery = "INSERT INTO Admin (idAdmin, emergencyContact) VALUES (?, ?)";
            
            try (PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {
                adminStmt.setString(1, admin.getIdUser()); // idAdmin = idUser
                adminStmt.setString(2, admin.getEmergencyContact());
                
                adminStmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("Error registering admin: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean editProfile(User user) {
        String query = "UPDATE User SET fullName = ?, email = ?, phone = ?, address = ?, gender = ? WHERE idUser = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getAddress());
            stmt.setString(5, user.getGender());
            stmt.setString(6, user.getIdUser());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }
    
    // Change password
    public boolean changePassword(String idUser, String newPassword) {
        String query = "UPDATE User SET password = ? WHERE idUser = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, newPassword);
            stmt.setString(2, idUser);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
    
    // Get customer by ID
    public Customer getCustomer(String idCustomer) {
        String query = "SELECT u.*, c.balance FROM User u " +
                      "JOIN Customer c ON u.idUser = c.idCustomer " +
                      "WHERE u.idUser = ? AND u.role = 'customer'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCustomer);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                mapResultSetToUser(rs, customer);
                customer.setBalance(rs.getDouble("balance"));
                return customer;
            }
        } catch (SQLException e) {
            System.out.println("Error getting customer: " + e.getMessage());
        }
        return null;
    }
    
    // Get admin by ID
    public Admin getAdmin(String idAdmin) {
        String query = "SELECT u.*, a.emergencyContact FROM User u " +
                      "JOIN Admin a ON u.idUser = a.idAdmin " +
                      "WHERE u.idUser = ? AND u.role = 'admin'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idAdmin);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Admin admin = new Admin();
                mapResultSetToUser(rs, admin);
                admin.setEmergencyContact(rs.getString("emergencyContact"));
                return admin;
            }
        } catch (SQLException e) {
            System.out.println("Error getting admin: " + e.getMessage());
        }
        return null;
    }
    
    // Get courier by ID
    public Courier getCourier(String idCourier) {
        String query = "SELECT u.*, c.vehicleType, c.vehiclePlate FROM User u " +
                      "JOIN Courier c ON u.idUser = c.idCourier " +
                      "WHERE u.idUser = ? AND u.role = 'courier'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idCourier);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Courier courier = new Courier();
                mapResultSetToUser(rs, courier);
                courier.setVehicleType(rs.getString("vehicleType"));
                courier.setVehiclePlate(rs.getString("vehiclePlate"));
                return courier;
            }
        } catch (SQLException e) {
            System.out.println("Error getting courier: " + e.getMessage());
        }
        return null;
    }
    
    // Update customer balance (top-up)
    public boolean topUpBalance(String idCustomer, double amount) {
        String query = "UPDATE Customer SET balance = balance + ? WHERE idCustomer = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, amount);
            stmt.setString(2, idCustomer);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error topping up balance: " + e.getMessage());
            return false;
        }
    }
    
    // Get all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT u.*, c.balance FROM User u " +
                      "JOIN Customer c ON u.idUser = c.idCustomer " +
                      "WHERE u.role = 'customer' " +
                      "ORDER BY u.fullName";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                mapResultSetToUser(rs, customer);
                customer.setBalance(rs.getDouble("balance"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all customers: " + e.getMessage());
        }
        return customers;
    }
    
    // Helper methods - FIXED
    private Admin getAdminByIdUser(String idUser) {
        String query = "SELECT u.*, a.emergencyContact FROM User u " +
                      "LEFT JOIN Admin a ON u.idUser = a.idAdmin " +
                      "WHERE u.idUser = ? AND u.role = 'admin'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idUser);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Admin admin = new Admin();
                mapResultSetToUser(rs, admin);
                admin.setEmergencyContact(rs.getString("emergencyContact"));
                return admin;
            }
        } catch (SQLException e) {
            System.out.println("Error getting admin by user ID: " + e.getMessage());
        }
        return null;
    }
    
    private Customer getCustomerByIdUser(String idUser) {
        String query = "SELECT u.*, c.balance FROM User u " +
                      "LEFT JOIN Customer c ON u.idUser = c.idCustomer " +
                      "WHERE u.idUser = ? AND u.role = 'customer'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idUser);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                mapResultSetToUser(rs, customer);
                customer.setBalance(rs.getDouble("balance"));
                return customer;
            }
        } catch (SQLException e) {
            System.out.println("Error getting customer by user ID: " + e.getMessage());
        }
        return null;
    }
    
    private Courier getCourierByIdUser(String idUser) {
        String query = "SELECT u.*, c.vehicleType, c.vehiclePlate FROM User u " +
                      "LEFT JOIN Courier c ON u.idUser = c.idCourier " +
                      "WHERE u.idUser = ? AND u.role = 'courier'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, idUser);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Courier courier = new Courier();
                mapResultSetToUser(rs, courier);
                courier.setVehicleType(rs.getString("vehicleType"));
                courier.setVehiclePlate(rs.getString("vehiclePlate"));
                return courier;
            }
        } catch (SQLException e) {
            System.out.println("Error getting courier by user ID: " + e.getMessage());
        }
        return null;
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        mapResultSetToUser(rs, user);
        return user;
    }
    
    private void mapResultSetToUser(ResultSet rs, User user) throws SQLException {
        user.setIdUser(rs.getString("idUser"));
        user.setFullName(rs.getString("fullName"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setRole(rs.getString("role"));
        user.setGender(rs.getString("gender"));
    }
    
    public double getCustomerBalance(String customerId) {
        String sql = "SELECT balance FROM Customer WHERE idCustomer = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}