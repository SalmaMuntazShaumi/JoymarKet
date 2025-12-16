package model;

import model_entity.CartItem;
import model_entity.Promo;
import database.DBConnection;
import java.sql.*;
import java.util.List;

public class OrderModel {

  public static String generateOrderId() {
    return "ORD" + System.currentTimeMillis();
  }

  // ===============================
  // OLD METHOD (KEEP)
  // ===============================
  public static String createOrder(String idCustomer, String idPromo) {
    double total = CartModel.calculateCartTotal(idCustomer);
    return createOrderInternal(idCustomer, idPromo, total);
  }

  // ===============================
  // NEW METHOD (SELECTED ITEMS)
  // ===============================
  public static String createOrder(
    String idCustomer,
    String idPromo,
    List < CartItem > items
  ) {
    double total = 0;
    for (CartItem item: items) {
      total += item.getTotalPrice();
    }

    if (idPromo != null) {
      Promo promo = PromoModel.getPromoById(idPromo);
      if (promo != null) {
        total -= total * promo.getDiscountPercentage() / 100.0;
      }
    }

    return createOrderInternal(idCustomer, idPromo, total);
  }

  // ===============================
  // SHARED LOGIC
  // ===============================
  private static String createOrderInternal(
    String idCustomer,
    String idPromo,
    double total
  ) {
    String idOrder = generateOrderId();

    String query =
      "INSERT INTO orderheader " +
      "(idOrder, idCustomer, idPromo, status, totalAmount, orderedAt) " +
      "VALUES (?, ?, ?, 'PENDING', ?, NOW())";

    try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, idOrder);
      stmt.setString(2, idCustomer);
      stmt.setString(3, idPromo);
      stmt.setDouble(4, total);
      stmt.executeUpdate();
      return idOrder;

    } catch (SQLException e) {
      System.err.println("Error create order: " + e.getMessage());
      return null;
    }
  }

  // ===============================
  // OLD DETAIL (KEEP)
  // ===============================
  public static boolean createOrderDetails(String idOrder, String idCustomer) {
    List < CartItem > cartItems = CartModel.getCartItemsByCustomer(idCustomer);
    return createOrderDetails(idOrder, cartItems);
  }

  // ===============================
  // NEW DETAIL (SELECTED ITEMS)
  // ===============================
  public static boolean createOrderDetails(
    String idOrder,
    List < CartItem > items
  ) {
    String query =
      "INSERT INTO orderdetail (idOrder, idProduct, qty) " +
      "VALUES (?, ?, ?)";

    try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

      for (CartItem item: items) {
        stmt.setString(1, idOrder);
        stmt.setString(2, item.getIdProduct());
        stmt.setInt(3, item.getCount());
        stmt.addBatch();
      }
      stmt.executeBatch();
      return true;

    } catch (SQLException e) {
      System.err.println("Error create order detail: " + e.getMessage());
      return false;
    }
  }

  // ===============================
  // PAY ORDER (SELECTED ITEMS)
  // ===============================
  public static boolean payOrder(
	        String idOrder,
	        String idCustomer,
	        double total,
	        List<CartItem> items
	) {
	    String updateOrder = "UPDATE orderheader SET status = 'PAID' WHERE idOrder = ?";

	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false);

	        // 1️⃣ VALIDASI BALANCE
	        double balance = CustomerModel.getCustomerBalance(idCustomer);
	        if (balance < total) {
	            conn.rollback();
	            System.err.println("Balance tidak mencukupi");
	            return false;
	        }

	        // 2️⃣ POTONG BALANCE
	        String deductBalance =
	                "UPDATE Customer SET balance = balance - ? WHERE idCustomer = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(deductBalance)) {
	            stmt.setDouble(1, total);
	            stmt.setString(2, idCustomer);
	            stmt.executeUpdate();
	        }

	        // 3️⃣ KURANGI STOK & HAPUS CART
	        for (CartItem item : items) {
	            ProductModel.decreaseProductStock(
	                    item.getIdProduct(),
	                    item.getCount()
	            );

	            CartModel.removeFromCart(
	                    idCustomer,
	                    item.getIdProduct()
	            );
	        }

	        // 4️⃣ UPDATE STATUS ORDER
	        try (PreparedStatement stmt = conn.prepareStatement(updateOrder)) {
	            stmt.setString(1, idOrder);
	            stmt.executeUpdate();
	        }

	        conn.commit();
	        return true;

	    } catch (SQLException e) {
	        System.err.println("Error paying order: " + e.getMessage());
	        return false;
	    }
	}

}