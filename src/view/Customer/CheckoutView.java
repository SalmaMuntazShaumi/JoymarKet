package view.Customer;

import controller.CheckoutController;
import controller.PromoController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model_entity.CartItem;
import model_entity.Promo;

import java.util.List;

public class CheckoutView {

    private Stage stage;
    private String customerId;
    private List<CartItem> items;

    private Label subtotalLabel;
    private Label discountLabel;
    private Label totalLabel;
    private ComboBox<Promo> promoBox;

    public CheckoutView(String customerId, List<CartItem> items) {
        this.customerId = customerId;
        this.items = items;
        initUI();
    }

    private void initUI() {
        stage = new Stage();
        stage.setTitle("Checkout");

        Label title = new Label("Order Summary");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox list = new VBox(5);
        for (CartItem i : items) {
            list.getChildren().add(
                new Label(i.getProduct().getName() +
                " x" + i.getCount() +
                " = Rp" + String.format("%,.0f", i.getTotalPrice()))
            );
        }

        double subtotal = items.stream()
            .mapToDouble(CartItem::getTotalPrice).sum();

        subtotalLabel = new Label("Subtotal : Rp" + String.format("%,.0f", subtotal));
        discountLabel = new Label("Discount : Rp0");
        totalLabel = new Label("Total : Rp" + String.format("%,.0f", subtotal));

        promoBox = new ComboBox<>();
        promoBox.setItems(FXCollections.observableArrayList(
            new PromoController().getAllPromos()
        ));
        promoBox.setPromptText("Select Promo");
        promoBox.setOnAction(e -> updateTotal(subtotal));

        Button payBtn = new Button("Pay Now");
        payBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white;");
        payBtn.setOnAction(e -> pay(subtotal));

        VBox root = new VBox(10,
            title, list, promoBox,
            subtotalLabel, discountLabel, totalLabel, payBtn
        );
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");

        stage.setScene(new Scene(root, 420, 480));
    }

    private void updateTotal(double subtotal) {
        Promo p = promoBox.getValue();
        double disc = p == null ? 0 : subtotal * p.getDiscountPercentage() / 100;
        discountLabel.setText("Discount : Rp" + String.format("%,.0f", disc));
        totalLabel.setText("Total : Rp" + String.format("%,.0f", subtotal - disc));
    }

    private void pay(double subtotal) {
        Promo p = promoBox.getValue();
        boolean ok = CheckoutController.checkout(
            customerId,
            p == null ? null : p.getIdPromo(),
            items
        );

        Alert a = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setContentText(ok ? "Payment success" : "Payment failed");
        a.showAndWait();

        if (ok) stage.close();
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}
