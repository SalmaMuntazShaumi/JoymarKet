package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model_entity.*;
import controller.AuthController;
import controller.CustomerController;
import controller.AdminController;
import controller.CourierController;

public class LoginView {
    private AuthController authController;
    private Stage stage;
    
    public LoginView() {
        this.authController = new AuthController();
    }
    
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Login - Shop System");
        
        // Title Label
        Label titleLabel = new Label("Shop System Login");
        titleLabel.setFont(Font.font("Arial", 20));
        
        // Email field
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        
        // Password field
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        
        // Role selection
        Label roleLabel = new Label("Login as:");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("customer", "courier", "admin");
        roleCombo.setValue("customer");
        
        // Message label
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");
        
        // Buttons
        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        
        Button registerButton = new Button("Register");
        
        // Login button action
        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String selectedRole = roleCombo.getValue();
            
            if (email.isEmpty()) {
                messageLabel.setText("Email is required");
                return;
            }
            
            if (password.isEmpty()) {
                messageLabel.setText("Password is required");
                return;
            }
            
            User user = authController.login(email, password);
            
            if (user == null) {
                messageLabel.setText("Invalid email or password");
                return;
            }
            
            // Check if user role matches selected role
            if (!user.getRole().equals(selectedRole)) {
                messageLabel.setText("Please select correct role: " + user.getRole());
                return;
            }
            
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Login successful! Welcome " + user.getFullName());
            
            // Redirect based on role
            if (user.isCustomer()) {
                Customer customer = (Customer) user;
                showProductView(customer.getIdUser(), customer.getFullName());
            } else if (user.isCourier()) {
                Courier courier = (Courier) user;
                showCourierView(courier);
            } else if (user.isAdmin()) {
                Admin admin = (Admin) user;
                showAdminView(admin);
            }
        });
        
        // Register button action
        registerButton.setOnAction(e -> {
            RoleSelection selectionView = new RoleSelection();
            Stage selectionStage = new Stage();
            selectionView.start(selectionStage);
            stage.close();
        });
        
        // Layout
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
            titleLabel,
            emailLabel,
            emailField,
            passwordLabel,
            passwordField,
            roleLabel,
            roleCombo,
            loginButton,
            new Label("Don't have an account?"),
            registerButton,
            messageLabel
        );
        
        Scene scene = new Scene(layout, 350, 450);
        stage.setScene(scene);
        stage.show();
    }
    
    private void showProductView(String customerId, String customerName) {
        ProductView productView = new ProductView();
        Stage productStage = new Stage();
        productView.start(productStage, customerId, customerName);
        stage.close();
    }
    
    private void showCourierView(Courier courier) {
        // Courier view dengan controller
        CourierController courierController = new CourierController();
        // Anda bisa buat CourierDashboardView nanti
        
        Stage courierStage = new Stage();
        courierStage.setTitle("Courier Dashboard - " + courier.getFullName());
        
        Label welcomeLabel = new Label("Welcome Courier: " + courier.getFullName());
        welcomeLabel.setFont(Font.font("Arial", 18));
        
        Label infoLabel = new Label("Vehicle: " + courier.getVehicleType() + 
                                   " - " + courier.getVehiclePlate());
        
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(welcomeLabel, infoLabel);
        
        Scene scene = new Scene(layout, 400, 300);
        courierStage.setScene(scene);
        courierStage.show();
        stage.close();
    }
    
    private void showAdminView(Admin admin) {
        // Admin view dengan controller
        AdminController adminController = new AdminController();
        // Anda bisa buat AdminDashboardView nanti
        
        Stage adminStage = new Stage();
        adminStage.setTitle("Admin Dashboard - " + admin.getFullName());
        
        Label welcomeLabel = new Label("Welcome Admin: " + admin.getFullName());
        welcomeLabel.setFont(Font.font("Arial", 18));
        
        Label infoLabel = new Label("Emergency Contact: " + admin.getEmergencyContact());
        
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(welcomeLabel, infoLabel);
        
        Scene scene = new Scene(layout, 400, 300);
        adminStage.setScene(scene);
        adminStage.show();
        stage.close();
    }
}