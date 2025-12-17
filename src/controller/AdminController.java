package controller;

import java.util.List;

import model.AdminModel;
import model.OrderModel;
import model.UserModel;
import model_entity.Admin;

public class AdminController {

	// Register admin
	public String registerAdmin(String fullName, String email, String password, String confirmPassword, String phone,
			String address, String gender, String emergencyContact) {

		// Validasi input
		StringBuilder errorMessage = new StringBuilder();

		if (fullName == null || fullName.trim().isEmpty()) {
			errorMessage.append("Full name is required\n");
		}

		if (!isValidEmail(email)) {
			errorMessage.append("Invalid email format\n");
		}

		if (!isValidPassword(password)) {
			errorMessage.append("Password must be at least 6 characters\n");
		}

		if (!password.equals(confirmPassword)) {
			errorMessage.append("Passwords do not match\n");
		}

		if (!isValidPhone(phone)) {
			errorMessage.append("Invalid phone number\n");
		}

		if (address == null || address.trim().isEmpty()) {
			errorMessage.append("Address is required\n");
		}

		if (!isValidGender(gender)) {
			errorMessage.append("Please select gender (Female or Male)\n");
		}

		if (emergencyContact == null || emergencyContact.trim().isEmpty()) {
			errorMessage.append("Emergency contact is required\n");
		}

		if (errorMessage.length() > 0) {
			return errorMessage.toString().trim();
		}

		// Check if email already exists
		if (UserModel.isEmailExists(email.trim())) {
			return "Email already registered";
		}

		// Create admin entity TANPA id (akan digenerate otomatis)
		Admin admin = new Admin(null, // ID akan digenerate otomatis
				fullName.trim(), email.trim(), password.trim(), phone.trim(), address.trim(), gender.trim(),
				emergencyContact.trim());

		Admin createdAdmin = AdminModel.createAdmin(admin);

		if (createdAdmin != null) {
			return "SUCCESS#" + createdAdmin.getIdUser(); // Return ID yang digenerate
		} else {
			return "Registration failed. Please try again.";
		}
	}

	// Get admin by ID
	public Admin getAdminById(String adminId) {
		return AdminModel.getAdminById(adminId);
	}

	// Get all admins
	public List<Admin> getAllAdmins() {
		return AdminModel.getAllAdmins();
	}

	// Validation methods
	private boolean isValidEmail(String email) {
		if (email == null) {
			return false;
		}
		String trimmed = email.trim();
		return trimmed.contains("@") && trimmed.contains(".") && trimmed.length() >= 6;
	}

	private boolean isValidPhone(String phone) {
		if (phone == null) {
			return false;
		}
		String trimmed = phone.trim();

		if (trimmed.length() < 10 || trimmed.length() > 15) {
			return false;
		}

		for (int i = 0; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	private boolean isValidPassword(String password) {
		return password != null && password.trim().length() >= 6;
	}

	private boolean isValidGender(String gender) {
		return "Female".equals(gender) || "Male".equals(gender);
	}
}