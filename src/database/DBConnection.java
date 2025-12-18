package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	// Parse to SQL
	private static final String URL = "jdbc:mysql://localhost:3306/OOAD";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("MySQL JDBC Driver tidak ditemukan!");
		}
	}

	public static Connection getConnection() {
		try {
			Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

			if (conn == null || conn.isClosed()) {
				System.err.println("Koneksi database gagal!");
				return null;
			}

			System.out.println("Koneksi database berhasil.");
			return conn;

		} catch (SQLException e) {
			System.err.println("Tidak bisa terhubung ke database!");
			System.err.println("Pesan: " + e.getMessage());
			return null;
		}
	}
}
