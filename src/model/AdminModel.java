package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model_entity.Admin;

public class AdminModel {

	// Create admin
	public static Admin createAdmin(Admin admin) {
		Connection conn = null;

		try {
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);

			// Generate ID otomatis
			String generatedId = UserModel.generateUserId("admin");
			admin.setIdUser(generatedId);

			// Create user record
			if (!UserModel.createUserRecord(admin)) {
				conn.rollback();
				return null;
			}

			// Create admin record
			String query = "INSERT INTO Admin (idAdmin, emergencyContact) VALUES (?, ?)";
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, admin.getIdUser());
				stmt.setString(2, admin.getEmergencyContact());
				stmt.executeUpdate();
			}

			conn.commit();
			return admin; // Return admin dengan ID yang sudah digenerate

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
				}
			}
			System.err.println("Error creating admin: " + e.getMessage());
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

	// Get full admin details
	public static Admin getAdminById(String idAdmin) {
		String query = "SELECT u.*, a.emergencyContact FROM User u " + "JOIN Admin a ON u.idUser = a.idAdmin "
				+ "WHERE u.idUser = ? AND u.role = 'admin'";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, idAdmin);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				Admin admin = new Admin();
				admin.setIdUser(rs.getString("idUser"));
				admin.setFullName(rs.getString("fullName"));
				admin.setEmail(rs.getString("email"));
				admin.setPassword(rs.getString("password"));
				admin.setPhone(rs.getString("phone"));
				admin.setAddress(rs.getString("address"));
				admin.setGender(rs.getString("gender"));
				admin.setEmergencyContact(rs.getString("emergencyContact"));
				return admin;
			}
		} catch (SQLException e) {
			System.err.println("Error getting admin: " + e.getMessage());
		}
		return null;
	}

	// Get all admins
	public static List<Admin> getAllAdmins() {
		List<Admin> admins = new ArrayList<>();
		String query = "SELECT u.*, a.emergencyContact FROM User u " + "JOIN Admin a ON u.idUser = a.idAdmin "
				+ "WHERE u.role = 'admin' ORDER BY u.fullName";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Admin admin = new Admin();
				admin.setIdUser(rs.getString("idUser"));
				admin.setFullName(rs.getString("fullName"));
				admin.setEmail(rs.getString("email"));
				admin.setPhone(rs.getString("phone"));
				admin.setAddress(rs.getString("address"));
				admin.setGender(rs.getString("gender"));
				admin.setEmergencyContact(rs.getString("emergencyContact"));
				admins.add(admin);
			}
		} catch (SQLException e) {
			System.err.println("Error getting all admins: " + e.getMessage());
		}
		return admins;
	}
}