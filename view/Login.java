package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;
import service.CustomerService;

public class Login {
	
	private CustomerService service = new CustomerService();

    public void start(Stage stage) {
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");

        Label info = new Label();

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Go to Register");

        loginBtn.setOnAction(e -> {
            Customer c = service.login(emailField.getText(), passField.getText());
            if (c == null) info.setText("Invalid login!");
            else new EditProfile(service, c).start(stage);
        });

        registerBtn.setOnAction(e -> {
            new Register(service).start(stage);
        });

        VBox root = new VBox(10, emailField, passField, loginBtn, registerBtn, info);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 300));
        stage.show();
    }

}
