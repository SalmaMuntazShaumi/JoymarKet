package view.Admin;

import controller.AdminProductController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model_entity.Product;

public class AdminProductManagementView {
	private Stage stage;
	private AdminProductController controller;

	private TableView<Product> productTable;
	private TextField searchField;
	private ComboBox<String> categoryFilter;

	public AdminProductManagementView() {
		this.controller = new AdminProductController();
		initializeUI();
		setupLayout();
		loadProducts();
	}

	private void initializeUI() {
		stage = new Stage();
		stage.setTitle("Product Management - Admin");

		productTable = new TableView<>();
		setupTableColumns();

		searchField = new TextField();
		searchField.setPromptText("Search products...");
		searchField.setMinWidth(200);

		categoryFilter = new ComboBox<>();
		categoryFilter.setPromptText("Filter by Category");
		categoryFilter.getItems().add("All Categories");
	}

	private void setupTableColumns() {
		// Product ID Column
		TableColumn<Product, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(cellData -> {
			String id = cellData.getValue().getIdProduct();
			return new javafx.beans.property.SimpleStringProperty(id);
		});
		idCol.setMinWidth(100);

		// Product Name Column
		TableColumn<Product, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(cellData -> {
			String name = cellData.getValue().getName();
			return new javafx.beans.property.SimpleStringProperty(name);
		});
		nameCol.setMinWidth(150);

		// Price Column
		TableColumn<Product, String> priceCol = new TableColumn<>("Price");
		priceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				String.format("Rp%,.0f", cellData.getValue().getPrice())));
		priceCol.setMinWidth(120);

		// Stock Column
		TableColumn<Product, String> stockCol = new TableColumn<>("Stock");
		stockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				String.valueOf(cellData.getValue().getStock())));
		stockCol.setMinWidth(80);

		// Category Column
		TableColumn<Product, String> categoryCol = new TableColumn<>("Category");
		categoryCol.setCellValueFactory(cellData -> {
			String category = cellData.getValue().getCategory();
			return new javafx.beans.property.SimpleStringProperty(category);
		});
		categoryCol.setMinWidth(120);

		// Actions Column - TOMBOL EDIT & DELETE DI SEBELAH PRODUK
		TableColumn<Product, Void> actionsCol = new TableColumn<>("Actions");
		actionsCol.setMinWidth(150);
		actionsCol.setCellFactory(param -> new TableCell<Product, Void>() {
			private final Button editBtn = new Button("Edit");
			private final Button deleteBtn = new Button("Delete");
			private final HBox buttons = new HBox(5, editBtn, deleteBtn);

			{
				buttons.setPadding(new Insets(2, 0, 2, 0));
				editBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 12px;");
				deleteBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 12px;");

				editBtn.setOnAction(event -> {
					Product product = getTableView().getItems().get(getIndex());
					showEditProductDialog(product);
				});

				deleteBtn.setOnAction(event -> {
					Product product = getTableView().getItems().get(getIndex());
					showDeleteConfirmation(product);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : buttons);
			}
		});

		productTable.getColumns().addAll(idCol, nameCol, priceCol, stockCol, categoryCol, actionsCol);
		productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void setupLayout() {
		// Top toolbar
		Button addProductBtn = new Button("+ Add New Product");
		addProductBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
		addProductBtn.setOnAction(e -> showAddProductDialog());

		Button refreshBtn = new Button("Refresh");
		refreshBtn.setOnAction(e -> loadProducts());

		Button viewLowStockBtn = new Button("Low Stock Alert");
		viewLowStockBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
		viewLowStockBtn.setOnAction(e -> showLowStockProducts());

		HBox toolbar = new HBox(10);
		toolbar.setPadding(new Insets(10));
		toolbar.getChildren().addAll(searchField, categoryFilter, addProductBtn, viewLowStockBtn, refreshBtn);

		// Main layout
		VBox root = new VBox(10);
		root.setPadding(new Insets(15));
		root.getChildren().addAll(toolbar, productTable);

		Scene scene = new Scene(root, 1000, 600);
		stage.setScene(scene);
	}

	private void loadProducts() {
		ObservableList<Product> products = FXCollections.observableArrayList(controller.getAllProducts());
		productTable.setItems(products);

		// Update category filter
		categoryFilter.getItems().clear();
		categoryFilter.getItems().add("All Categories");
		categoryFilter.getItems().addAll(controller.getAllCategories());
		categoryFilter.setValue("All Categories");

		// Search functionality
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.trim().isEmpty()) {
				productTable.setItems(products);
			} else {
				ObservableList<Product> filtered = FXCollections.observableArrayList();
				for (Product product : products) {
					if (product.getName().toLowerCase().contains(newValue.toLowerCase())
							|| product.getIdProduct().toLowerCase().contains(newValue.toLowerCase())
							|| product.getCategory().toLowerCase().contains(newValue.toLowerCase())) {
						filtered.add(product);
					}
				}
				productTable.setItems(filtered);
			}
		});

		// Category filter functionality
		categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || "All Categories".equals(newValue)) {
				productTable.setItems(products);
			} else {
				ObservableList<Product> filtered = FXCollections.observableArrayList();
				for (Product product : products) {
					if (product.getCategory().equals(newValue)) {
						filtered.add(product);
					}
				}
				productTable.setItems(filtered);
			}
		});
	}

	private void showAddProductDialog() {
		Stage dialog = new Stage();
		dialog.setTitle("Add New Product");

		// Form fields
		Label titleLabel = new Label("Add New Product");
		titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		TextField nameField = new TextField();
		nameField.setPromptText("Product Name");

		TextField priceField = new TextField();
		priceField.setPromptText("Price (e.g., 100000)");

		TextField stockField = new TextField();
		stockField.setPromptText("Initial Stock");
		stockField.setText("0");

		// Category selection
		ComboBox<String> categoryCombo = new ComboBox<>();
		categoryCombo.getItems().addAll(controller.getAllCategories());
		categoryCombo.setPromptText("Select Category");
		categoryCombo.setEditable(true);

		Button addBtn = new Button("Add Product");
		addBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");

		Button cancelBtn = new Button("Cancel");

		Label messageLabel = new Label();

		addBtn.setOnAction(e -> {
			String result = controller.addNewProduct(nameField.getText(), priceField.getText(), stockField.getText(),
					categoryCombo.getValue());

			if (result.startsWith("SUCCESS")) {
				messageLabel.setStyle("-fx-text-fill: green;");
				String generatedId = result.split("#")[1];
				messageLabel.setText("Product added successfully!\nProduct ID: " + generatedId);

				// Clear fields
				nameField.clear();
				priceField.clear();
				stockField.clear();
				categoryCombo.setValue(null);

				// Refresh table
				loadProducts();
			} else {
				messageLabel.setStyle("-fx-text-fill: red;");
				messageLabel.setText(result);
			}
		});

		cancelBtn.setOnAction(e -> dialog.close());

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		layout.getChildren().addAll(titleLabel, new Label("Product Name:"), nameField, new Label("Price:"), priceField,
				new Label("Initial Stock:"), stockField, new Label("Category:"), categoryCombo,
				new HBox(10, addBtn, cancelBtn), messageLabel);

		dialog.setScene(new Scene(layout, 400, 400));
		dialog.showAndWait();
	}

	private void showEditProductDialog(Product product) {
		Stage dialog = new Stage();
		dialog.setTitle("Edit Product - " + product.getName());

		// Form fields with existing values
		Label titleLabel = new Label("Edit Product: " + product.getIdProduct());
		titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		TextField nameField = new TextField(product.getName());

		TextField priceField = new TextField(String.valueOf(product.getPrice()));

		TextField stockField = new TextField(String.valueOf(product.getStock()));

		ComboBox<String> categoryCombo = new ComboBox<>();
		categoryCombo.getItems().addAll(controller.getAllCategories());
		categoryCombo.setValue(product.getCategory());
		categoryCombo.setEditable(true);

		// Restock section
		Label restockLabel = new Label("Restock (Optional):");
		TextField additionalStockField = new TextField();
		additionalStockField.setPromptText("Additional stock amount");

		Button updateBtn = new Button("Update");
		updateBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white;");

		Button restockBtn = new Button("Restock Only");
		restockBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");

		Button cancelBtn = new Button("Cancel");

		Label messageLabel = new Label();

		updateBtn.setOnAction(e -> {
			String result = controller.editProductDetails(product.getIdProduct(), nameField.getText(),
					priceField.getText(), stockField.getText(), categoryCombo.getValue());

			if ("SUCCESS".equals(result)) {
				messageLabel.setStyle("-fx-text-fill: green;");
				messageLabel.setText("Product updated successfully!");

				// Refresh table
				loadProducts();

				// Close dialog after 1 second
				new Thread(() -> {
					try {
						Thread.sleep(1000);
						javafx.application.Platform.runLater(() -> dialog.close());
					} catch (InterruptedException ex) {
					}
				}).start();
			} else {
				messageLabel.setStyle("-fx-text-fill: red;");
				messageLabel.setText(result);
			}
		});

		restockBtn.setOnAction(e -> {
			String additionalStock = additionalStockField.getText().trim();
			if (additionalStock.isEmpty()) {
				messageLabel.setStyle("-fx-text-fill: red;");
				messageLabel.setText("Please enter additional stock amount");
				return;
			}

			String result = controller.restockProduct(product.getIdProduct(), additionalStock);

			if ("SUCCESS".equals(result)) {
				messageLabel.setStyle("-fx-text-fill: green;");
				messageLabel.setText("Stock added successfully!");

				// Update stock field
				int currentStock = Integer.parseInt(stockField.getText());
				int additional = Integer.parseInt(additionalStock);
				stockField.setText(String.valueOf(currentStock + additional));
				additionalStockField.clear();

				// Refresh table
				loadProducts();
			} else {
				messageLabel.setStyle("-fx-text-fill: red;");
				messageLabel.setText(result);
			}
		});

		cancelBtn.setOnAction(e -> dialog.close());

		HBox buttonBox1 = new HBox(10, updateBtn, restockBtn, cancelBtn);

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));
		layout.getChildren().addAll(titleLabel, new Label("Product Name:"), nameField, new Label("Price:"), priceField,
				new Label("Stock:"), stockField, new Label("Category:"), categoryCombo, restockLabel,
				additionalStockField, buttonBox1, messageLabel);

		dialog.setScene(new Scene(layout, 400, 450));
		dialog.showAndWait();
	}

	private void showDeleteConfirmation(Product product) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Delete Product");
		confirm.setHeaderText("Delete " + product.getName() + "?");
		confirm.setContentText("Product ID: " + product.getIdProduct() + "\n" + "Category: " + product.getCategory()
				+ "\n" + "Price: Rp" + product.getPrice() + "\n" + "Stock: " + product.getStock() + "\n\n"
				+ "Are you sure you want to delete this product?\nThis action cannot be undone.");

		confirm.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				String result = controller.deleteProduct(product.getIdProduct());

				if ("SUCCESS".equals(result)) {
					showAlert("Success", "Product deleted successfully!");
					loadProducts();
				} else {
					showAlert("Error", result);
				}
			}
		});
	}

	private void showLowStockProducts() {
		Stage dialog = new Stage();
		dialog.setTitle("Low Stock Products Alert");

		// Get low stock products (threshold = 10)
		java.util.List<Product> lowStockProducts = controller.getLowStockProducts(10);

		if (lowStockProducts.isEmpty()) {
			Label noProductsLabel = new Label("✅ No low stock products!");
			noProductsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");

			VBox layout = new VBox(10, noProductsLabel);
			layout.setPadding(new Insets(20));
			layout.setAlignment(javafx.geometry.Pos.CENTER);

			dialog.setScene(new Scene(layout, 300, 150));
		} else {
			TableView<Product> lowStockTable = new TableView<>();

			// Setup columns
			TableColumn<Product, String> idCol = new TableColumn<>("ID");
			idCol.setCellValueFactory(
					cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdProduct()));

			TableColumn<Product, String> nameCol = new TableColumn<>("Name");
			nameCol.setCellValueFactory(
					cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

			TableColumn<Product, String> stockCol = new TableColumn<>("Stock");
			stockCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
					String.valueOf(cellData.getValue().getStock())));

			TableColumn<Product, String> actionCol = new TableColumn<>("Action");
			actionCol.setCellFactory(param -> new TableCell<Product, String>() {
				private final Button restockBtn = new Button("Restock");

				{
					restockBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-size: 12px;");
					restockBtn.setOnAction(event -> {
						Product product = getTableView().getItems().get(getIndex());
						dialog.close();
						showEditProductDialog(product); // Open edit dialog for restocking
					});
				}

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setGraphic(empty ? null : restockBtn);
				}
			});

			lowStockTable.getColumns().addAll(idCol, nameCol, stockCol, actionCol);
			lowStockTable.setItems(FXCollections.observableArrayList(lowStockProducts));

			VBox layout = new VBox(10, new Label("⚠️ Products with stock ≤ 10 units:"), lowStockTable);
			layout.setPadding(new Insets(20));

			dialog.setScene(new Scene(layout, 400, 300));
		}

		dialog.show();
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void show() {
		stage.show();
	}
}