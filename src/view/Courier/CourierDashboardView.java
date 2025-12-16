package view.Courier;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model_entity.Courier;
import view.Auth.LoginView;

public class CourierDashboardView {
    private Courier courier;
    
    public CourierDashboardView(Courier courier) {
        this.courier = courier;
    }
    
    public void start(Stage stage) {
        stage.setTitle("Courier Dashboard - " + courier.getFullName());
        
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: #f7fafc;");
        
        // Header
        Label welcomeLabel = new Label("Welcome, " + courier.getFullName());
        welcomeLabel.setFont(Font.font("Arial", 24));
        welcomeLabel.setStyle("-fx-text-fill: #2d3748; -fx-font-weight: bold;");
        
        Label roleLabel = new Label("Courier");
        roleLabel.setFont(Font.font("Arial", 16));
        roleLabel.setStyle("-fx-text-fill: #718096;");
        
        // Info Card
        VBox infoCard = new VBox(15);
        infoCard.setPadding(new Insets(20));
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        infoCard.setMaxWidth(400);
        
        Label vehicleLabel = new Label("🚚 Vehicle Information");
        vehicleLabel.setFont(Font.font("Arial", 18));
        vehicleLabel.setStyle("-fx-font-weight: bold;");
        
        Label typeLabel = new Label("Type: " + courier.getVehicleType());
        Label plateLabel = new Label("Plate: " + courier.getVehiclePlate());
        Label contactLabel = new Label("Contact: " + courier.getPhone());
        
        infoCard.getChildren().addAll(vehicleLabel, typeLabel, plateLabel, contactLabel);
        
        // Statistics
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        
        VBox deliveredBox = createStatBox("📦", "Delivered", "24");
        VBox pendingBox = createStatBox("⏳", "Pending", "5");
        VBox earningsBox = createStatBox("💰", "Earnings", "Rp 1,250,000");
        
        statsBox.getChildren().addAll(deliveredBox, pendingBox, earningsBox);
        
        // Quick Actions
        VBox actionsBox = new VBox(10);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setMaxWidth(400);
        
        Button viewOrdersBtn = createActionButton("View Active Orders", "#4299e1");
        Button updateStatusBtn = createActionButton("Update Status", "#48bb78");
        Button viewHistoryBtn = createActionButton("Delivery History", "#ed8936");
        
        actionsBox.getChildren().addAll(viewOrdersBtn, updateStatusBtn, viewHistoryBtn);
        
        // Logout Button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
            "-fx-background-color: #fed7d7; " +
            "-fx-text-fill: #c53030; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-radius: 8px;"
        );
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView();
            Stage loginStage = new Stage();
            loginView.start(loginStage);
            stage.close();
        });
        
        mainLayout.getChildren().addAll(
            welcomeLabel,
            roleLabel,
            infoCard,
            statsBox,
            new Separator(),
            new Label("Quick Actions"),
            actionsBox,
            logoutBtn
        );
        
        Scene scene = new Scene(mainLayout, 600, 700);
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox createStatBox(String icon, String title, String value) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-radius: 8px;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 12px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        
        box.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return box;
    }
    
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12px; " +
            "-fx-background-radius: 8px;"
        );
        button.setOnAction(e -> showAlert("Info", text + " feature coming soon!"));
        return button;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}