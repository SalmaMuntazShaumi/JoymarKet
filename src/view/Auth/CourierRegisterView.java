package view.Auth;

import controller.CourierController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class CourierRegisterView {

	public CourierRegisterView() {
		// Constructor tanpa parameter
	}

	public void start(Stage stage) {
		stage.setTitle("Register as Courier");

		Label titleLabel = new Label("Courier Registration");
		titleLabel.setFont(Font.font("Arial", 20));

		// Form fields - HAPUS ID FIELD
		TextField nameField = new TextField();
		nameField.setPromptText("Your full name");

		TextField emailField = new TextField();
		emailField.setPromptText("your@email.com");

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Minimum 6 characters");

		PasswordField confirmField = new PasswordField();
		confirmField.setPromptText("Re-enter password");

		TextField phoneField = new TextField();
		phoneField.setPromptText("e.g., 081234567890");

		TextArea addressArea = new TextArea();
		addressArea.setPromptText("Your complete address");
		addressArea.setPrefRowCount(3);

		// Gender field
		Label genderLabel = new Label("Gender:");
		ComboBox<String> genderCombo = new ComboBox<>();
		genderCombo.getItems().addAll("Female", "Male");
		genderCombo.setValue("Female");

		TextField vehicleTypeField = new TextField();
		vehicleTypeField.setPromptText("e.g., Motorcycle, Car, Truck");

		TextField vehiclePlateField = new TextField();
		vehiclePlateField.setPromptText("e.g., B 1234 XYZ");

		Button registerBtn = new Button("Register");
		registerBtn.setDefaultButton(true);

		Button backBtn = new Button("Back");

		Label messageLabel = new Label();
		messageLabel.setWrapText(true);

		// Register button action
		registerBtn.setOnAction(e -> {
			CourierController controller = new CourierController();

			String result = controller.registerCourier(nameField.getText(), emailField.getText(),
					passwordField.getText(), confirmField.getText(), phoneField.getText(), addressArea.getText(),
					genderCombo.getValue(), vehicleTypeField.getText(), vehiclePlateField.getText());

			if (result.startsWith("SUCCESS")) {
				messageLabel.setStyle("-fx-text-fill: green;");
				// Extract generated ID
				String generatedId = result.split("#")[1];
				messageLabel.setText("Courier registration successful!\nYour Courier ID: " + generatedId);

				// Clear fields
				nameField.clear();
				emailField.clear();
				passwordField.clear();
				confirmField.clear();
				phoneField.clear();
				addressArea.clear();
				genderCombo.setValue("Female");
				vehicleTypeField.clear();
				vehiclePlateField.clear();
			} else {
				messageLabel.setStyle("-fx-text-fill: red;");
				messageLabel.setText(result);
			}
		});

		// Back button action
		backBtn.setOnAction(e -> {
			RoleSelection selectionView = new RoleSelection();
			Stage selectionStage = new Stage();
			selectionView.start(selectionStage);
			stage.close();
		});

		// Layout
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20));

		int row = 0;
		grid.add(titleLabel, 0, row++, 2, 1);

		// TIDAK ADA ID FIELD LAGI
		grid.add(new Label("Full Name:"), 0, row);
		grid.add(nameField, 1, row++);

		grid.add(new Label("Email:"), 0, row);
		grid.add(emailField, 1, row++);

		grid.add(new Label("Password:"), 0, row);
		grid.add(passwordField, 1, row++);

		grid.add(new Label("Confirm Password:"), 0, row);
		grid.add(confirmField, 1, row++);

		grid.add(new Label("Phone:"), 0, row);
		grid.add(phoneField, 1, row++);

		grid.add(new Label("Address:"), 0, row);
		grid.add(addressArea, 1, row++);

		grid.add(genderLabel, 0, row);
		grid.add(genderCombo, 1, row++);

		grid.add(new Label("Vehicle Type:"), 0, row);
		grid.add(vehicleTypeField, 1, row++);

		grid.add(new Label("Vehicle Plate:"), 0, row);
		grid.add(vehiclePlateField, 1, row++);

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(registerBtn, backBtn);
		grid.add(buttonBox, 0, row++, 2, 1);

		grid.add(messageLabel, 0, row++, 2, 1);

		ScrollPane scrollPane = new ScrollPane(grid);
		scrollPane.setFitToWidth(true);

		Scene scene = new Scene(scrollPane, 500, 650);
		stage.setScene(scene);
		stage.show();
	}
}