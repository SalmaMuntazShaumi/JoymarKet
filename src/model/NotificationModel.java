package model;

import database.DBConnection;
import model_entity.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationModel {

    public static List<Notification> getByCustomer(String idCustomer) {
        List<Notification> list = new ArrayList<>();

        String sql =
            "SELECT * FROM notification " +
            "WHERE idCustomer=? " +
            "ORDER BY createdAt DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idCustomer);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Notification(
                        rs.getString("idNotification"),
                        rs.getString("idCustomer"),
                        rs.getString("message"),
                        rs.getBoolean("isRead"),
                        rs.getTimestamp("createdAt").toLocalDateTime()
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void markAsRead(String idNotification) {
        String sql =
            "UPDATE notification SET isRead=TRUE WHERE idNotification=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idNotification);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
