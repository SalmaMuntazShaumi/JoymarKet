package view.Customer;

import controller.CartController;
import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ProductModel;
import model_entity.CartItem;
import model_entity.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CartView {

    private Stage stage;
    private String customerId;
    private CartController cartController;

    private TableView<CartItem> cartTable;
    private Label totalLabel;
    private CheckBox selectAllCheckBox;

    // CALLBACK
    private Runnable onCheckoutSuccess;

    // ===== CONSTRUCTOR =====
    public CartView(String customerId, Runnable onCheckoutSuccess) {
        this.customerId = customerId;
        this.onCheckoutSuccess = onCheckoutSuccess;
        this.cartController = CartController.getInstance();

        initUI();
        setupLayout();
        loadData();
    }

    // OPTIONAL backward compatibility
    public CartView(String customerId) {
        this(customerId, null);
    }

    private void initUI() {
        stage = new Stage();
        stage.setTitle("Shopping Cart");

        cartTable = new TableView<>();
        cartTable.setEditable(true);

        selectAllCheckBox = new CheckBox("Select All");
        selectAllCheckBox.setOnAction(e -> {
            boolean checked = selectAllCheckBox.isSelected();
            cartTable.getItems().forEach(i -> i.setSelected(checked));
            updateTotal();
        });

        totalLabel = new Label("Selected Total: Rp 0");
        totalLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
    }

    private void setupLayout() {

        setupColumns();

        Button checkoutBtn = new Button("Checkout Selected");
        checkoutBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        checkoutBtn.setOnAction(e -> openCheckout());

        Button clearBtn = new Button("Clear Cart");
        clearBtn.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> {
            cartController.clearCart(customerId);
            loadData();
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.close());

        VBox footer = new VBox(10,
                totalLabel,
                new HBox(10, checkoutBtn, clearBtn, backBtn)
        );
        footer.setPadding(new Insets(10));

        VBox root = new VBox(10,
                new VBox(5, new Label("Shopping Cart"), selectAllCheckBox),
                cartTable,
                footer
        );
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        stage.setScene(new Scene(root, 750, 450));
    }

    private void setupColumns() {

        TableColumn<CartItem, Boolean> selectCol = new TableColumn<>("✓");
        selectCol.setCellValueFactory(c -> c.getValue().selectedProperty());
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setPrefWidth(50);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getProduct().getName()
            )
        );

        TableColumn<CartItem, String> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getCount())
            )
        );

        TableColumn<CartItem, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("Rp%,.0f", c.getValue().getTotalPrice())
            )
        );

        TableColumn<CartItem, Void> actionCol = createActionColumn();

        cartTable.getColumns().addAll(
            selectCol, nameCol, qtyCol, totalCol, actionCol
        );
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Action Button Column
    private TableColumn<CartItem, Void> createActionColumn() {

        TableColumn<CartItem, Void> col = new TableColumn<>("Action");

        col.setCellFactory(c -> new TableCell<>() {

            private final Button edit = new Button("Edit");
            private final Button del = new Button("Delete");

            {
                edit.setStyle("-fx-background-color:#0277bd;-fx-text-fill:white;");
                del.setStyle("-fx-background-color:#c62828;-fx-text-fill:white;");

                edit.setOnAction(e ->
                    editQty(getTableView().getItems().get(getIndex()))
                );

                del.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartController.removeFromCart(customerId, item.getIdProduct());
                    loadData();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, edit, del));
            }
        });

        col.setPrefWidth(160);
        return col;
    }
    
    // Edit qty
    private void editQty(CartItem item) {
        // Get product stock
        int currentStock = getProductStock(item.getIdProduct());
        int currentQty = item.getCount();
        
        TextInputDialog dialog = new TextInputDialog(String.valueOf(currentQty));
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText(item.getProduct().getName());
        dialog.setContentText("Quantity (Max: " + currentStock + "):");
        
        // Show dialog and handle result
        String result = dialog.showAndWait().orElse(null);
        
        if (result != null) {
            try {
                int qty = Integer.parseInt(result);
                
                // Validation
                if (qty <= 0) {
                    showError("Quantity must be greater than 0");
                    return;
                }
                
                if (qty > currentStock) {
                    showError("Not enough stock! Available: " + currentStock);
                    return;
                }
                
                // Update cart
                cartController.updateCartItem(customerId, item.getIdProduct(), qty);
                loadData();
                
            } catch (NumberFormatException e) {
                showError("Please enter a valid number");
            }
        }
    }

    private int getProductStock(String productId) {
        // Method 1: If CartItem has product with stock
        Product product = ProductModel.getProductById(productId);
        if (product != null) {
            return product.getStock();
        }
        
        // Method 2: Direct database query
        String sql = "SELECT stock FROM Product WHERE idProduct = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("stock");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    // Error Validation
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadData() {
        ObservableList<CartItem> items =
            FXCollections.observableArrayList(
                cartController.getCustomerCart(customerId)
            );

        items.forEach(i ->
            i.selectedProperty().addListener((a,b,c) -> updateTotal())
        );

        cartTable.setItems(items);
        updateTotal();
    }

    // Update total harga
    private void updateTotal() {
        double total = cartTable.getItems().stream()
            .filter(CartItem::isSelected)
            .mapToDouble(CartItem::getTotalPrice)
            .sum();

        totalLabel.setText(
            String.format("Selected Total: Rp%,.0f", total)
        );
    }

    private void openCheckout() {
        List<CartItem> selected =
            cartTable.getItems().stream()
                .filter(CartItem::isSelected)
                .collect(Collectors.toList());

        if (selected.isEmpty()) {
            alert("No item selected");
            return;
        }

        // Checkout dialog
        CheckoutView checkout =
            new CheckoutView(customerId, selected);

        checkout.showAndWait();

        if (onCheckoutSuccess != null) {
            onCheckoutSuccess.run();
        }

        loadData();
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public void show() {
        stage.show();
    }
}