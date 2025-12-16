package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model_entity.Courier;

public class CourierModel {

	// Create courier
	public static Courier createCourier(Courier courier) {
		Connection conn = null;

		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);

			// Generate ID otomatis
			String generatedId = UserModel.generateUserId("courier");
			courier.setIdUser(generatedId);

			// Create user record
			if (!UserModel.createUserRecord(courier)) {
				conn.rollback();
				return null;
			}

			// Create courier record
			String query = "INSERT INTO Courier (idCourier, vehicleType, vehiclePlate) VALUES (?, ?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, courier.getIdUser());
				stmt.setString(2, courier.getVehicleType());
				stmt.setString(3, courier.getVehiclePlate());
				stmt.executeUpdate();
			}

			conn.commit();
			return courier; // Return courier dengan ID yang sudah digenerate

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
				}
			}
			System.err.println("Error creating courier: " + e.getMessage());
			return null;
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	// Get full courier details
	public static Courier getCourierById(String idCourier) {
		String query = "SELECT u.*, c.vehicleType, c.vehiclePlate FROM User u "
				+ "JOIN Courier c ON u.idUser = c.idCourier " + "WHERE u.idUser = ? AND u.role = 'courier'";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, idCourier);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				Courier courier = new Courier();
				courier.setIdUser(rs.getString("idUser"));
				courier.setFullName(rs.getString("fullName"));
				courier.setEmail(rs.getString("email"));
				courier.setPassword(rs.getString("password"));
				courier.setPhone(rs.getString("phone"));
				courier.setAddress(rs.getString("address"));
				courier.setGender(rs.getString("gender"));
				courier.setVehicleType(rs.getString("vehicleType"));
				courier.setVehiclePlate(rs.getString("vehiclePlate"));
				return courier;
			}
		} catch (SQLException e) {
			System.err.println("Error getting courier: " + e.getMessage());
		}
		return null;
	}

	// Get all couriers
	public static List<Courier> getAllCouriers() {
		List<Courier> couriers = new ArrayList<>();
		String query = "SELECT u.*, c.vehicleType, c.vehiclePlate FROM User u "
				+ "JOIN Courier c ON u.idUser = c.idCourier " + "WHERE u.role = 'courier' ORDER BY u.fullName";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Courier courier = new Courier();
				courier.setIdUser(rs.getString("idUser"));
				courier.setFullName(rs.getString("fullName"));
				courier.setEmail(rs.getString("email"));
				courier.setPhone(rs.getString("phone"));
				courier.setAddress(rs.getString("address"));
				courier.setGender(rs.getString("gender"));
				courier.setVehicleType(rs.getString("vehicleType"));
				courier.setVehiclePlate(rs.getString("vehiclePlate"));
				couriers.add(courier);
			}
		} catch (SQLException e) {
			System.err.println("Error getting all couriers: " + e.getMessage());
		}
		return couriers;
	}
}