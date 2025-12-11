package service;

import handler.UserHandler;
import model.*;
import java.util.UUID;

public class UserService {
    private UserHandler userHandler;
    
    public UserService() {
        this.userHandler = UserHandler.getInstance();
    }
    
    public User login(String email, String password) {
        if (email == null || email.trim().length() == 0) {
            return null;
        }
        
        if (password == null || password.trim().length() == 0) {
            return null;
        }
        
        return userHandler.login(email.trim(), password.trim());
    }
    
    public String registerCustomer(String idUser, String fullName, String email, 
            String password, String confirmPassword,
            String phone, String address, String gender) {

			StringBuilder errorMessage = new StringBuilder();
			
			// Validation
			if (idUser == null || idUser.trim().length() == 0) {
				errorMessage.append("User ID is required\n");
			}
			
			if (fullName == null || fullName.trim().length() == 0) {
				errorMessage.append("Full name is required\n");
			}
			
			if (email == null || email.trim().length() == 0) {
				errorMessage.append("Email is required\n");
			} else {
				String trimmedEmail = email.trim();
				if (trimmedEmail.indexOf("@") == -1 || trimmedEmail.indexOf(".") == -1 || trimmedEmail.length() < 6) {
					errorMessage.append("Invalid email format\n");
				}
			}
			
			if (password == null || password.trim().length() == 0) {
				errorMessage.append("Password is required\n");
			} else if (password.trim().length() < 6) {
				errorMessage.append("Password must be at least 6 characters\n");
			}
			
			if (!password.equals(confirmPassword)) {
				errorMessage.append("Passwords do not match\n");
			}
			
			if (phone == null || phone.trim().length() == 0) {
				errorMessage.append("Phone is required\n");
			} else {
				String trimmedPhone = phone.trim();
				boolean validPhone = true;
				if (trimmedPhone.length() < 10 || trimmedPhone.length() > 15) {
				validPhone = false;
				} else {
					for (int i = 0; i < trimmedPhone.length(); i++) {
						char c = trimmedPhone.charAt(i);
						if (c < '0' || c > '9') {
						  validPhone = false;
						  break;
						}
				}
			}
				if (!validPhone) {
					errorMessage.append("Invalid phone number\n");
				}
			}
			
			if (address == null || address.trim().length() == 0) {
				errorMessage.append("Address is required\n");
			}
			
			if (gender == null || (!gender.equals("Female") && !gender.equals("Male"))) {
				errorMessage.append("Please select gender (Female or Male)\n");
			}
			
			if (errorMessage.length() > 0) {
				return errorMessage.toString().trim();
			}
			
			// Check if email already exists
			User existing = userHandler.getUserByEmail(email.trim());
			if (existing != null) {
				return "Email already registered";
			}
			
			// Create customer object with gender
			Customer customer = new Customer(
				idUser.trim(),
				fullName.trim(),
				email.trim(),
				password.trim(),
				phone.trim(),
				address.trim(),
				gender
			);
			
			// Save to database
			boolean success = userHandler.registerCustomer(customer);
			
			if (success) {
				return "SUCCESS";
			} else {
				return "Registration failed. Please try again.";
			}
	}
    
