package controller;

import java.util.List;

import model.CartModel;
import model.ProductModel;
import model_entity.CartItem;
import model_entity.Product;

public class CartController {
	private static CartController instance;

	private CartController() {
	}

	public static CartController getInstance() {
		if (instance == null) {
			instance = new CartController();
		}
		return instance;
	}

	public String addToCart(String idCustomer, String idProduct, int quantity) {
		// Validasi input
		if (quantity <= 0) {
			return "Quantity must be greater than 0";
		}

		Product product = ProductModel.getProductById(idProduct);
		if (product == null) {
			return "Product not found";
		}

		if (quantity > product.getStock()) {
			return "Not enough stock available. Available: " + product.getStock();
		}

		CartItem existingItem = CartModel.getCartItem(idCustomer, idProduct);
		if (existingItem != null) {
			int newQuantity = existingItem.getCount() + quantity;
			if (newQuantity > product.getStock()) {
				int available = product.getStock() - existingItem.getCount();
				return "Can only add " + available + " more items";
			}
		}

		if (!CartModel.addToCart(idCustomer, idProduct, quantity)) {
			return "Failed to add item to cart";
		}

		return "";
	}

	// Update Cart
	public String updateCartItem(String idCustomer, String idProduct, int newQuantity) {
		if (newQuantity <= 0) {
			return "Quantity must be greater than 0";
		}

		Product product = ProductModel.getProductById(idProduct);
		if (product == null) {
			return "Product not found";
		}

		if (newQuantity > product.getStock()) {
			return "Only " + product.getStock() + " items available in stock";
		}

		CartItem existingItem = CartModel.getCartItem(idCustomer, idProduct);
		if (existingItem == null) {
			return "Item not found in cart";
		}

		if (!CartModel.updateCartItem(idCustomer, idProduct, newQuantity)) {
			return "Failed to update cart item";
		}

		return "";
	}

	//Remove Cart
	public String removeFromCart(String idCustomer, String idProduct) {
		CartItem existingItem = CartModel.getCartItem(idCustomer, idProduct);
		if (existingItem == null) {
			return "Item not found in cart";
		}

		if (!CartModel.deleteCartItem(idCustomer, idProduct)) {
			return "Failed to remove item from cart";
		}

		return "";
	}

	public List<CartItem> getCustomerCart(String idCustomer) {
		return CartModel.getCartItemsByCustomer(idCustomer);
	}

	public double getCartTotal(String idCustomer) {
		return CartModel.calculateCartTotal(idCustomer);
	}

	public String clearCart(String idCustomer) {
		if (!CartModel.clearCustomerCart(idCustomer)) {
			return "Failed to clear cart";
		}
		return "";
	}

	public int getCartItemCount(String idCustomer) {
		return CartModel.getCartItemCount(idCustomer);
	}

	public boolean checkoutCart(String idCustomer) {
		List<CartItem> cartItems = CartModel.getCartItemsByCustomer(idCustomer);

		// 1. Validasi semua item masih tersedia
		for (CartItem item : cartItems) {
			Product product = ProductModel.getProductById(item.getIdProduct());
			if (product == null || product.getStock() < item.getCount()) {
				return false; // Item tidak tersedia
			}
		}

		// 2. Update stock produk
		for (CartItem item : cartItems) {
			Product product = ProductModel.getProductById(item.getIdProduct());
			int newStock = product.getStock() - item.getCount();
			ProductModel.updateProductStock(item.getIdProduct(), newStock);
		}

		// 3. Clear cart
		CartModel.clearCustomerCart(idCustomer);

		return true;
	}
}