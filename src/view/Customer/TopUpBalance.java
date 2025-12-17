package view.Customer;

import controller.CustomerController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TopUpBalance {
<<<<<<< HEAD
	private String customerId;

	public TopUpBalance(String customerId) {
		this.customerId = customerId;
	}

	public void show() {
		Stage stage = new Stage();
		CustomerController controller = new CustomerController();
=======

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
>>>>>>> c8ca42ad331c582c33421fdb36bdbdab07ec9a56

		Label label = new Label("Top Up Amount");
		TextField amountField = new TextField();
		amountField.setPromptText("Minimum 10000");

		Button topUpBtn = new Button("Top Up");
		Button cancelBtn = new Button("Cancel");

<<<<<<< HEAD
		topUpBtn.setOnAction(e -> {
			String result = controller.topUpBalance(customerId, amountField.getText());
=======
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
>>>>>>> c8ca42ad331c582c33421fdb36bdbdab07ec9a56

			if ("SUCCESS".equals(result)) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Top up success");
				stage.close();
			} else {
				showAlert(Alert.AlertType.ERROR, "Error", result);
			}
		});

<<<<<<< HEAD
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
=======
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
>>>>>>> c8ca42ad331c582c33421fdb36bdbdab07ec9a56
