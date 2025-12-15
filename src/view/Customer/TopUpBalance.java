package view.Customer;

import controller.CustomerController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TopUpBalance {
    private String customerId;

    public TopUpBalance(String customerId) {
        this.customerId = customerId;
    }

    public void show() {
        Stage stage = new Stage();
        CustomerController controller = new CustomerController();

        Label label = new Label("Top Up Amount");
        TextField amountField = new TextField();
        amountField.setPromptText("Minimum 10000");

        Button topUpBtn = new Button("Top Up");
        Button cancelBtn = new Button("Cancel");

        topUpBtn.setOnAction(e -> {
            String result = controller.topUpBalance(customerId, amountField.getText());
            
            if ("SUCCESS".equals(result)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Top up success");
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", result);
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(10, label, amountField, topUpBtn, cancelBtn);
        root.setPadding(new Insets(15));

        stage.setTitle("Top Up Balance");
        stage.setScene(new Scene(root, 300, 200));
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}