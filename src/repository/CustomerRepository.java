//package repository;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import model.Customer;
//
//public class CustomerRepository {
//	
//	private static ArrayList<Customer> customers = new ArrayList<>();
//	
//	public void createCustomer(Customer customer) {
//		customers.add(customer);
//	}
//	
//	public List<Customer> getAll() {
//        return customers;
//    }
//	
//	 public Customer findByEmail(String email) {
//	        return customers.stream().filter(c -> c.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
//	 }
//	 
//	 public boolean existsById(String id) {
//	        return customers.stream().anyMatch(c -> c.getId().equals(id));
//	 }
//
//}
