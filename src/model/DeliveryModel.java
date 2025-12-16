package model;

import database.DBConnection;
import model_entity.Delivery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryModel {

    // ================= CREATE =================
    public static void createDelivery(String idOrder) throws SQLException {
        String sql = "INSERT INTO delivery (idOrder, status) VALUES (?, 'waiting')";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, idOrder);
            ps.executeUpdate();
        }
    }

    // ================= ASSIGN COURIER =================
    public static boolean assignCourier(String idOrder, String courierId) {
        String sql =
            "UPDATE delivery SET idCourier=?, status='assigned' " +
            "WHERE idOrder=? AND status='waiting'";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, courierId);
            ps.setString(2, idOrder);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= COURIER ACCEPT =================
    public static boolean courierAccept(String idOrder, String courierId) {
        String sql =
            "UPDATE delivery SET status='onDelivery' " +
            "WHERE idOrder=? AND idCourier=? AND status='assigned'";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, idOrder);
            ps.setString(2, courierId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= COMPLETE =================
    public static boolean completeDelivery(String idOrder) {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement ps =
                c.prepareStatement(
                    "UPDATE delivery SET status='completed' WHERE idOrder=?")) {
                ps.setString(1, idOrder);
                ps.executeUpdate();
            }

            try (PreparedStatement ps =
                c.prepareStatement(
                    "UPDATE orderheader SET status='delivered' WHERE idOrder=?")) {
                ps.setString(1, idOrder);
                ps.executeUpdate();
            }

            c.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= DELIVERY MILIK COURIER =================
    public static List<Delivery> getCourierDeliveries(String courierId, String status) {
        List<Delivery> list = new ArrayList<>();

        String sql =
            "SELECT d.idOrder, d.status, u.fullName, u.address " +
            "FROM delivery d " +
            "JOIN orderheader o ON d.idOrder=o.idOrder " +
            "JOIN customer c ON o.idCustomer=c.idCustomer " +
            "JOIN user u ON c.idCustomer=u.idUser " +
            "WHERE d.idCourier=? ";

        if (status != null) {
            sql += "AND d.status=?";
        }

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, courierId);
            if (status != null) ps.setString(2, status);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Delivery(
                    rs.getString("idOrder"),
                    rs.getString("status"),
                    rs.getString("fullName"),
                    rs.getString("address")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= AVAILABLE DELIVERY =================
    public static List<Delivery> getAvailableDeliveries() {
        List<Delivery> list = new ArrayList<>();

        String sql =
            "SELECT d.idOrder, d.status, u.fullName, u.address " +
            "FROM delivery d " +
            "JOIN orderheader o ON d.idOrder=o.idOrder " +
            "JOIN customer c ON o.idCustomer=c.idCustomer " +
            "JOIN user u ON c.idCustomer=u.idUser " +
            "WHERE d.status='waiting' AND d.idCourier IS NULL";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Delivery(
                    rs.getString("idOrder"),
                    rs.getString("status"),
                    rs.getString("fullName"),
                    rs.getString("address")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
