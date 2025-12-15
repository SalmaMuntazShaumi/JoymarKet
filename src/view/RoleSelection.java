package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import view.Register.AdminRegisterView;
import view.Register.CourierRegisterView;
import view.Register.CustomerRegisterView;

public class RoleSelection {
    
    public RoleSelection() {
        // Constructor tanpa service
    }
    
    public void start(Stage stage) {
        stage.setTitle("Select Registration Type");
        
        Label titleLabel = new Label("Register as:");
        titleLabel.setFont(Font.font("Arial", 24));
        
        Button customerBtn = new Button("Customer");
        customerBtn.setMinWidth(200);
        customerBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        
        Button courierBtn = new Button("Courier");
        courierBtn.setMinWidth(200);
        courierBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        
        Button adminBtn = new Button("Admin");
        adminBtn.setMinWidth(200);
        adminBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        
        Button backBtn = new Button("Back to Login");
        backBtn.setMinWidth(200);
        backBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        
        // Event handlers
        customerBtn.setOnAction(e -> {
            CustomerRegisterView customerView = new CustomerRegisterView();
            Stage customerStage = new Stage();
            customerView.start(customerStage);
            stage.close();
        });
        
        courierBtn.setOnAction(e -> {
            CourierRegisterView courierView = new CourierRegisterView();
            Stage courierStage = new Stage();
            courierView.start(courierStage);
            stage.close();
        });
        
        adminBtn.setOnAction(e -> {
            AdminRegisterView adminView = new AdminRegisterView();
            Stage adminStage = new Stage();
            adminView.start(adminStage);
            stage.close();
        });
        
        backBtn.setOnAction(e -> {
            LoginView loginView = new LoginView();
            Stage loginStage = new Stage();
            loginView.start(loginStage);
            stage.close();
        });
        
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getChildren().addAll(
            titleLabel,
            customerBtn,
            courierBtn,
            adminBtn,
            backBtn
        );
        
        Scene scene = new Scene(layout, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
}