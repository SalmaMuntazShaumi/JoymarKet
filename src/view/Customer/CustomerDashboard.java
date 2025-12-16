package view.Customer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import model_entity.Product;
import model_entity.User;
import model_entity.Notification;

import controller.ProductController;
import controller.CustomerController;
import controller.AuthController;
import controller.NotificationController;

import view.EditProfile;

public class CustomerDashboard extends Application {

    // ================== CONTROLLERS ==================
    private ProductController productController;
    private CustomerController customerController;
    private AuthController authController;
    private NotificationController notificationController;

    // ================== USER ==================
    private String currentCustomerId;
    private String currentCustomerName;

    // ================== UI ==================
    private Label balanceLabel;
    private Button notifBtn;
    private TableView<Product> productTable;
    private TextField searchField;

    private ObservableList<Notification> notifications =
            FXCollections.observableArrayList();

    // ================== START ==================
    public void start(Stage stage, String customerId, String customerName) {
        this.currentCustomerId = customerId;
        this.currentCustomerName = customerName;

        productController = ProductController.getInstance();
        customerController = new CustomerController();
        authController = new AuthController();
        notificationController = new NotificationController();

        initUI(stage);
        loadProducts();
        updateBalance();
        startNotificationPolling();
    }

    @Override
    public void start(Stage primaryStage) {
        start(primaryStage, "GUEST", "Guest");
    }

    // ================== UI SETUP ==================
    private void initUI(Stage stage) {

        // --------- TABLE ---------
        productTable = new TableView<>();
        setupProductTable();

        searchField = new TextField();
        searchField.setPromptText("Search products...");

        balanceLabel = new Label();

        notifBtn = new Button("Notifications");
        notifBtn.setStyle("-fx-background-color:#6a1b9a; -fx-text-fill:white;");
        notifBtn.setOnAction(e -> showNotificationCenter());

        Button cartBtn = new Button("Cart");
        cartBtn.setOnAction(e -> showCartView());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> {
            loadProducts();
            updateBalance();
        });

        Button editProfileBtn = new Button("Edit Profile");
        editProfileBtn.setOnAction(e -> showEditProfile());

        HBox topBar = new HBox(10,
                balanceLabel,
                notifBtn,
                searchField,
                refreshBtn,
                cartBtn,
                editProfileBtn
        );
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(10, topBar, productTable);
        root.setPadding(new Insets(15));

        stage.setTitle("Customer Dashboard - " + currentCustomerName);
        stage.setScene(new Scene(root, 900, 520));
        stage.show();
    }

    // ================== PRODUCT TABLE ==================
    private void setupProductTable() {

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getName()
        ));

        TableColumn<Product, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                "Rp " + String.format("%,.0f", c.getValue().getPrice())
        ));

        TableColumn<Product, String> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getStock())
        ));

        TableColumn<Product, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button addBtn = new Button("Add");

            {
                addBtn.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    showAddToCartDialog(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addBtn);
            }
        });

        productTable.getColumns().addAll(
                nameCol, priceCol, stockCol, actionCol
        );
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ================== DATA ==================
    private void loadProducts() {
        ObservableList<Product> products =
                FXCollections.observableArrayList(productController.getAllProducts());

        productTable.setItems(products);

        searchField.textProperty().addListener((obs, o, n) -> {
            if (n == null || n.isBlank()) {
                productTable.setItems(products);
            } else {
                productTable.setItems(
                        FXCollections.observableArrayList(
                                productController.searchProducts(n)
                        )
                );
            }
        });
    }

    private void updateBalance() {
        double balance = customerController.getCustomerBalance(currentCustomerId);
        balanceLabel.setText("Balance: Rp " + String.format("%,.0f", balance));
    }

    // ================== NOTIFICATION ==================
    private void startNotificationPolling() {

        Thread t = new Thread(() -> {
            while (true) {
                Platform.runLater(this::checkNotifications);
                try {
                    Thread.sleep(5000); // 5 detik
                } catch (InterruptedException ignored) {}
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void checkNotifications() {
        var list = notificationController.getByCustomer(currentCustomerId);

        if (list.size() > notifications.size()) {
            Notification newest = list.get(0);
            showPopup(newest.getMessage());
        }

        notifications.setAll(list);

        long unread = list.stream().filter(n -> !n.isRead()).count();
        notifBtn.setText("Notifications (" + unread + ")");
    }

    private void showPopup(String message) {
        Popup popup = new Popup();

        Label label = new Label(message);
        label.setStyle(
                "-fx-background-color:#333;" +
                "-fx-text-fill:white;" +
                "-fx-padding:10;" +
                "-fx-background-radius:8;"
        );

        popup.getContent().add(label);
        popup.setAutoHide(true);
        popup.show(productTable.getScene().getWindow());

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    private void showNotificationCenter() {
        Stage stage = new Stage();
        stage.setTitle("Notifications");

        ListView<Notification> listView = new ListView<>(notifications);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Notification n, boolean empty) {
                super.updateItem(n, empty);
                if (empty || n == null) {
                    setText(null);
                } else {
                    setText(n.getMessage());
                    setStyle(n.isRead()
                            ? "-fx-text-fill:black;"
                            : "-fx-font-weight:bold;");
                }
            }
        });

        listView.setOnMouseClicked(e -> {
            Notification n = listView.getSelectionModel().getSelectedItem();
            if (n != null && !n.isRead()) {
                notificationController.markAsRead(n.getIdNotification());
                checkNotifications();
            }
        });

        VBox root = new VBox(listView);
        root.setPadding(new Insets(10));

        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    // ================== NAVIGATION ==================
    private void showAddToCartDialog(Product p) {
        // implement milikmu
    }

    private void showCartView() {
        // implement milikmu
    }

    private void showEditProfile() {
        try {
            User u = authController.getUserById(currentCustomerId);
            new EditProfile(u).show();
        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
