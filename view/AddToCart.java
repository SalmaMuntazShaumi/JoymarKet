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
 * AddToCartView - View for adding products to cart
 * This view allows customers to add products to their shopping cart
 */
public class AddToCart {
    
    private Stage stage;
    private Scene scene;
    private GridPane gridPane;
    
    private Label titleLabel;
    private Label productNameLabel;
    private Label stockLabel;
    private Label countLabel;
    private TextField countField;
    private Button addButton;
    private Button cancelButton;
    private Label errorLabel;
    
    private String customerId;
    private String productId;
    private String productName;
    private int availableStock;
    
    /**
     * Constructor
     * @param customerId Logged in customer ID
     * @param productId Product to add to cart
     * @param productName Product name for display
     * @param availableStock Available stock for validation
     */
    public AddToCart(String customerId, String productId, String productName, int availableStock) {
        this.customerId = customerId;
        this.productId = productId;
        this.productName = productName;
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
        
        titleLabel = new Label("Add to Cart");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        productNameLabel = new Label("Product: " + productName);
        stockLabel = new Label("Available Stock: " + availableStock);
        
        countLabel = new Label("Quantity:");
        countField = new TextField();
        countField.setPromptText("Enter quantity");
        
        addButton = new Button("Add to Cart");
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
        gridPane.add(countLabel, 0, 3);
        gridPane.add(countField, 1, 3);
        
        // Button container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addButton, cancelButton);
        gridPane.add(buttonBox, 0, 4, 2, 1);
        
        gridPane.add(errorLabel, 0, 5, 2, 1);
        
        scene = new Scene(gridPane, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Add to Cart");
    }
    
    /**
     * Sets up event handlers for buttons
     */
    private void setEventHandlers() {
        addButton.setOnAction(e -> handleAddToCart());
        cancelButton.setOnAction(e -> stage.close());
    }
    
    /**
     * Handles the add to cart action
     * Validates input and calls the controller
     */
    private void handleAddToCart() {
        errorLabel.setVisible(false);
        
        try {
            // Parse count from text field
            String countText = countField.getText().trim();
            
            if (countText.isEmpty()) {
                showError("Please enter a quantity");
                return;
            }
            
            int count = Integer.parseInt(countText);
            
            // Call controller to add item to cart
            String validationResult = CartItemHandler.addCartItem(customerId, productId, count);
            
            if (validationResult.isEmpty()) {
                // Success
                showSuccess("Product added to cart successfully!");
                
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