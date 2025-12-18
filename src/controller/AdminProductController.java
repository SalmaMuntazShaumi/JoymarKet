package controller;

import java.util.List;

import model.ProductModel;
import model_entity.Product;

public class AdminProductController {

	// Add new product
	public String addNewProduct(String name, String priceStr, String stockStr, String category) {
		if (name == null || name.trim().isEmpty()) {
			return "Product name is required";
		}

		double price;
		try {
			price = Double.parseDouble(priceStr.trim());
		} catch (NumberFormatException e) {
			return "Invalid price format";
		}

		if (price <= 0) {
			return "Price must be greater than 0";
		}

		int stock;
		try {
			stock = Integer.parseInt(stockStr.trim());
		} catch (NumberFormatException e) {
			return "Invalid stock format";
		}

		if (stock < 0) {
			return "Stock cannot be negative";
		}

		if (category == null || category.trim().isEmpty()) {
			return "Category is required";
		}

		String generatedId = ProductModel.generateProductId();

		Product product = new Product(generatedId, name.trim(), price, stock, category.trim());

		if (ProductModel.addProduct(product)) {
			return "SUCCESS#" + generatedId;
		} else {
			return "Failed to add product";
		}
	}

	// Edit product stock
	public String editProductStock(String idProduct, String newStockStr) {
		if (idProduct == null || idProduct.trim().isEmpty()) {
			return "Product ID is required";
		}

		int newStock;
		try {
			newStock = Integer.parseInt(newStockStr.trim());
		} catch (NumberFormatException e) {
			return "Invalid stock format";
		}

		if (newStock < 0) {
			return "Stock cannot be negative";
		}

		// Cek apakah product ada
		Product product = ProductModel.getProductById(idProduct);
		if (product == null) {
			return "Product not found";
		}

		// Update stock
		if (ProductModel.updateProductStock(idProduct, newStock)) {
			return "SUCCESS";
		} else {
			return "Failed to update stock";
		}
	}

	// Restock product (tambah stock)
	public String restockProduct(String idProduct, String additionalStockStr) {
		if (idProduct == null || idProduct.trim().isEmpty()) {
			return "Product ID is required";
		}

		int additionalStock;
		try {
			additionalStock = Integer.parseInt(additionalStockStr.trim());
		} catch (NumberFormatException e) {
			return "Invalid stock format";
		}

		if (additionalStock <= 0) {
			return "Additional stock must be greater than 0";
		}

		Product product = ProductModel.getProductById(idProduct);
		if (product == null) {
			return "Product not found";
		}

		// Restock
		if (ProductModel.restockProduct(idProduct, additionalStock)) {
			return "SUCCESS";
		} else {
			return "Failed to restock product";
		}
	}

	// Edit product details (all fields)
	public String editProductDetails(String idProduct, String name, String priceStr, String stockStr, String category) {
		if (idProduct == null || idProduct.trim().isEmpty()) {
			return "Product ID is required";
		}

		if (name == null || name.trim().isEmpty()) {
			return "Product name is required";
		}

		double price;
		try {
			price = Double.parseDouble(priceStr.trim());
		} catch (NumberFormatException e) {
			return "Invalid price format";
		}

		if (price <= 0) {
			return "Price must be greater than 0";
		}

		int stock;
		try {
			stock = Integer.parseInt(stockStr.trim());
		} catch (NumberFormatException e) {
			return "Invalid stock format";
		}

		if (stock < 0) {
			return "Stock cannot be negative";
		}

		if (category == null || category.trim().isEmpty()) {
			return "Category is required";
		}

		Product existingProduct = ProductModel.getProductById(idProduct);
		if (existingProduct == null) {
			return "Product not found";
		}

		// Create updated product
		Product updatedProduct = new Product(idProduct.trim(), name.trim(), price, stock, category.trim());

		// Update product
		if (ProductModel.updateProductDetails(updatedProduct)) {
			return "SUCCESS";
		} else {
			return "Failed to update product";
		}
	}

	// Delete product
	public String deleteProduct(String idProduct) {
		if (idProduct == null || idProduct.trim().isEmpty()) {
			return "Product ID is required";
		}

		Product product = ProductModel.getProductById(idProduct);
		if (product == null) {
			return "Product not found";
		}

		// Cek apakah product masih ada di cart customer
		if (ProductModel.isProductInCart(idProduct)) {
			return "Cannot delete product that is in customer carts";
		}

		// Delete product
		if (ProductModel.deleteProduct(idProduct)) {
			return "SUCCESS";
		} else {
			return "Failed to delete product";
		}
	}

	// Get product by ID
	public Product getProductById(String idProduct) {
		return ProductModel.getProductById(idProduct);
	}

	// Get all products
	public List<Product> getAllProducts() {
		return ProductModel.getAllProducts();
	}

	// Get low stock products
	public List<Product> getLowStockProducts(int threshold) {
		if (threshold < 0) {
			threshold = 5;
		}
		return ProductModel.getLowStockProducts(threshold);
	}

	// Get all categories
	public List<String> getAllCategories() {
		return ProductModel.getAllCategories();
	}
}