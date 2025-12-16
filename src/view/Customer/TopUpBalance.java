package view.Customer;

import controller.CustomerController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TopUpBalance {

    private String customerId;
    private Stage stage;
    private CustomerController controller;

    public TopUpBalance(String customerId) {
        this.customerId = customerId;
        this.controller = new CustomerController();
        initUI();
    }

    private void initUI() {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // 🔒 block window lain
        stage.setTitle("Top Up Balance");

        Label label = new Label("Top Up Amount");
        TextField amountField = new TextField();
        amountField.setPromptText("Minimum 10000");

        Button topUpBtn = new Button("Top Up");
        Button cancelBtn = new Button("Cancel");

        topUpBtn.setOnAction(e -> {
            String result = controller.topUpBalance(
                customerId,
                amountField.getText()
            );

            if ("SUCCESS".equals(result)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Top up success");
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", result);
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(10,
            label,
            amountField,
            topUpBtn,
            cancelBtn
        );
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 300, 200));
    }

    // ✅ NON-BLOCKING
    public void show() {
        stage.show();
    }

    // ✅ BLOCKING (INI YANG KAMU BUTUHKAN)
    public void showAndWait() {
        stage.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
