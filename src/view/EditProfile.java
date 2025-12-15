package view;

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
import controller.AuthController;

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
        Button logoutBtn = new Button("Logout");
        
        // Password change section
        Label passwordLabel = new Label("Change Password:");
        javafx.scene.control.PasswordField oldPasswordField = new javafx.scene.control.PasswordField();
        oldPasswordField.setPromptText("Current Password");
        javafx.scene.control.PasswordField newPasswordField = new javafx.scene.control.PasswordField();
        newPasswordField.setPromptText("New Password");
        javafx.scene.control.PasswordField confirmPasswordField = new javafx.scene.control.PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");
        Button changePasswordBtn = new Button("Change Password");

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
        
        changePasswordBtn.setOnAction(e -> {
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            String result = authController.changePassword(
                user.getIdUser(),
                oldPassword,
                newPassword,
                confirmPassword
            );
            
            if ("SUCCESS".equals(result)) {
                infoLabel.setStyle("-fx-text-fill: green;");
                infoLabel.setText("Password changed successfully!");
                oldPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                infoLabel.setStyle("-fx-text-fill: red;");
                infoLabel.setText(result);
            }
        });

        logoutBtn.setOnAction(e -> {
            // Show login view
            LoginView loginView = new LoginView();
            Stage loginStage = new Stage();
            loginView.start(loginStage);
            stage.close();
        });

        VBox root = new VBox(10,
            new Label("Edit Profile"),
            new Label("Name:"),
            nameField,
            new Label("Email:"),
            emailField,
            new Label("Phone:"),
            phoneField,
            new Label("Address:"),
            addressField,
            new Label("Gender:"),
            genderCombo,
            saveBtn,
            new javafx.scene.control.Separator(),
            passwordLabel,
            oldPasswordField,
            newPasswordField,
            confirmPasswordField,
            changePasswordBtn,
            new javafx.scene.control.Separator(),
            logoutBtn,
            infoLabel
        );
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