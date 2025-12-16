package view.Admin;

import controller.AdminOrderController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model_entity.OrderHeader;

import java.util.List;

public class AdminOrdersDashboardView {

    private TableView<OrderHeader> table;
    
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Manage Orders");

        table = new TableView<>();

        TableColumn<OrderHeader, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getIdOrder())
        );

        TableColumn<OrderHeader, String> custCol = new TableColumn<>("Customer");
        custCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getIdCustomer())
        );

        TableColumn<OrderHeader, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus())
        );

        TableColumn<OrderHeader, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("Rp%,.0f", c.getValue().getTotalAmount())
            )
        );

        TableColumn<OrderHeader, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(c -> new TableCell<>() {

            private final Button acceptBtn = new Button("Accept");
            private final Button cancelBtn = new Button("Cancel");

            {
                acceptBtn.setStyle("-fx-background-color:#2e7d32;-fx-text-fill:white;");
                cancelBtn.setStyle("-fx-background-color:#c62828;-fx-text-fill:white;");

                acceptBtn.setOnAction(e -> {
                    OrderHeader order = getTableView().getItems().get(getIndex());

                    if (AdminOrderController.acceptOrder(order.getIdOrder())) {
                        showAlert("Success", "Order accepted");
                        loadData();
                    } else {
                        showAlert("Error", "Failed to accept order");
                    }
                });

                cancelBtn.setOnAction(e -> {
                    OrderHeader order = getTableView().getItems().get(getIndex());

                    if (AdminOrderController.cancelOrder(order.getIdOrder())) {
                        showAlert("Success", "Order cancelled & refunded");
                        loadData();
                    } else {
                        showAlert("Error", "Failed to cancel order");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new VBox(5, acceptBtn, cancelBtn));
            }
        });

        table.getColumns().addAll(idCol, custCol, statusCol, totalCol, actionCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadData();

        VBox root = new VBox(10, table);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 800, 450));
        stage.show();
    }

    private void loadData() {
        List<OrderHeader> orders = AdminOrderController.getPaidOrders();

        if (orders.isEmpty()) {
            table.setItems(FXCollections.observableArrayList());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Tidak ada order dengan status PAID");
            alert.showAndWait();
            return;
        }

        ObservableList<OrderHeader> data =
            FXCollections.observableArrayList(orders);
        table.setItems(data);
    }
    
}
