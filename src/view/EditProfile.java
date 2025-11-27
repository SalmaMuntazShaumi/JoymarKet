package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;
import service.CustomerService;

public class EditProfile {

	private CustomerService service;
    private Customer customer;

    public EditProfile(CustomerService service, Customer customer) {
        this.service = service;
        this.customer = customer;
    }

    public void start(Stage stage) {

        TextField name = new TextField(customer.getFullName());
        TextField phone = new TextField(customer.getPhone());
        TextField address = new TextField(customer.getAddress());

        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("Male", "Female");
        gender.setValue(customer.getGender());

        Label info = new Label();

        Button saveBtn = new Button("Save Changes");
        Button logoutBtn = new Button("Logout");

        saveBtn.setOnAction(e -> {
            customer.setFullName(name.getText());
            customer.setPhone(phone.getText());
            customer.setAddress(address.getText());
            customer.setGender(gender.getValue());

            info.setText("Profile Updated!");
        });

        logoutBtn.setOnAction(e -> new Login().start(stage));

        VBox root = new VBox(10,
                new Label("Edit Profile"),
                name, phone, address, gender,
                saveBtn, logoutBtn, info
        );
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 400, 350));
        stage.show();
    }
    
}
