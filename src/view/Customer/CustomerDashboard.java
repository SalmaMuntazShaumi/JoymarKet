package view.Customer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model_entity.Product;
import model_entity.User;
import view.EditProfile;
import controller.ProductController;
import controller.CustomerController;
import controller.AuthController;

public class CustomerDashboard extends Application {

    private Label balanceLabel;
    private ProductController productController;
    private CustomerController customerController;
    private AuthController authController;

    private String currentCustomerId;
    private String currentCustomerName;

    private TableView<Product> productTable;
    private TextField searchField;

    public void start(Stage primaryStage, String customerId, String customerName) {
        this.currentCustomerId = customerId;
        this.currentCustomerName = customerName;

        this.productController = ProductController.getInstance();
        this.customerController = new CustomerController();
        this.authController = new AuthController();

        initializeUI();
        setupLayout(primaryStage);
        loadProducts();
        updateBalance();
    }

    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, "GUEST", "Guest");
    }

    private void initializeUI() {
        productTable = new TableView<>();
        setupProductTable();

        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setMinWidth(200);
    }

    private void setupProductTable() {
        TableColumn<Product, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getIdProduct())
        );

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getName())
        );

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getCategory())
        );

        TableColumn<Product, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("Rp%,.0f", c.getValue().getPrice())
            )
        );

        TableColumn<Product, String> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getStock())
            )
        );

        TableColumn<Product, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button addBtn = new Button("Add to Cart");

            {
                addBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");
                addBtn.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    showAddToCartDialog(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Product p = getTableView().getItems().get(getIndex());
                    addBtn.setDisable(p.getStock() <= 0);
                    setGraphic(addBtn);
                }
            }
        });

        productTable.getColumns().addAll(
            idCol, nameCol, catCol, priceCol, stockCol, actionCol
        );
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupLayout(Stage stage) {
        stage.setTitle("Customer Dashboard - " + currentCustomerName);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> {
            loadProducts();
            updateBalance();
        });

        Button cartBtn = new Button("View Cart");
        cartBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white;");
        cartBtn.setOnAction(e -> showCartView());

        Button topUpBtn = new Button("Top Up");
        topUpBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        topUpBtn.setOnAction(e -> showTopUp());

        Button editProfileBtn = new Button("Edit Profile");
        editProfileBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
        editProfileBtn.setOnAction(e -> showEditProfile());

        balanceLabel = new Label();

        HBox toolbar = new HBox(10,
            balanceLabel, searchField, refreshBtn, cartBtn, topUpBtn, editProfileBtn
        );
        toolbar.setPadding(new Insets(10));

        VBox root = new VBox(10, toolbar, productTable);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 850, 500));
        stage.show();
    }

    private void updateBalance() {
        try {
            double balance = customerController.getCustomerBalance(currentCustomerId);
            balanceLabel.setText(String.format("Balance: Rp%,.0f", balance));
        } catch (Exception e) {
            balanceLabel.setText("Balance: -");
        }
    }

    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList(
            productController.getAllProducts()
        );
        productTable.setItems(products);

        searchField.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isBlank()) {
                productTable.setItems(products);
            } else {
                productTable.setItems(
                    FXCollections.observableArrayList(
                        productController.searchProducts(newV)
                    )
                );
            }
        });
    }

    private void showAddToCartDialog(Product product) {
        AddToCartView view = new AddToCartView(
            currentCustomerId,
            product.getIdProduct(),
            product.getName(),
            product.getStock()
        );
        view.showAndWait();
        loadProducts();
    }

    private void showCartView() {
        CartView cartView = new CartView(
            currentCustomerId,
            () -> {
                updateBalance();
                loadProducts();
            }
        );
        cartView.show();
    }

    private void showTopUp() {
        TopUpBalance topUp = new TopUpBalance(currentCustomerId);
        topUp.showAndWait();
        updateBalance();
    }

    private void showEditProfile() {
        try {
            User user = authController.getUserById(currentCustomerId);
            if (user == null) {
                showAlert("Error", "User not found");
                return;
            }
            new EditProfile(user).show();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
