package service;

import model.Customer;
import repository.CustomerRepository;

public class CustomerService {
	
	private CustomerRepository repo = new CustomerRepository();
	
	public String register(String id, String name, String email, 
            String password, String confirmPassword,
            String phone, String address, String gender) {
		
        if (repo.existsById(id)) return "ID must be unique.";
		
		if (name.isEmpty()) return "Full Name cannot be empty.";	
		
		if (!email.endsWith("@gmail.com")) return "Email must end with @gmail.com.";
	    if (repo.findByEmail(email) != null) return "Email must be unique.";
	    
	    if (password.length() < 6) return "Password must be at least 6 characters.";
        if (!password.equals(confirmPassword)) return "Passwords do not match.";
        
        if (!phone.matches("\\d+")) return "Phone must be numeric.";
        if (phone.length() < 10 || phone.length() > 13) return "Phone must be 10–13 digits.";
        
        if (address.isEmpty()) return "Address must be filled.";

        if (gender.isEmpty()) return "Gender must be chosen.";

        Customer customer = new Customer(id, name, email, password, phone, address, gender);

        repo.createCustomer(customer);

		return "SUCCESS REGISTER";
	}
	
	public Customer login(String email, String password) {
        Customer c = repo.findByEmail(email);
        if (c != null && c.getPassword().equals(password)) {
            return c;
        }
        return null;
    }
	
	public String updateProfile(Customer customer, String name, String phone, String address) {

        if (name.isEmpty()) return "Name must be filled.";
        if (!phone.matches("\\d+")) return "Phone must be numeric.";
        if (phone.length() < 10 || phone.length() > 13) return "Phone must be 10–13 digits.";
        if (address.isEmpty()) return "Address must be filled.";

        customer.setFullName(name);
        customer.setPhone(phone);
        customer.setAddress(address);

        return "SUCCESS";
    }

}
