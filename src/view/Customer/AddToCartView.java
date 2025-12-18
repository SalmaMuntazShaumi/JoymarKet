package view.Customer;

import controller.CartController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * AddToCartView - Dialog for adding products to cart
 */
public class AddToCartView {
	private Stage stage;
	private GridPane gridPane;

	private Label titleLabel;
	private Label productNameLabel;
	private Label stockLabel;
	private TextField countField;
	private Button addButton;
	private Button cancelButton;
	private Label messageLabel;

	private String customerId;
	private String productId;
	private String productName;
	private int availableStock;
	private CartController cartController;

	public AddToCartView(String customerId, String productId, String productName, int availableStock) {
		this.customerId = customerId;
		this.productId = productId;
		this.productName = productName;
		this.availableStock = availableStock;
		this.cartController = CartController.getInstance();

		initialize();
		setupLayout();
		setupEventHandlers();
	}

	private void initialize() {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		gridPane = new GridPane();

		titleLabel = new Label("Add to Cart");
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		productNameLabel = new Label("Product: " + productName);
		productNameLabel.setWrapText(true);

		stockLabel = new Label("Available Stock: " + availableStock);

		countField = new TextField("1");
		countField.setPromptText("Enter quantity");

		addButton = new Button("Add to Cart");
		addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

		cancelButton = new Button("Cancel");

		messageLabel = new Label();
		messageLabel.setWrapText(true);
		messageLabel.setVisible(false);
	}

	private void setupLayout() {
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setPadding(new Insets(20));
		gridPane.setHgap(10);
		gridPane.setVgap(15);

		int row = 0;

		// Title
		gridPane.add(titleLabel, 0, row++, 2, 1);

		// Product info
		gridPane.add(productNameLabel, 0, row++, 2, 1);
		gridPane.add(stockLabel, 0, row++, 2, 1);

		// Quantity input
		gridPane.add(new Label("Quantity:"), 0, row);
		gridPane.add(countField, 1, row++);

		// Buttons
		HBox buttonBox = new HBox(15);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(addButton, cancelButton);
		gridPane.add(buttonBox, 0, row++, 2, 1);

		// Message label
		gridPane.add(messageLabel, 0, row, 2, 1);

		Scene scene = new Scene(gridPane, 400, 250);
		stage.setScene(scene);
		stage.setTitle("Add to Cart");
		stage.setResizable(false);
	}

	private void setupEventHandlers() {
		addButton.setOnAction(e -> handleAddToCart());
		cancelButton.setOnAction(e -> stage.close());
	}

	private void handleAddToCart() {
		clearMessage();

		String countText = countField.getText().trim();
		if (countText.isEmpty()) {
			showMessage("Please enter quantity", true);
			return;
		}

		// Validationo
		try {
			int quantity = Integer.parseInt(countText);

			if (quantity <= 0) {
				showMessage("Quantity must be greater than 0", true);
				return;
			}

			if (quantity > availableStock) {
				showMessage("Not enough stock. Available: " + availableStock, true);
				return;
			}

			String result = cartController.addToCart(customerId, productId, quantity);

			if (result.isEmpty()) {
				showMessage("Successfully added to cart!", false);

				// Close after delay
				new Thread(() -> {
					try {
						Thread.sleep(1500);
						Platform.runLater(() -> stage.close());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}).start();
			} else {
				showMessage(result, true);
			}
		} catch (NumberFormatException e) {
			showMessage("Please enter a valid number", true);
		}
	}

	private void clearMessage() {
		messageLabel.setText("");
		messageLabel.setVisible(false);
	}

	private void showMessage(String message, boolean isError) {
		messageLabel.setText(message);
		messageLabel.setStyle(isError ? "-fx-text-fill: #d32f2f;" : "-fx-text-fill: #388e3c;");
		messageLabel.setVisible(true);
	}

	public void show() {
		stage.show();
	}

	public void showAndWait() {
		stage.showAndWait();
	}
}