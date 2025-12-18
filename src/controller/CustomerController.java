package controller;

import java.util.List;

import model.CustomerModel;
import model.UserModel;
import model_entity.Customer;

public class CustomerController {

	// Register customer
	public String registerCustomer(String fullName, String email, String password, String confirmPassword, String phone,
			String address, String gender) {

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

		if (errorMessage.length() > 0) {
			return errorMessage.toString().trim();
		}

		if (UserModel.isEmailExists(email.trim())) {
			return "Email already registered";
		}

		Customer customer = new Customer(null,
				fullName.trim(), email.trim(), password.trim(), phone.trim(), address.trim(), gender);

		Customer createdCustomer = CustomerModel.createCustomer(customer);

		if (createdCustomer != null) {
			return "SUCCESS#" + createdCustomer.getIdUser();
		} else {
			return "Registration failed. Please try again.";
		}
	}

	// Top-up balance
	public String topUpBalance(String customerId, String amountStr) {
		if (amountStr == null || amountStr.trim().isEmpty()) {
			return "Amount is required";
		}

		double amount;
		try {
			amount = Double.parseDouble(amountStr.trim());
		} catch (NumberFormatException e) {
			return "Invalid amount format";
		}

		if (amount <= 0) {
			return "Amount must be greater than 0";
		}

		if (amount < 10000) {
			return "Minimum top-up amount is Rp 10,000";
		}

		if (CustomerModel.topUpBalance(customerId, amount)) {
			return "SUCCESS";
		} else {
			return "Top-up failed";
		}
	}

	// Get customer by ID
	public Customer getCustomerById(String customerId) {
		return CustomerModel.getCustomerById(customerId);
	}

	// Get customer balance
	public double getCustomerBalance(String customerId) {
		return CustomerModel.getCustomerBalance(customerId);
	}

	// Get all customers
	public List<Customer> getAllCustomers() {
		return CustomerModel.getAllCustomers();
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