package view;

import controller.CartItemHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * UpdateCartView - View for updating cart item quantity
 * Allows customers to modify the quantity of items in their cart
 */
public class UpdateCart {
    
    private Stage stage;
    private Scene scene;
    private GridPane gridPane;
    
    private Label titleLabel;
    private Label productNameLabel;
    private Label stockLabel;
    private Label currentCountLabel;
    private Label countLabel;
    private TextField countField;
    private Button updateButton;
    private Button deleteButton;
    private Button cancelButton;
    private Label errorLabel;
    
    private String customerId;
    private String productId;
    private String productName;
    private int currentCount;
    private int availableStock;
    
    /**
     * Constructor
     * @param customerId Logged in customer ID
     * @param productId Product ID in cart
     * @param productName Product name for display
     * @param currentCount Current quantity in cart
     * @param availableStock Available stock for validation
     */
    public UpdateCart(String customerId, String productId, String productName, 
                          int currentCount, int availableStock) {
        this.customerId = customerId;
        this.productId = productId;
        this.productName = productName;
        this.currentCount = currentCount;
        this.availableStock = availableStock;
        
        initialize();
        setLayout();
        setEventHandlers();
    }
    
    /**
     * Initializes all UI components
     */
    private void initialize() {
        stage = new Stage();
        gridPane = new GridPane();
        
        titleLabel = new Label("Update Cart Item");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        productNameLabel = new Label("Product: " + productName);
        stockLabel = new Label("Available Stock: " + availableStock);
        currentCountLabel = new Label("Current Quantity: " + currentCount);
        
        countLabel = new Label("New Quantity:");
        countField = new TextField();
        countField.setText(String.valueOf(currentCount));
        countField.setPromptText("Enter new quantity");
        
        updateButton = new Button("Update");
        deleteButton = new Button("Remove from Cart");
        deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        cancelButton = new Button("Cancel");
        
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(false);
    }
    
    /**
     * Sets up the layout of the view
     */
    private void setLayout() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        
        // Add components to grid
        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(productNameLabel, 0, 1, 2, 1);
        gridPane.add(stockLabel, 0, 2, 2, 1);
        gridPane.add(currentCountLabel, 0, 3, 2, 1);
        gridPane.add(countLabel, 0, 4);
        gridPane.add(countField, 1, 4);
        
        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(updateButton, deleteButton, cancelButton);
        gridPane.add(buttonBox, 0, 5, 2, 1);
        
        gridPane.add(errorLabel, 0, 6, 2, 1);
        
        scene = new Scene(gridPane, 450, 350);
        stage.setScene(scene);
        stage.setTitle("Update Cart Item");
    }
    
    /**
     * Sets up event handlers for buttons
     */
    private void setEventHandlers() {
        updateButton.setOnAction(e -> handleUpdateCart());
        deleteButton.setOnAction(e -> handleDeleteFromCart());
        cancelButton.setOnAction(e -> stage.close());
    }
    
    /**
     * Handles the update cart action
     * Validates input and calls the controller
     */
    private void handleUpdateCart() {
        errorLabel.setVisible(false);
        
        try {
            // Parse count from text field
            String countText = countField.getText().trim();
            
            if (countText.isEmpty()) {
                showError("Please enter a quantity");
                return;
            }
            
            int count = Integer.parseInt(countText);
            
            // Call controller to update cart item
            String validationResult = CartItemHandler.updateCartItem(customerId, productId, count);
            
            if (validationResult.isEmpty()) {
                // Success
                showSuccess("Cart updated successfully!");
                
                // Close window after short delay
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(() -> stage.close());
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } else {
                // Validation error
                showError(validationResult);
            }
            
        } catch (NumberFormatException ex) {
            showError("Count must be a valid number");
        }
    }
    
    /**
     * Handles removing item from cart
     */
    private void handleDeleteFromCart() {
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Remove from Cart");
        confirmAlert.setContentText("Are you sure you want to remove this item from your cart?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String result = CartItemHandler.deleteCartItem(customerId, productId);
                
                if (result.isEmpty()) {
                    showSuccess("Item removed from cart!");
                    
                    // Close window after short delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            javafx.application.Platform.runLater(() -> stage.close());
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                } else {
                    showError(result);
                }
            }
        });
    }
    
    /**
     * Displays an error message
     * @param message Error message to display
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }
    
    /**
     * Displays a success message
     * @param message Success message to display
     */
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setVisible(true);
    }
    
    /**
     * Shows the view
     */
    public void show() {
        stage.show();
    }
}