    public String registerCourier(String idUser, String fullName, String email, 
            String password, String confirmPassword,
            String phone, String address, String gender,
            String vehicleType, String vehiclePlate) {

			StringBuilder errorMessage = new StringBuilder();
			
			// Validation (same as customer plus gender)
			if (idUser == null || idUser.trim().length() == 0) {
				errorMessage.append("User ID is required\n");
			}
			
			if (fullName == null || fullName.trim().length() == 0) {
				errorMessage.append("Full name is required\n");
			}
			
			if (email == null || email.trim().length() == 0) {
				errorMessage.append("Email is required\n");
			} else {
				String trimmedEmail = email.trim();
				if (trimmedEmail.indexOf("@") == -1 || trimmedEmail.indexOf(".") == -1 || trimmedEmail.length() < 6) {
					errorMessage.append("Invalid email format\n");
				}
			}
			
			if (password == null || password.trim().length() == 0) {
				errorMessage.append("Password is required\n");
			} else if (password.trim().length() < 6) {
				errorMessage.append("Password must be at least 6 characters\n");
			}
			
			if (!password.equals(confirmPassword)) {
				errorMessage.append("Passwords do not match\n");
			}
			
			if (phone == null || phone.trim().length() == 0) {
				errorMessage.append("Phone is required\n");
			} else {
				String trimmedPhone = phone.trim();
				boolean validPhone = true;
				if (trimmedPhone.length() < 10 || trimmedPhone.length() > 15) {
				validPhone = false;
			} else {
				for (int i = 0; i < trimmedPhone.length(); i++) {
					char c = trimmedPhone.charAt(i);
					if (c < '0' || c > '9') {
					   validPhone = false;
					   break;
					}
				}
			}
				if (!validPhone) {
					errorMessage.append("Invalid phone number\n");
				}
			}
			
			if (address == null || address.trim().length() == 0) {
				errorMessage.append("Address is required\n");
			}
			
			if (gender == null || (!gender.equals("Female") && !gender.equals("Male"))) {
				errorMessage.append("Please select gender (Female or Male)\n");
			}
			
			if (vehicleType == null || vehicleType.trim().length() == 0) {
				errorMessage.append("Vehicle type is required\n");
			}
			
			if (vehiclePlate == null || vehiclePlate.trim().length() == 0) {
				errorMessage.append("Vehicle plate is required\n");
			}
			
			if (errorMessage.length() > 0) {
				return errorMessage.toString().trim();
			}
			
			// Check if email already exists
			User existing = userHandler.getUserByEmail(email.trim());
			if (existing != null) {
				return "Email already registered";
			}
			
			// Create courier object with gender
			Courier courier = new Courier(
				idUser.trim(),
				fullName.trim(),
				email.trim(),
				password.trim(),
				phone.trim(),
				address.trim(),
				gender.trim(),
				vehicleType.trim(),
				vehiclePlate.trim()
			);
			
			// Save to database
			boolean success = userHandler.registerCourier(courier);
			
			if (success) {
				return "SUCCESS";
			} else {
				return "Registration failed. Please try again.";
			}
	}
    
    public String registerAdmin(String idUser, String fullName, String email, 
            String password, String confirmPassword,
            String phone, String address, String gender,
            String emergencyContact) {

			StringBuilder errorMessage = new StringBuilder();
			
			// Validation (same as customer plus gender)
			if (idUser == null || idUser.trim().length() == 0) {
				errorMessage.append("User ID is required\n");
			}
			
			if (fullName == null || fullName.trim().length() == 0) {
				errorMessage.append("Full name is required\n");
			}
			
			if (email == null || email.trim().length() == 0) {
				errorMessage.append("Email is required\n");
			} else {
				String trimmedEmail = email.trim();
				if (trimmedEmail.indexOf("@") == -1 || trimmedEmail.indexOf(".") == -1 || trimmedEmail.length() < 6) {
					errorMessage.append("Invalid email format\n");
				}
			}
			
			if (password == null || password.trim().length() == 0) {
				errorMessage.append("Password is required\n");
			} else if (password.trim().length() < 6) {
				errorMessage.append("Password must be at least 6 characters\n");
			}
			
			if (!password.equals(confirmPassword)) {
				errorMessage.append("Passwords do not match\n");
			}
			
			if (phone == null || phone.trim().length() == 0) {
				errorMessage.append("Phone is required\n");
			} else {
				String trimmedPhone = phone.trim();
				boolean validPhone = true;
				if (trimmedPhone.length() < 10 || trimmedPhone.length() > 15) {
				validPhone = false;
			} else {
				for (int i = 0; i < trimmedPhone.length(); i++) {
					 char c = trimmedPhone.charAt(i);
					 if (c < '0' || c > '9') {
					     validPhone = false;
					     break;
					 }
				}
			}
				
				if (!validPhone) {
					errorMessage.append("Invalid phone number\n");
				}
			}
			
			if (address == null || address.trim().length() == 0) {
				errorMessage.append("Address is required\n");
			}
			
			if (gender == null || (!gender.equals("Female") && !gender.equals("Male"))) {
				errorMessage.append("Please select gender (Female or Male)\n");
			}
			
			if (emergencyContact == null || emergencyContact.trim().length() == 0) {
				errorMessage.append("Emergency contact is required\n");
			}
			
			if (errorMessage.length() > 0) {
				return errorMessage.toString().trim();
			}
			
			// Check if email already exists
			User existing = userHandler.getUserByEmail(email.trim());
			if (existing != null) {
				return "Email already registered";
			}
			
			// Create admin object with gender
			Admin admin = new Admin(
				idUser.trim(),
				fullName.trim(),
				email.trim(),
				password.trim(),
				phone.trim(),
				address.trim(),
				gender.trim(),
				emergencyContact.trim()
			);
			
			// Save to database
			boolean success = userHandler.registerAdmin(admin);
			
			if (success) {
				return "SUCCESS";
			} else {
				return "Registration failed. Please try again.";
			}
	}   
    
