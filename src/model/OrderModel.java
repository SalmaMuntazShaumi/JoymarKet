package model;

import model_entity.CartItem;
import model_entity.OrderHeader;
import model_entity.Promo;
import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    // ===============================
    // GENERATE ORDER ID
    // ===============================
    public static String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    // ===============================
    // CREATE ORDER
    // ===============================
    public static String createOrder(
            String idCustomer,
            String idPromo,
            List<CartItem> items
    ) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }

        if (idPromo != null) {
            Promo promo = PromoModel.getPromoById(idPromo);
            if (promo != null) {
                total -= total * promo.getDiscountPercentage() / 100.0;
            }
        }

        return createOrderInternal(idCustomer, idPromo, total);
    }

    private static String createOrderInternal(
            String idCustomer,
            String idPromo,
            double total
    ) {
        String idOrder = generateOrderId();

        String sql =
                "INSERT INTO orderheader " +
                "(idOrder, idCustomer, idPromo, status, totalAmount, orderedAt) " +
                "VALUES (?, ?, ?, 'PENDING', ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idOrder);
            ps.setString(2, idCustomer);
            ps.setString(3, idPromo);
            ps.setDouble(4, total);
            ps.executeUpdate();

            return idOrder;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===============================
    // ORDER DETAILS
    // ===============================
    public static boolean createOrderDetails(
            String idOrder,
            List<CartItem> items
    ) {
        String sql =
                "INSERT INTO orderdetail (idOrder, idProduct, qty) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (CartItem item : items) {
                ps.setString(1, idOrder);
                ps.setString(2, item.getIdProduct());
                ps.setInt(3, item.getCount());
                ps.addBatch();
            }

            ps.executeBatch();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================
    // PAY ORDER
    // ===============================
    public static boolean payOrder(
            String idOrder,
            String idCustomer,
            double total,
            List<CartItem> items
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            double balance = CustomerModel.getCustomerBalance(idCustomer);
            if (balance < total) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement ps =
                         conn.prepareStatement(
                                 "UPDATE customer SET balance = balance - ? WHERE idCustomer = ?")) {
                ps.setDouble(1, total);
                ps.setString(2, idCustomer);
                ps.executeUpdate();
            }

            for (CartItem item : items) {
                ProductModel.decreaseProductStock(
                        item.getIdProduct(),
                        item.getCount()
                );
                CartModel.removeFromCart(
                        idCustomer,
                        item.getIdProduct()
                );
            }

            try (PreparedStatement ps =
                         conn.prepareStatement(
                                 "UPDATE orderheader SET status='PAID' WHERE idOrder=?")) {
                ps.setString(1, idOrder);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================
    // ADMIN ACCEPT ORDER
    // ===============================
    public static boolean updateOrderStatus(String idOrder, String newStatus) {
        if ("PROCESSING".equals(newStatus)) {
            return adminAcceptOrder(idOrder);
        }
        if ("CANCELLED".equals(newStatus)) {
            return adminCancelOrder(idOrder);
        }
        return false;
    }

    public static boolean adminAcceptOrder(String idOrder) {
        String updateOrder =
                "UPDATE orderheader SET status='PROCESSING' WHERE idOrder=?";
        String insertDelivery =
                "INSERT INTO delivery (idOrder, status) VALUES (?, 'WAITING')";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // update order
            try (PreparedStatement ps = conn.prepareStatement(updateOrder)) {
                ps.setString(1, idOrder);
                ps.executeUpdate();
            }

            // create delivery
            try (PreparedStatement ps = conn.prepareStatement(insertDelivery)) {
                ps.setString(1, idOrder);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================
    // ADMIN CANCEL ORDER
    // ===============================
    public static boolean adminCancelOrder(String idOrder) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // ambil order
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT idCustomer, totalAmount FROM orderheader WHERE idOrder=?"
            );
            ps.setString(1, idOrder);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            String customerId = rs.getString("idCustomer");
            double total = rs.getDouble("totalAmount");

            // refund
            PreparedStatement refund = conn.prepareStatement(
                    "UPDATE customer SET balance = balance + ? WHERE idCustomer=?"
            );
            refund.setDouble(1, total);
            refund.setString(2, customerId);
            refund.executeUpdate();

            // update order
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE orderheader SET status='CANCELLED' WHERE idOrder=?"
            );
            update.setString(1, idOrder);
            update.executeUpdate();

            // update delivery
            PreparedStatement deliv = conn.prepareStatement(
                    "UPDATE delivery SET status='CANCELLED' WHERE idOrder=?"
            );
            deliv.setString(1, idOrder);
            deliv.executeUpdate();

            // notification
            PreparedStatement notif = conn.prepareStatement(
                    "INSERT INTO notification (idCustomer, message, createdAt) VALUES (?, ?, NOW())"
            );
            notif.setString(1, customerId);
            notif.setString(2,
                    "Pesanan " + idOrder + " dibatalkan. Dana telah direfund.");
            notif.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================
    // GET PAID ORDERS
    // ===============================
    public static List<OrderHeader> getOrdersByStatus(String status) {
        List<OrderHeader> list = new ArrayList<>();

        String sql = "SELECT * FROM orderheader WHERE status=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderHeader o = new OrderHeader(
                        rs.getString("idOrder"),
                        rs.getString("idCustomer"),
                        rs.getString("idPromo"),
                        rs.getString("status"),
                        rs.getDouble("totalAmount"),
                        rs.getTimestamp("orderedAt").toLocalDateTime()
                );
                list.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
