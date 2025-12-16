
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

		// Menu Buttons
		Button manageProductsBtn = new Button("Manage Products");
		manageProductsBtn.setMinWidth(200);
		manageProductsBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

		Button manageUsersBtn = new Button("Manage Users");
		manageUsersBtn.setMinWidth(200);
		manageUsersBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

		Button viewReportsBtn = new Button("View Reports");
		viewReportsBtn.setMinWidth(200);
		viewReportsBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

		Button logoutBtn = new Button("Logout");
		logoutBtn.setMinWidth(200);
		logoutBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

		// Event Handlers
		manageProductsBtn.setOnAction(e -> {
			// Buka Product Management
			AdminProductManagementView productView = new AdminProductManagementView();
			productView.show();
		});

		manageUsersBtn.setOnAction(e -> {
			showAlert("Info", "User management feature coming soon!");
		});

		viewReportsBtn.setOnAction(e -> {
			showAlert("Info", "Reports feature coming soon!");
		});

		logoutBtn.setOnAction(e -> {
			LoginView loginView = new LoginView();
			Stage loginStage = new Stage();
			loginView.start(loginStage);
			stage.close();
		});

		VBox layout = new VBox(20);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(20));
		layout.getChildren().addAll(welcomeLabel, infoLabel, manageProductsBtn, manageUsersBtn, viewReportsBtn,
				logoutBtn);

		Scene scene = new Scene(layout, 400, 400);
		stage.setScene(scene);
		stage.show();
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}