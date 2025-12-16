package view.Customer;

import controller.NotificationController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model_entity.Notification;

public class NotificationCenterView {

    private final String customerId;
    private final TableView<Notification> table = new TableView<>();
    private final NotificationController controller = new NotificationController();

    public NotificationCenterView(String customerId) {
        this.customerId = customerId;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Notifications");

        setupTable();
        loadData();

        VBox root = new VBox(10, table);
        root.setPadding(new Insets(15));

        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    // ================= TABLE =================
    private void setupTable() {

        TableColumn<Notification, String> msgCol =
                new TableColumn<>("Message");
        msgCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getMessage()
                ));

        TableColumn<Notification, String> timeCol =
                new TableColumn<>("Time");
        timeCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getCreatedAt().toString()
                ));

        TableColumn<Notification, String> statusCol =
                new TableColumn<>("Status");
        statusCol.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().isRead() ? "READ" : "UNREAD"
                ));

        TableColumn<Notification, Void> actionCol =
                new TableColumn<>("Action");

        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button readBtn = new Button("Mark Read");

            {
                readBtn.setOnAction(e -> {
                    Notification n =
                            getTableView().getItems().get(getIndex());

                    controller.markAsRead(n.getIdNotification());
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

                Notification n =
                        getTableView().getItems().get(getIndex());

                setGraphic(n.isRead() ? null : readBtn);
            }
        });

        table.getColumns().addAll(
                msgCol, timeCol, statusCol, actionCol
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }

    // ================= LOAD =================
    private void loadData() {
        table.setItems(
                FXCollections.observableArrayList(
                        controller.getByCustomer(customerId)
                )
        );
    }
}