    public String editProfile(User user) {
        StringBuilder errorMessage = new StringBuilder();
        
        if (user.getFullName() == null || user.getFullName().trim().length() == 0) {
            errorMessage.append("Full name is required\n");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().length() == 0) {
            errorMessage.append("Email is required\n");
        } else {
            String trimmedEmail = user.getEmail().trim();
            if (trimmedEmail.indexOf("@") == -1 || trimmedEmail.indexOf(".") == -1 || trimmedEmail.length() < 6) {
                errorMessage.append("Invalid email format\n");
            }
        }
        
        if (user.getPhone() == null || user.getPhone().trim().length() == 0) {
            errorMessage.append("Phone is required\n");
        } else {
            String trimmedPhone = user.getPhone().trim();
            boolean validPhone = true;
            if (trimmedPhone.length() < 10 || trimmedPhone.length() > 15) {
                validPhone = false;
            } else {
                for (int i = 0; i < trimmedPhone.length(); i++) {
                    char c = trimmedPhone.charAt(i);
                    if (c < '0' || c > '9') {
                        validPhone = false;
                        break;
                    }
                }
            }
            if (!validPhone) {
                errorMessage.append("Invalid phone number\n");
            }
        }
        
        if (user.getAddress() == null || user.getAddress().trim().length() == 0) {
            errorMessage.append("Address is required\n");
        }
        
        if (user.getGender() == null || (!user.getGender().equals("Female") && !user.getGender().equals("Male"))) {
            errorMessage.append("Please select gender (Female or Male)\n");
        }
        
        if (errorMessage.length() > 0) {
            return errorMessage.toString().trim();
        }
        
        boolean success = userHandler.editProfile(user);
        
        if (success) {
            return "SUCCESS";
        } else {
            return "Update failed. Please try again.";
        }
    }
    
    public User getUserById(String userId) {
        System.out.println("UserService.getUserById called with: " + userId);
        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("UserService.getUserById: userId is null or empty");
            return null;
        }
        User user = userHandler.getUser(userId.trim());
        System.out.println("UserService.getUserById: found user = " + user);
        if (user != null) {
            System.out.println("UserService.getUserById: user details - " +
                              "ID: " + user.getIdUser() + ", " +
                              "Name: " + user.getFullName() + ", " +
                              "Email: " + user.getEmail());
        }
        return user;
    }
    
    public String changePassword(String userId, String oldPassword, 
                                String newPassword, String confirmPassword) {
        
        if (oldPassword == null || oldPassword.trim().length() == 0) {
            return "Current password is required";
        }
        
        if (newPassword == null || newPassword.trim().length() == 0) {
            return "New password is required";
        }
        
        if (newPassword.trim().length() < 6) {
            return "New password must be at least 6 characters";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            return "New passwords do not match";
        }
        
        // Verify old password
        User user = userHandler.getUser(userId);
        if (user == null || !user.getPassword().equals(oldPassword.trim())) {
            return "Current password is incorrect";
        }
        
        boolean success = userHandler.changePassword(userId, newPassword.trim());
        
        if (success) {
            return "SUCCESS";
        } else {
            return "Password change failed";
        }
    }
    
    public String topUpBalance(String customerId, double amount) {
        if (amount <= 0) {
            return "Amount must be greater than 0";
        }
        
        boolean success = userHandler.topUpBalance(customerId, amount);
        
        if (success) {
            return "SUCCESS";
        } else {
            return "Top-up failed";
        }
    }
}