package view.Admin;

import controller.AdminOrderController;
import controller.CourierController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model_entity.Courier;
import model_entity.OrderHeader;

public class AdminOrdersDashboardView {

    private TableView<OrderHeader> paidTable = new TableView<>();
    private TableView<OrderHeader> assignTable = new TableView<>();

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Order Management - Admin");

        setupPaidTable();
        setupAssignTable();

        Label paidLabel = new Label("PAID Orders");
        paidLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label assignLabel = new Label("Assign Courier (PROCESSING Orders)");
        assignLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox root = new VBox(15,
                paidLabel,
                paidTable,
                new Separator(),
                assignLabel,
                assignTable
        );
        root.setPadding(new Insets(15));

        loadPaidOrders();
        loadProcessingOrders();

        stage.setScene(new Scene(root, 950, 650));
        stage.show();
    }

    // ================= PAID TABLE =================
    private void setupPaidTable() {
        paidTable.getColumns().addAll(
                col("Order ID", OrderHeader::getIdOrder, 120),
                col("Customer", OrderHeader::getIdCustomer, 150),
                col("Total", o -> String.format("Rp%,.0f", o.getTotalAmount()), 150),
                actionPaidCol()
        );
        paidTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private TableColumn<OrderHeader, Void> actionPaidCol() {
        TableColumn<OrderHeader, Void> col = new TableColumn<>("Actions");
        col.setMinWidth(180);

        col.setCellFactory(c -> new TableCell<>() {
            private final Button acceptBtn = new Button("Accept");
            private final Button cancelBtn = new Button("Cancel");
            private final HBox buttons = new HBox(8, acceptBtn, cancelBtn);

            {
                acceptBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 12px;");
                cancelBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 12px;");

                acceptBtn.setOnAction(e -> {
                    OrderHeader o = getTableView().getItems().get(getIndex());
                    AdminOrderController.acceptOrder(o.getIdOrder());
                    loadPaidOrders();
                    loadProcessingOrders();
                });

                cancelBtn.setOnAction(e -> {
                    OrderHeader o = getTableView().getItems().get(getIndex());
                    AdminOrderController.cancelOrder(o.getIdOrder());
                    loadPaidOrders();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        return col;
    }

    // ================= ASSIGN TABLE =================
    private void setupAssignTable() {
        assignTable.getColumns().addAll(
                col("Order ID", OrderHeader::getIdOrder, 200),
                assignCourierCol()
        );
        assignTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private TableColumn<OrderHeader, Void> assignCourierCol() {
        TableColumn<OrderHeader, Void> col = new TableColumn<>("Actions");
        col.setMinWidth(150);

        col.setCellFactory(c -> new TableCell<>() {
            private final Button assignBtn = new Button("Assign Courier");

            {
                assignBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 12px;");

                assignBtn.setOnAction(e -> {
                    OrderHeader o = getTableView().getItems().get(getIndex());

                    boolean assigned = showCourierDialog(o.getIdOrder());
                    if (assigned) {
                        loadProcessingOrders(); // AUTO REFRESH
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : assignBtn);
            }
        });
        return col;
    }

    // ================= DIALOG =================
    private boolean showCourierDialog(String orderId) {
        ChoiceDialog<Courier> dialog =
                new ChoiceDialog<>(null, new CourierController().getAllCouriers());

        dialog.setTitle("Assign Courier");
        dialog.setHeaderText("Order ID: " + orderId);
        dialog.setContentText("Select Courier:");

        return dialog.showAndWait().map(c -> {
            AdminOrderController.assignCourier(orderId, c.getIdUser());
            return true;
        }).orElse(false);
    }

    // ================= LOAD =================
    private void loadPaidOrders() {
        paidTable.setItems(FXCollections.observableArrayList(
                AdminOrderController.getPaidOrders()
        ));
    }

    private void loadProcessingOrders() {
        assignTable.setItems(FXCollections.observableArrayList(
                AdminOrderController.getProcessingOrders()
        ));
    }

    // ================= UTIL =================
    private TableColumn<OrderHeader, String> col(
            String title,
            java.util.function.Function<OrderHeader, String> mapper,
            int minWidth
    ) {
        TableColumn<OrderHeader, String> c = new TableColumn<>(title);
        c.setMinWidth(minWidth);
        c.setCellValueFactory(v ->
                new javafx.beans.property.SimpleStringProperty(mapper.apply(v.getValue()))
        );
        return c;
    }
}
