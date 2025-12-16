package model;

import model_entity.Promo;
import database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromoModel {

    public static List<Promo> getAllPromos() {
        List<Promo> promos = new ArrayList<>();
        String query = "SELECT * FROM Promo";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                promos.add(new Promo(
                    rs.getString("idPromo"),
                    rs.getString("code"),
                    rs.getString("headline"),
                    rs.getInt("discountPercentage")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error get promos: " + e.getMessage());
        }
        return promos;
    }

    public static Promo getPromoById(String idPromo) {
        String query = "SELECT * FROM Promo WHERE idPromo = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, idPromo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Promo(
                    rs.getString("idPromo"),
                    rs.getString("code"),
                    rs.getString("headline"),
                    rs.getInt("discountPercentage")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error get promo: " + e.getMessage());
        }
        return null;
    }
}
