package view.Register;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import service.UserService;
import view.RoleSelection;

public class AdminRegisterView {
    private UserService userService;
    
    public AdminRegisterView(UserService userService) {
        this.userService = userService;
    }
    
    public void start(Stage stage) {
        stage.setTitle("Register as Admin");
        
        Label titleLabel = new Label("Admin Registration");
        titleLabel.setFont(Font.font("Arial", 20));
        
        // Form fields
        TextField idField = new TextField();
        idField.setPromptText("e.g., ADM001");
        
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
        
        // Gender field - ADDED
        Label genderLabel = new Label("Gender:");
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Female", "Male");
        genderCombo.setValue("Female");
        
        TextField emergencyField = new TextField();
        emergencyField.setPromptText("Emergency contact number");
        
        Button registerBtn = new Button("Register");
        registerBtn.setDefaultButton(true);
        
        Button backBtn = new Button("Back");
        
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        
        // Register button action - UPDATED to include gender
        registerBtn.setOnAction(e -> {
            String result = userService.registerAdmin(
                idField.getText(),
                nameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmField.getText(),
                phoneField.getText(),
                addressArea.getText(),
                genderCombo.getValue(), // ADDED GENDER
                emergencyField.getText()
            );
            
            if ("SUCCESS".equals(result)) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText("Admin registration successful!");
                
                // Clear fields
                idField.clear();
                nameField.clear();
                emailField.clear();
                passwordField.clear();
                confirmField.clear();
                phoneField.clear();
                addressArea.clear();
                genderCombo.setValue("Female"); // RESET GENDER
                emergencyField.clear();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText(result);
            }
        });
        
        // Back button action
        backBtn.setOnAction(e -> {
            RoleSelection selectionView = new RoleSelection(userService);
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
        
        grid.add(new Label("Admin ID:"), 0, row);
        grid.add(idField, 1, row++);
        
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
        
        grid.add(genderLabel, 0, row); // ADDED GENDER ROW
        grid.add(genderCombo, 1, row++);
        
        grid.add(new Label("Emergency Contact:"), 0, row);
        grid.add(emergencyField, 1, row++);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerBtn, backBtn);
        grid.add(buttonBox, 0, row++, 2, 1);
        
        grid.add(messageLabel, 0, row++, 2, 1);
        
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        
        Scene scene = new Scene(scrollPane, 500, 650); // Increased height
        stage.setScene(scene);
        stage.show();
    }
}