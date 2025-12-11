package view;

import controller.CartController;
import handler.ProductHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.CartItem;
import model.Product;

public class CartView {
    private Stage stage;
    private String customerId;
    private CartController cartController;
    
    private TableView<CartItem> cartTable;
    private Label totalLabel;
    private Button checkoutButton;
    private Button clearCartButton;
    private Button backButton;
    
    public CartView(String customerId) {
        this.customerId = customerId;
        this.cartController = CartController.getInstance();
        initializeUI();
        setupLayout();
        loadCartData();
    }
    
    private void initializeUI() {
        stage = new Stage();
        stage.setTitle("Shopping Cart - Customer: " + customerId);
        
        cartTable = new TableView<>();
        setupTableColumns();
        
        totalLabel = new Label("Total: Rp 0");
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        checkoutButton = new Button("Checkout");
        checkoutButton.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        checkoutButton.setMinWidth(100);
        
        clearCartButton = new Button("Clear Cart");
        clearCartButton.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        clearCartButton.setMinWidth(100);
        
        backButton = new Button("Back");
        backButton.setMinWidth(100);
    }
    
    private void setupTableColumns() {
        // Product ID Column - FIXED: Use lambda instead of PropertyValueFactory
        TableColumn<CartItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getIdProduct();
            return new javafx.beans.property.SimpleStringProperty(id);
        });
        idCol.setMinWidth(80);
        
        // Product Name Column - Already using lambda (good)
        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getProduct().getName());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        nameCol.setMinWidth(150);
        
        // Price Column - Already using lambda (good)
        TableColumn<CartItem, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProduct() != null) {
                double price = cellData.getValue().getProduct().getPrice();
                return new javafx.beans.property.SimpleStringProperty(
                    String.format("Rp%,.0f", price));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        priceCol.setMinWidth(100);
        
        // Quantity Column - FIXED: Use lambda instead of PropertyValueFactory
        TableColumn<CartItem, String> quantityCol = new TableColumn<>("Qty");
        quantityCol.setCellValueFactory(cellData -> {
            int count = cellData.getValue().getCount();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(count));
        });
        quantityCol.setMinWidth(60);
        
        // Total Column - Already using lambda (good)
        TableColumn<CartItem, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("Rp%,.0f", cellData.getValue().getTotalPrice())));
        totalCol.setMinWidth(100);
        
        // Actions Column - Keep as is
        TableColumn<CartItem, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setMinWidth(150);
        actionsCol.setCellFactory(param -> new TableCell<CartItem, Void>() {
            private final Button updateBtn = new Button("Update");
            private final Button removeBtn = new Button("Remove");
            private final HBox buttons = new HBox(5, updateBtn, removeBtn);
            
            {
                buttons.setPadding(new Insets(2, 0, 2, 0));
                updateBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white;");
                removeBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
                
                updateBtn.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    showUpdateCartDialog(item);
                });
                
                removeBtn.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    removeItem(item);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        cartTable.getColumns().addAll(idCol, nameCol, priceCol, quantityCol, totalCol, actionsCol);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void setupLayout() {
        // Toolbar
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.getChildren().addAll(totalLabel, checkoutButton, clearCartButton, backButton);
        
        // Main layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(cartTable, toolbar);
        
        // Event handlers
        setupEventHandlers();
        
        Scene scene = new Scene(root, 700, 400);
        stage.setScene(scene);
    }
    
    private void setupEventHandlers() {
        checkoutButton.setOnAction(e -> handleCheckout());
        clearCartButton.setOnAction(e -> handleClearCart());
        backButton.setOnAction(e -> stage.close());
        
        cartTable.setRowFactory(tv -> {
            TableRow<CartItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showUpdateCartDialog(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void loadCartData() {
        ObservableList<CartItem> items = FXCollections.observableArrayList(
            cartController.getCustomerCart(customerId)
        );
        cartTable.setItems(items);
        updateTotal();
    }
    
    private void updateTotal() {
        double total = cartController.getCartTotal(customerId);
        totalLabel.setText(String.format("Total: Rp%,.0f", total));
    }
    
    private void showUpdateCartDialog(CartItem item) {
        if (item.getProduct() == null) return;
        
        // Use the UpdateCartView class
        UpdateCartView updateView = new UpdateCartView(
            customerId,
            item.getIdProduct(),
            item.getProduct().getName(),
            item.getCount(),
            item.getProduct().getStock()
        );
        updateView.showAndWait();
        loadCartData(); // Refresh after update
    }
    
    private void removeItem(CartItem item) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText("Remove Item");
        confirm.setContentText("Remove " + 
            (item.getProduct() != null ? item.getProduct().getName() : "this item") + 
            " from cart?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String error = cartController.removeFromCart(customerId, item.getIdProduct());
                if (error.isEmpty()) {
                    loadCartData();
                } else {
                    showAlert("Error", error);
                }
            }
        });
    }
    
    private void handleCheckout() {
        if (cartTable.getItems().isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Checkout");
        confirm.setHeaderText("Confirm Purchase");
        confirm.setContentText("Total: Rp" + cartController.getCartTotal(customerId) + 
                              "\n\nProceed with checkout?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Get ProductHandler instance
                ProductHandler productHandler = ProductHandler.getInstance();
                
                // Update stock for each item in cart
                boolean allUpdated = true;
                String errorMessage = "";
                
                for (CartItem item : cartTable.getItems()) {
                    // Get current product
                    Product product = productHandler.getProduct(item.getIdProduct());
                    
                    if (product != null) {
                        // Calculate new stock
                        int newStock = product.getStock() - item.getCount();
                        
                        // Update in database
                        boolean updated = productHandler.updateProductStock(item.getIdProduct(), newStock);
                        if (!updated) {
                            allUpdated = false;
                            errorMessage = "Failed to update stock for " + product.getName();
                            break;
                        }
                    }
                }
                
                if (allUpdated) {
                    // Clear cart after successful stock update
                    cartController.clearCart(customerId);
                    loadCartData(); // Refresh cart
                    showAlert("Success", "Checkout completed! Product stocks updated.");
                } else {
                    showAlert("Error", errorMessage);
                }
            }
        });
    }
    
    private void handleClearCart() {
        if (cartTable.getItems().isEmpty()) {
            showAlert("Empty Cart", "Cart is already empty");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Clear Cart");
        confirm.setHeaderText("Confirm Clear Cart");
        confirm.setContentText("Clear all items from cart?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String error = cartController.clearCart(customerId);
                if (error.isEmpty()) {
                    loadCartData();
                    showAlert("Success", "Cart cleared");
                } else {
                    showAlert("Error", error);
                }
            }
        });
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
    
    public void showAndWait() {
        stage.showAndWait();
    }
}