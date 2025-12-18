package view.Customer;

import controller.AuthController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model_entity.User;
import view.Auth.LoginView;

public class EditProfile {
	private AuthController authController;
	private User user;

	public EditProfile(User user) {
		this.authController = new AuthController();
		this.user = user;

		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
	}

	public void start(Stage stage) {
		// Create a local copy of user data
		String fullName = user.getFullName() != null ? user.getFullName() : "";
		String email = user.getEmail() != null ? user.getEmail() : "";
		String phone = user.getPhone() != null ? user.getPhone() : "";
		String address = user.getAddress() != null ? user.getAddress() : "";
		String gender = user.getGender() != null ? user.getGender() : "Female";

		TextField nameField = new TextField(fullName);
		TextField emailField = new TextField(email);
		TextField phoneField = new TextField(phone);
		TextField addressField = new TextField(address);

		ComboBox<String> genderCombo = new ComboBox<>();
		genderCombo.getItems().addAll("Female", "Male");
		genderCombo.setValue(gender);

		Label infoLabel = new Label();

		Button saveBtn = new Button("Save Changes");

		saveBtn.setOnAction(e -> {
			// Update user information
			user.setFullName(nameField.getText().trim());
			user.setEmail(emailField.getText().trim());
			user.setPhone(phoneField.getText().trim());
			user.setAddress(addressField.getText().trim());
			user.setGender(genderCombo.getValue());

			String result = authController.editProfile(user);

			if ("SUCCESS".equals(result)) {
				infoLabel.setStyle("-fx-text-fill: green;");
				infoLabel.setText("Profile updated successfully!");
			} else {
				infoLabel.setStyle("-fx-text-fill: red;");
				infoLabel.setText(result);
			}
		});


		VBox root = new VBox(10, new Label("Edit Profile"), new Label("Name:"), nameField, new Label("Email:"),
				emailField, new Label("Phone:"), phoneField, new Label("Address:"), addressField, new Label("Gender:"),
				genderCombo, saveBtn, infoLabel);
		root.setPadding(new Insets(20));

		stage.setScene(new Scene(root, 400, 650));
		stage.setTitle("Edit Profile - " + user.getFullName());
		stage.show();
	}

	public void show() {
		Stage stage = new Stage();
		start(stage);
	}
}