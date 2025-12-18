package view.Customer;

import controller.OrderController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model_entity.OrderHeader;
import model_entity.OrderDetail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderHistoryView {
    
    private Stage stage;
    private String customerId;
    private OrderController orderController;
    
    private TableView<OrderHeader> orderTable;
    private TableView<OrderDetail> detailTable;
    
    private ObservableList<OrderHeader> orders;
    private ObservableList<OrderDetail> orderDetails;
    
    public OrderHistoryView(String customerId) {
        this.customerId = customerId;
        this.orderController = OrderController.getInstance();
        this.orders = FXCollections.observableArrayList();
        this.orderDetails = FXCollections.observableArrayList();
        
        initUI();
        loadOrders();
    }
    
    private void initUI() {
        stage = new Stage();
        stage.setTitle("Order History");
        
        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Top: Title
        Label titleLabel = new Label("Your Order History");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        HBox topBox = new HBox(titleLabel);
        topBox.setPadding(new Insets(0, 0, 10, 0));
        
        // Left: Orders table
        orderTable = createOrderTable();
        VBox leftBox = new VBox(10, new Label("Orders"), orderTable);
        leftBox.setPrefWidth(500);
        
        // Right: Order details
        detailTable = createDetailTable();
        VBox rightBox = new VBox(10, new Label("Order Details"), detailTable);
        
        // Bottom: Buttons
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadOrders());
        
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> stage.close());
        
        HBox bottomBox = new HBox(10, refreshBtn, closeBtn);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        
        // SplitPane for tables
        SplitPane splitPane = new SplitPane(leftBox, rightBox);
        splitPane.setDividerPositions(0.5);
        
        root.setTop(topBox);
        root.setCenter(splitPane);
        root.setBottom(bottomBox);
        
        stage.setScene(new Scene(root, 1000, 600));
    }
    
    private TableView<OrderHeader> createOrderTable() {
        TableView<OrderHeader> table = new TableView<>();
        
        // Columns using lambda expressions
        TableColumn<OrderHeader, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdOrder())
        );
        idCol.setPrefWidth(120);
        
        TableColumn<OrderHeader, String> dateCol = new TableColumn<>("Order Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getOrderedAt();
            String formatted = date != null 
                ? date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                : "";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        dateCol.setPrefWidth(150);
        
        TableColumn<OrderHeader, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus())
        );
        statusCol.setPrefWidth(100);
        
        statusCol.setCellFactory(col -> new TableCell<OrderHeader, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toUpperCase());
                    // Color coding for statuses from both OrderHeader and Delivery
                    switch (status.toLowerCase()) {
                        // Original OrderHeader statuses
                        case "pending":
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        case "paid":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "processing":
                            setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            break;
                        case "cancelled":
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                        // New Delivery statuses
                        case "waiting":
                            setStyle("-fx-text-fill: darkorange; -fx-font-weight: bold;");
                            break;
                        case "assigned":
                        case "ondelivery":
                            setStyle("-fx-text-fill: dodgerblue; -fx-font-weight: bold;");
                            break;
                        case "completed":
                            setStyle("-fx-text-fill: limegreen; -fx-font-weight: bold;");
                            break;
                        case "failed":
                            setStyle("-fx-text-fill: crimson; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: gray;");
                    }
                }
            }
        });
        
        TableColumn<OrderHeader, String> amountCol = new TableColumn<>("Total Amount");
        amountCol.setCellValueFactory(cellData -> {
            double amount = cellData.getValue().getTotalAmount();
            return new javafx.beans.property.SimpleStringProperty(
                String.format("Rp%,.0f", amount)
            );
        });
        amountCol.setPrefWidth(120);
        
        table.getColumns().addAll(idCol, dateCol, statusCol, amountCol);
        
        // Row selection listener
        table.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadOrderDetails(newSelection.getIdOrder());
                }
            }
        );
        
        return table;
    }
    
    private TableView<OrderDetail> createDetailTable() {
        TableView<OrderDetail> table = new TableView<>();
        
        // Columns using lambda expressions
        TableColumn<OrderDetail, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProductName())
        );
        productCol.setPrefWidth(200);
        
        TableColumn<OrderDetail, String> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getQty())
            )
        );
        qtyCol.setPrefWidth(80);
        
        TableColumn<OrderDetail, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> {
            double price = cellData.getValue().getPrice();
            return new javafx.beans.property.SimpleStringProperty(
                String.format("Rp%,.0f", price)
            );
        });
        priceCol.setPrefWidth(120);
        
        TableColumn<OrderDetail, String> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(cellData -> {
            double subtotal = cellData.getValue().getSubtotal();
            return new javafx.beans.property.SimpleStringProperty(
                String.format("Rp%,.0f", subtotal)
            );
        });
        subtotalCol.setPrefWidth(120);
        
        table.getColumns().addAll(productCol, qtyCol, priceCol, subtotalCol);
        
        return table;
    }
    
    private void loadOrders() {
        List<OrderHeader> orderList = orderController.getCustomerOrders(customerId);
        orders.setAll(orderList);
        orderTable.setItems(orders);
        
        // Clear details
        orderDetails.clear();
        detailTable.setItems(orderDetails);
        
        if (orderList.isEmpty()) {
            showAlert("Information", "No orders found.");
        }
    }
    
    private void loadOrderDetails(String orderId) {
        List<OrderDetail> details = orderController.getOrderDetails(orderId);
        orderDetails.setAll(details);
        detailTable.setItems(orderDetails);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}