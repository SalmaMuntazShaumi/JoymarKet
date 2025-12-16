package model;

import database.DBConnection;
import java.sql.*;

public class NotificationModel {

    public static void send(String idCustomer, String message) throws SQLException {
        String sql = "INSERT INTO notification(idCustomer, message, createdAt) VALUES (?, ?, NOW()) ";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idCustomer);
            stmt.setString(2, message);
            stmt.executeUpdate();
        }
    }
}
