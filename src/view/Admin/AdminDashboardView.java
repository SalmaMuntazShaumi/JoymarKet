package view.Admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model_entity.Admin;
import view.Auth.LoginView;

public class AdminDashboardView {
    private Admin admin;

    public AdminDashboardView(Admin admin) {
        this.admin = admin;
    }

    public void start(Stage stage) {
        stage.setTitle("Admin Dashboard - " + admin.getFullName());

        Label welcomeLabel = new Label("Welcome Admin: " + admin.getFullName());
        welcomeLabel.setFont(Font.font("Arial", 18));

        Label infoLabel = new Label("Emergency Contact: " + admin.getEmergencyContact());

        Button manageProductsBtn = new Button("Manage Products");
        Button manageOrdersBtn = new Button("Manage Orders");
        Button logoutBtn = new Button("Logout");

        manageProductsBtn.setMinWidth(220);
        manageOrdersBtn.setMinWidth(220);
        logoutBtn.setMinWidth(220);

        manageProductsBtn.setOnAction(e -> {
            AdminProductManagementView productView = new AdminProductManagementView();
            productView.show();
        });

        manageOrdersBtn.setOnAction(e -> {
            AdminOrdersDashboardView ordersView = new AdminOrdersDashboardView();
            ordersView.show();
        });

        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView();
            Stage loginStage = new Stage();
            loginView.start(loginStage);
            stage.close();
        });

        VBox layout = new VBox(20,
                welcomeLabel,
                infoLabel,
                manageProductsBtn,
                manageOrdersBtn,
                logoutBtn
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        stage.setScene(new Scene(layout, 420, 380));
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
