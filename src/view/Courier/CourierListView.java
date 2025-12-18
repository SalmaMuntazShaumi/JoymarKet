package view.Courier;

import controller.CourierController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model_entity.Courier;

import java.util.List;

public class CourierListView {
    
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Courier List");
        
        // Create table
        TableView<Courier> courierTable = new TableView<>();
        ObservableList<Courier> courierData = FXCollections.observableArrayList();
        
        // Load data
        CourierController controller = new CourierController();
        List<Courier> couriers = controller.getAllCouriers();
        
        if (couriers != null) {
            courierData.addAll(couriers);
            System.out.println("Successfully loaded " + couriers.size() + " couriers");
        } else {
            System.out.println("No couriers returned from controller");
        }
        
        // Setup columns using lambda expressions
        TableColumn<Courier, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> {
            String id = cellData.getValue().getIdUser();
            return new javafx.beans.property.SimpleStringProperty(id != null ? id : "N/A");
        });
        idCol.setPrefWidth(80);
        
        TableColumn<Courier, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> {
            String name = cellData.getValue().getFullName();
            return new javafx.beans.property.SimpleStringProperty(name != null ? name : "");
        });
        nameCol.setPrefWidth(150);
        
        TableColumn<Courier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getEmail();
            return new javafx.beans.property.SimpleStringProperty(email != null ? email : "");
        });
        emailCol.setPrefWidth(200);
        
        TableColumn<Courier, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> {
            String phone = cellData.getValue().getPhone();
            return new javafx.beans.property.SimpleStringProperty(phone != null ? phone : "");
        });
        phoneCol.setPrefWidth(120);
        
        TableColumn<Courier, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(cellData -> {
            String vehicle = cellData.getValue().getVehicleType();
            return new javafx.beans.property.SimpleStringProperty(vehicle != null ? vehicle : "");
        });
        vehicleCol.setPrefWidth(120);
        
        // Add columns to table
        courierTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, vehicleCol);
        courierTable.setItems(courierData);
        
        // If no couriers
        if (courierData.isEmpty()) {
            courierTable.setPlaceholder(new Label("No couriers available in the database"));
        }
        
        // Add refresh button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> {
            courierData.clear();
            List<Courier> refreshed = controller.getAllCouriers();
            if (refreshed != null) {
                courierData.addAll(refreshed);
            }
        });
        
        // Layout
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
            new Label("Courier List") {{
                setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            }},
            courierTable,
            refreshBtn
        );
        
        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }
}