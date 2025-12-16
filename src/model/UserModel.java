package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import database.DBConnection;
import model_entity.Admin;
import model_entity.Courier;
import model_entity.Customer;
import model_entity.User;

public class UserModel {

	// Generate ID otomatis berdasarkan role dan timestamp
	public static String generateUserId(String role) {
		String prefix = "";

		switch (role.toLowerCase()) {
		case "customer":
			prefix = "CUS";
			break;
		case "admin":
			prefix = "ADM";
			break;
		case "courier":
			prefix = "CRR";
			break;
		default:
			prefix = "USR";
		}

		// Format: PREFIX + YYMMDD + 5 digit random
		String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
		String randomPart = String.format("%05d", (int) (Math.random() * 100000));

		return prefix + datePart + randomPart;
	}

	// Get user by ID
	public static User getUserById(String idUser) {
		String query = "SELECT * FROM User WHERE idUser = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, idUser);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return mapResultSetToUser(rs);
			}
		} catch (SQLException e) {
			System.err.println("Error getting user: " + e.getMessage());
		}
		return null;
	}

	// Get user by email
	public static User getUserByEmail(String email) {
		String query = "SELECT * FROM User WHERE email = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return mapResultSetToUser(rs);
			}
		} catch (SQLException e) {
			System.err.println("Error getting user by email: " + e.getMessage());
		}
		return null;
	}

	// Check if email exists
	public static boolean isEmailExists(String email) {
		return getUserByEmail(email) != null;
	}

	// Update profile
	public static boolean updateProfile(User user) {
		String query = "UPDATE User SET fullName = ?, email = ?, phone = ?, address = ?, gender = ? WHERE idUser = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, user.getFullName());
			stmt.setString(2, user.getEmail());
			stmt.setString(3, user.getPhone());
			stmt.setString(4, user.getAddress());
			stmt.setString(5, user.getGender());
			stmt.setString(6, user.getIdUser());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating profile: " + e.getMessage());
			return false;
		}
	}

	// Change password
	public static boolean changePassword(String idUser, String newPassword) {
		String query = "UPDATE User SET password = ? WHERE idUser = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, newPassword);
			stmt.setString(2, idUser);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error changing password: " + e.getMessage());
			return false;
		}
	}

	// Create base user record
	public static boolean createUserRecord(User user) {
		String query = "INSERT INTO User (idUser, fullName, email, password, phone, address, role, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, user.getIdUser());
			stmt.setString(2, user.getFullName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPassword());
			stmt.setString(5, user.getPhone());
			stmt.setString(6, user.getAddress());
			stmt.setString(7, user.getRole());
			stmt.setString(8, user.getGender());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error creating user record: " + e.getMessage());
			return false;
		}
	}

	// Helper method untuk mapping
	private static User mapResultSetToUser(ResultSet rs) throws SQLException {
		String role = rs.getString("role");
		User user;

		switch (role.toLowerCase()) {
		case "customer":
			user = new Customer();
			break;
		case "admin":
			user = new Admin();
			break;
		case "courier":
			user = new Courier();
			break;
		default:
			user = new User();
		}

		user.setIdUser(rs.getString("idUser"));
		user.setFullName(rs.getString("fullName"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setPhone(rs.getString("phone"));
		user.setAddress(rs.getString("address"));
		user.setRole(role);
		user.setGender(rs.getString("gender"));

		return user;
	}
}