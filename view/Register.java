package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.CustomerService;

public class Register {
	
	private CustomerService service;

    public Register(CustomerService service) {
        this.service = service;
    }

    public void start(Stage stage) {

        TextField idField = new TextField(); idField.setPromptText("Customer ID");
        TextField nameField = new TextField(); nameField.setPromptText("Name");
        TextField emailField = new TextField(); emailField.setPromptText("Email");

        PasswordField passField = new PasswordField(); passField.setPromptText("Password");
        PasswordField confirmField = new PasswordField(); confirmField.setPromptText("Confirm Password");

        TextField phoneField = new TextField(); phoneField.setPromptText("Phone");
        TextField addressField = new TextField(); addressField.setPromptText("Address");

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female");

        Label info = new Label();
        Button regBtn = new Button("Register");
        Button backBtn = new Button("Back");

        regBtn.setOnAction(e -> {
            String msg = service.register(
                    idField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    passField.getText(),
                    confirmField.getText(),
                    phoneField.getText(),
                    addressField.getText(),
                    genderBox.getValue()
            );

            if (msg.equals("SUCCESS")) {
                info.setText("Register success!");
            } else {
                info.setText(msg);
            }
        });

        backBtn.setOnAction(e -> new Login().start(stage));

        VBox root = new VBox(10,
                idField, nameField, emailField,
                passField, confirmField,
                phoneField, addressField,
                genderBox, regBtn, backBtn, info
        );
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 400, 500));
        stage.show();
    }
}
