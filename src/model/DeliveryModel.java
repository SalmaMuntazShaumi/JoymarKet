package model;

import database.DBConnection;
import java.sql.*;

public class DeliveryModel {

    public static void createDelivery(String idOrder) throws SQLException {
        String sql = "INSERT INTO delivery (idOrder, status) VALUES (?, 'WAITING')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idOrder);
            stmt.executeUpdate();
        }
    }

    public static void updateStatus(String idOrder, String status) throws SQLException {
        String sql = "UPDATE delivery SET status = ? WHERE idOrder = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, idOrder);
            stmt.executeUpdate();
        }
    }
}
