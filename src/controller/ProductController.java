
import java.util.List;

import model.ProductModel;
import model_entity.Product;

public class ProductController {
	private static ProductController instance;

	private ProductController() {
	}

	public static ProductController getInstance() {
		if (instance == null) {
			instance = new ProductController();
		}
		return instance;
	}

	public List<Product> getAllProducts() {
		return ProductModel.getAllProducts();
	}

	public Product getProduct(String idProduct) {
		// Validasi input
		if (idProduct == null || idProduct.trim().isEmpty()) {
			return null;
		}

		return ProductModel.getProductById(idProduct);
	}

	public String updateProductStock(String idProduct, int newStock) {
		// Validasi input
		if (newStock < 0) {
			return "Stock cannot be negative";
		}

		if (idProduct == null || idProduct.trim().isEmpty()) {
			return "Invalid product ID";
		}

		// Business logic: cek apakah product ada
		Product product = ProductModel.getProductById(idProduct);
		if (product == null) {
			return "Product not found";
		}

		// Delegasi ke Model
		if (ProductModel.updateProductStock(idProduct, newStock)) {
			return "";
		} else {
			return "Failed to update stock";
		}
	}

	public List<Product> searchProducts(String keyword) {
		// Validasi input
		if (keyword == null || keyword.trim().isEmpty()) {
			return ProductModel.getAllProducts(); // Return semua jika keyword kosong
		}

		return ProductModel.searchProducts(keyword.trim());
	}

	public String addProduct(Product product) {
		// Validasi input
		if (product == null) {
			return "Product cannot be null";
		}

		if (product.getIdProduct() == null || product.getIdProduct().trim().isEmpty()) {
			return "Product ID is required";
		}

		if (product.getName() == null || product.getName().trim().isEmpty()) {
			return "Product name is required";
		}

		if (product.getPrice() <= 0) {
			return "Price must be greater than 0";
		}

		if (product.getStock() < 0) {
			return "Stock cannot be negative";
		}

		// Business logic: cek apakah product ID sudah ada
		Product existingProduct = ProductModel.getProductById(product.getIdProduct());
		if (existingProduct != null) {
			return "Product ID already exists";
		}

		// Delegasi ke Model
		if (ProductModel.addProduct(product)) {
			return "";
		} else {
			return "Failed to add product";
		}
	}

	public String updateProduct(Product product) {
		// Validasi input
		if (product == null) {
			return "Product cannot be null";
		}

		if (product.getIdProduct() == null || product.getIdProduct().trim().isEmpty()) {
			return "Product ID is required";
		}

		if (product.getName() == null || product.getName().trim().isEmpty()) {
			return "Product name is required";
		}

		if (product.getPrice() <= 0) {
			return "Price must be greater than 0";
		}

		if (product.getStock() < 0) {
			return "Stock cannot be negative";
		}

		// Business logic: cek apakah product ada
		Product existingProduct = ProductModel.getProductById(product.getIdProduct());
		if (existingProduct == null) {
			return "Product not found";
		}

		// Delegasi ke Model - PERBAIKAN DI SINI
		if (ProductModel.updateProductDetails(product)) { // Ganti updateProduct dengan updateProductDetails
			return "";
		} else {
			return "Failed to update product";
		}
	}

	public String deleteProduct(String idProduct) {
		// Validasi input
		if (idProduct == null || idProduct.trim().isEmpty()) {
			return "Invalid product ID";
		}

		// Business logic: cek apakah product ada
		Product existingProduct = ProductModel.getProductById(idProduct);
		if (existingProduct == null) {
			return "Product not found";
		}

		// Delegasi ke Model
		if (ProductModel.deleteProduct(idProduct)) {
			return "";
		} else {
			return "Failed to delete product";
		}
	}

	public List<Product> getLowStockProducts(int threshold) {
		// Validasi input
		if (threshold < 0) {
			threshold = 5; // Default threshold
		}

		return ProductModel.getLowStockProducts(threshold);
	}

	public List<String> getAllCategories() {
		return ProductModel.getAllCategories();
	}

	public List<Product> getProductsByCategory(String category) {
		// Validasi input
		if (category == null || category.trim().isEmpty()) {
			return ProductModel.getAllProducts();
		}

		return ProductModel.getProductsByCategory(category.trim());
	}

	// Business logic untuk restock
	public String restockProduct(String idProduct, int additionalStock) {
		if (additionalStock <= 0) {
			return "Additional stock must be greater than 0";
		}

		Product product = ProductModel.getProductById(idProduct);
		if (product == null) {
			return "Product not found";
		}

		// Gunakan method restockProduct dari Model
		if (ProductModel.restockProduct(idProduct, additionalStock)) {
			return "";
		} else {
			return "Failed to restock product";
		}
	}

	// New method: Get product statistics
	public ProductModel.ProductStats getProductStatistics() {
		return ProductModel.getProductStatistics();
	}

	// New method: Generate product ID
	public String generateProductId() {
		return ProductModel.generateProductId();
	}
}