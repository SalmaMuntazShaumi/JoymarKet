package view.Courier;

import controller.CourierDeliveryController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model_entity.Delivery;
import view.Auth.LoginView;

public class CourierDashboardView {

    private final String courierId;
    private final TableView<Delivery> table = new TableView<>();
    private final ComboBox<String> filterBox = new ComboBox<>();

    public CourierDashboardView(String courierId) {
        this.courierId = courierId;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Courier Dashboard");

        setupFilter();
        setupTable();
        loadData();
        
        Button logoutBtn = new Button("Logout");
        
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView();
            Stage loginStage = new Stage();
            loginView.start(loginStage);
            stage.close();
        });

        VBox root = new VBox(12, filterBox, table, logoutBtn);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 900, 520));
        stage.show();
    }

    // ================= FILTER =================
    private void setupFilter() {
        filterBox.getItems().addAll(
                "ALL",
                "assigned",
                "onDelivery",
                "completed"
        );
        filterBox.setValue("assigned");

        filterBox.setOnAction(e -> loadData());
    }

    // ================= TABLE =================
    private void setupTable() {

        TableColumn<Delivery, String> orderCol = new TableColumn<>("Order ID");
        orderCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getIdOrder()));

        TableColumn<Delivery, String> custCol = new TableColumn<>("Customer");
        custCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCustomerName()));

        TableColumn<Delivery, String> addrCol = new TableColumn<>("Address");
        addrCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCustomerAddress()));

        TableColumn<Delivery, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getStatus()));

        TableColumn<Delivery, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button acceptBtn = new Button("Accept");
            private final Button receivedBtn = new Button("Received");

            {
                acceptBtn.setStyle(
                        "-fx-background-color:#1976d2; -fx-text-fill:white;"
                );
                receivedBtn.setStyle(
                        "-fx-background-color:#388e3c; -fx-text-fill:white;"
                );

                acceptBtn.setOnAction(e -> {
                    Delivery d = getTableView().getItems().get(getIndex());
                    CourierDeliveryController.acceptDelivery(
                            d.getIdOrder(),
                            courierId
                    );
                    loadData();
                });

                receivedBtn.setOnAction(e -> {
                    Delivery d = getTableView().getItems().get(getIndex());
                    CourierDeliveryController.completeDelivery(d.getIdOrder());
                    loadData();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Delivery d = getTableView().getItems().get(getIndex());
                String status = d.getStatus(); // SUDAH SESUAI ENUM DB

                if ("assigned".equals(status)) {
                    setGraphic(acceptBtn);
                } else if ("onDelivery".equals(status)) {
                    setGraphic(receivedBtn);
                } else {
                    setGraphic(null);
                }
            }
        });

        table.getColumns().addAll(
                orderCol,
                custCol,
                addrCol,
                statusCol,
                actionCol
        );

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ================= LOAD =================
    private void loadData() {
        String status = filterBox.getValue();

        if ("ALL".equals(status)) {
            status = null;
        }

        table.setItems(FXCollections.observableArrayList(
                CourierDeliveryController.getDeliveries(courierId, status)
        ));
    }
}