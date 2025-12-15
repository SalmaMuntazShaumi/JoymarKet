package view;

import handler.ProductHandler;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Product;
import model.User;
import service.UserService;

public class ProductView extends Application {
	
	private Label balanceLabel;
    
    private ProductHandler productHandler;
    private UserService service;
    private String currentCustomerId;
    
    private TableView<Product> productTable;
    private TextField searchField;

    public void start(Stage primaryStage, String customerId) {
        this.currentCustomerId = customerId;
        productHandler = ProductHandler.getInstance();
        this.service = new UserService(); // Initialize UserService

        initializeUI();
        setupLayout(primaryStage);
        loadProducts();
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.currentCustomerId = "GUEST";
        start(primaryStage, currentCustomerId);
    }
    
    private void initializeUI() {
        productTable = new TableView<>();
        setupProductTable();
        
        searchField = new TextField();
        searchField.setPromptText("Search products...");
        searchField.setMinWidth(200);
    }
    
    private void setupProductTable() {
        // ID Column - use lambda instead
        TableColumn<Product, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getIdProduct();
            return new javafx.beans.property.SimpleStringProperty(id);
        });
        idCol.setMinWidth(80);
        
        // Name Column
        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getName();
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        nameCol.setMinWidth(150);
        
        // Category Column
        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(cellData -> {
            String category = cellData.getValue().getCategory();
            return new javafx.beans.property.SimpleStringProperty(category);
        });
        catCol.setMinWidth(100);
        
        // Price Column
        TableColumn<Product, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("Rp%,.0f", cellData.getValue().getPrice())));
        priceCol.setMinWidth(100);
        
        // Stock Column
        TableColumn<Product, String> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getStock())));
        stockCol.setMinWidth(80);
        
        // Add to Cart Column
        TableColumn<Product, Void> actionCol = new TableColumn<>("Action");
        actionCol.setMinWidth(120);
        actionCol.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button addBtn = new Button("Add to Cart");
            
            {
                addBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");
                addBtn.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showAddToCartDialog(product);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    addBtn.setDisable(product.getStock() <= 0);
                    setGraphic(addBtn);
                }
            }
        });
        
        productTable.getColumns().addAll(idCol, nameCol, catCol, priceCol, stockCol, actionCol);
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void setupLayout(Stage stage) {
        stage.setTitle("Product Catalog - Customer: " + currentCustomerId);
        
        // Top toolbar
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadProducts());
        
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
        updateBalance();
        
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.getChildren().addAll(balanceLabel, searchField, refreshBtn, cartBtn, topUpBtn, editProfileBtn);
        
        // Main layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(toolbar, productTable);
        
        Scene scene = new Scene(root, 850, 500);
        stage.setScene(scene);
        stage.show();
    }
    
    private void updateBalance() {
  	  try {
  	        double balance = service.getCustomerBalance(currentCustomerId);
  	        balanceLabel.setText(String.format("Balance: Rp %, .0f", balance));
  	    } catch (Exception e) {
  	        balanceLabel.setText("Balance: -");
  	    }
  	}

      private void showTopUp() {
          TopUpBalance topUpView = new TopUpBalance(currentCustomerId);
          topUpView.show();
          updateBalance();
      }
    
    private void loadProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList(
            productHandler.getAllProducts()
        );
        productTable.setItems(products);
    }
    
    private void showAddToCartDialog(Product product) {
        AddToCartView addView = new AddToCartView(
            currentCustomerId,
            product.getIdProduct(),
            product.getName(),
            product.getStock()
        );
        addView.show();
    }
    
    private void showCartView() {
        CartView cartView = new CartView(currentCustomerId);
        cartView.show();
    }
    
    private void showEditProfile() {
        try {
            if (service == null) {
                service = new UserService();
            }
            
            User user = service.getUserById(currentCustomerId);
            
            if (user == null) {
                user = createTemporaryUser(currentCustomerId);
            }
            
            // Open edit profile window with the User object
            EditProfile editProfile = new EditProfile(service, user);
            editProfile.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot open Edit Profile: " + e.getMessage());
        }
    }
    
    private User createTemporaryUser(String userId) {
        // Create a temporary user for testing or when user doesn't exist in DB
        User tempUser = new User();
        tempUser.setIdUser(userId);
        tempUser.setFullName("Customer " + userId);
        tempUser.setEmail(userId + "@example.com");
        tempUser.setPhone("1234567890");
        tempUser.setAddress("Default Address");
        tempUser.setGender("Female");
        tempUser.setPassword("password123");
        tempUser.setRole("customer");
        return tempUser;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}