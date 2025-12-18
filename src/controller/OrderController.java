package controller;

import database.DBConnection;
import model_entity.OrderHeader;
import model_entity.OrderDetail;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    
    private static OrderController instance;
    
    private OrderController() {}
    
    public static OrderController getInstance() {
        if (instance == null) {
            instance = new OrderController();
        }
        return instance;
    }
    
    // Get all orders for a specific customer
 // In OrderController.java, use this simpler query:
 // In OrderController.java
    public List<OrderHeader> getCustomerOrders(String customerId) {
        List<OrderHeader> orders = new ArrayList<>();
        
        // Updated SQL with JOIN to Delivery table
        String sql = "SELECT oh.idOrder, oh.idCustomer, oh.idPromo, " +
                     "COALESCE(d.status, oh.status) as status, " + // Priority: Delivery status first
                     "oh.totalAmount, oh.orderedAt " +
                     "FROM OrderHeader oh " +
                     "LEFT JOIN Delivery d ON oh.idOrder = d.idOrder " +
                     "WHERE oh.idCustomer = ? " +
                     "ORDER BY oh.orderedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderHeader order = new OrderHeader();
                order.setIdOrder(rs.getString("idOrder"));
                order.setIdCustomer(rs.getString("idCustomer"));
                order.setIdPromo(rs.getString("idPromo"));
                
                // This now gets the status from the Delivery table if it exists
                order.setStatus(rs.getString("status"));
                order.setTotalAmount(rs.getDouble("totalAmount"));
                
                Timestamp timestamp = rs.getTimestamp("orderedAt");
                if (timestamp != null) {
                    order.setOrderedAt(timestamp.toLocalDateTime());
                }
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
    
    // SIMPLER VERSION: Without courier name if not needed
    public List<OrderHeader> getCustomerOrdersSimple(String customerId) {
        List<OrderHeader> orders = new ArrayList<>();
        
        String sql = "SELECT oh.idOrder, oh.idCustomer, oh.idPromo, oh.status, " +
                     "oh.totalAmount, oh.orderedAt " +
                     "FROM OrderHeader oh " +
                     "WHERE oh.idCustomer = ? " +
                     "ORDER BY oh.orderedAt DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderHeader order = new OrderHeader();
                order.setIdOrder(rs.getString("idOrder"));
                order.setIdCustomer(rs.getString("idCustomer"));
                order.setIdPromo(rs.getString("idPromo"));
                order.setStatus(rs.getString("status"));
                order.setTotalAmount(rs.getDouble("totalAmount"));
                
                Timestamp timestamp = rs.getTimestamp("orderedAt");
                if (timestamp != null) {
                    order.setOrderedAt(timestamp.toLocalDateTime());
                }
                
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting customer orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }
    
    // Get order details by order ID
    public List<OrderDetail> getOrderDetails(String orderId) {
        List<OrderDetail> details = new ArrayList<>();
        
        String sql = "SELECT od.idOrder, od.idProduct, p.name as productName, " +
                     "od.qty, p.price " +
                     "FROM OrderDetail od " +
                     "JOIN Product p ON od.idProduct = p.idProduct " +
                     "WHERE od.idOrder = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setIdOrder(rs.getString("idOrder"));
                detail.setIdProduct(rs.getString("idProduct"));
                detail.setProductName(rs.getString("productName"));
                detail.setQty(rs.getInt("qty"));
                detail.setPrice(rs.getDouble("price"));
                details.add(detail);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting order details: " + e.getMessage());
            e.printStackTrace();
        }
        
        return details;
    }
    
    // Get order by ID
    public OrderHeader getOrderById(String orderId) {
        String sql = "SELECT oh.idOrder, oh.idCustomer, oh.idPromo, oh.status, " +
                     "oh.totalAmount, oh.orderedAt " +
                     "FROM OrderHeader oh " +
                     "WHERE oh.idOrder = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                OrderHeader order = new OrderHeader();
                order.setIdOrder(rs.getString("idOrder"));
                order.setIdCustomer(rs.getString("idCustomer"));
                order.setIdPromo(rs.getString("idPromo"));
                order.setStatus(rs.getString("status"));
                order.setTotalAmount(rs.getDouble("totalAmount"));
                
                Timestamp timestamp = rs.getTimestamp("orderedAt");
                if (timestamp != null) {
                    order.setOrderedAt(timestamp.toLocalDateTime());
                }
                
                return order;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting order: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}