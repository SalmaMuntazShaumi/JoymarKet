package repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import database.DBConnection;

public class CustomerRepository {
	public boolean topUpBalance(String customerId, double amount) {
	    String sql = "UPDATE Customer SET balance = balance + ? WHERE idCustomer = ?";

	    try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
	        ps.setDouble(1, amount);
	        ps.setString(2, customerId);
	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	
}
