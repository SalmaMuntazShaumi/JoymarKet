package controller;

import model.AdminModel;
import model.CourierModel;
import model.CustomerModel;
import model.UserModel;
import model_entity.User;

public class AuthController {

	// Login user
	public User login(String email, String password) {
		if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			return null;
		}

		User user = UserModel.getUserByEmail(email.trim());

		if (user == null || !user.getPassword().equals(password.trim())) {
			return null;
		}

		switch (user.getRole()) {
		case "customer":
			return CustomerModel.getCustomerById(user.getIdUser());
		case "admin":
			return AdminModel.getAdminById(user.getIdUser());
		case "courier":
			return CourierModel.getCourierById(user.getIdUser());
		default:
			return user;
		}
	}

	// Edit profile
	public String editProfile(User user) {
		StringBuilder errorMessage = new StringBuilder();

		if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
			errorMessage.append("Full name is required\n");
		}

		if (!isValidEmail(user.getEmail())) {
			errorMessage.append("Invalid email format\n");
		}

		if (!isValidPhone(user.getPhone())) {
			errorMessage.append("Invalid phone number\n");
		}

		if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
			errorMessage.append("Address is required\n");
		}

		if (!isValidGender(user.getGender())) {
			errorMessage.append("Please select gender (Female or Male)\n");
		}

		if (errorMessage.length() > 0) {
			return errorMessage.toString().trim();
		}

		// Update profile
		if (UserModel.updateProfile(user)) {
			return "SUCCESS";
		} else {
			return "Update failed. Please try again.";
		}
	}

	public User getUserById(String userId) {
		if (userId == null || userId.trim().isEmpty()) {
			return null;
		}

		User user = UserModel.getUserById(userId.trim());

		if (user != null) {
			switch (user.getRole()) {
			case "customer":
				return CustomerModel.getCustomerById(user.getIdUser());
			case "admin":
				return AdminModel.getAdminById(user.getIdUser());
			case "courier":
				return CourierModel.getCourierById(user.getIdUser());
			default:
				return user;
			}
		}
		return null;
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