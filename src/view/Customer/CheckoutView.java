package view.Customer;

import controller.CheckoutController;
import controller.PromoController;
import controller.CustomerController;
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
    private CustomerController customerController;

    private Label subtotalLabel;
    private Label discountLabel;
    private Label totalLabel;
    private Label balanceLabel;
    private Label statusLabel;
    private ComboBox<Promo> promoBox;
    private Button payBtn;
    
    private double subtotalAmount;
    private double customerBalance;

    public CheckoutView(String customerId, List<CartItem> items) {
        this.customerId = customerId;
        this.items = items;
        this.customerController = new CustomerController();
        initUI();
    }

    private void initUI() {
        stage = new Stage();
        stage.setTitle("Checkout - Payment");

        Label title = new Label("Order Summary");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Order items list
        VBox list = new VBox(5);
        for (CartItem i : items) {
            list.getChildren().add(
                new Label("• " + i.getProduct().getName() +
                " x" + i.getCount() +
                " = Rp" + String.format("%,.0f", i.getTotalPrice()))
            );
        }

        // Calculate subtotal
        subtotalAmount = items.stream()
            .mapToDouble(CartItem::getTotalPrice).sum();

        // Get customer balance
        customerBalance = customerController.getCustomerBalance(customerId);
        
        // Create labels
        subtotalLabel = new Label("Subtotal : Rp" + String.format("%,.0f", subtotalAmount));
        discountLabel = new Label("Discount : Rp0");
        totalLabel = new Label("Total : Rp" + String.format("%,.0f", subtotalAmount));
        totalLabel.setStyle("-fx-font-weight: bold;");
        
        balanceLabel = new Label("Your Balance : Rp" + String.format("%,.0f", customerBalance));
        balanceLabel.setStyle("-fx-font-weight: bold;");
        
        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-weight: bold;");

        // Promo selection
        promoBox = new ComboBox<>();
        promoBox.setItems(FXCollections.observableArrayList(
            new PromoController().getAllPromos()
        ));
        promoBox.setPromptText("Select Promo (Optional)");
        promoBox.setOnAction(e -> updatePaymentInfo());

        // Pay button
        payBtn = new Button("Pay Now");
        payBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        payBtn.setOnAction(e -> processPayment());
        
        // Update initial status
        updatePaymentInfo();

        // Layout
        VBox root = new VBox(15,
            title, 
            new Separator(),
            list,
            new Separator(),
            promoBox,
            new Separator(),
            subtotalLabel,
            discountLabel,
            totalLabel,
            new Separator(),
            balanceLabel,
            statusLabel,
            new Separator(),
            payBtn
        );
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        stage.setScene(new Scene(root, 450, 550));
    }

    private void updatePaymentInfo() {
        Promo p = promoBox.getValue();
        double discount = p == null ? 0 : subtotalAmount * p.getDiscountPercentage() / 100;
        double total = subtotalAmount - discount;
        
        discountLabel.setText("Discount : Rp" + String.format("%,.0f", discount));
        totalLabel.setText("Total : Rp" + String.format("%,.0f", total));
        
        // Update payment status
        if (customerBalance >= total) {
            statusLabel.setText("✓ Sufficient balance");
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            totalLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
            payBtn.setDisable(false);
            payBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            double shortage = total - customerBalance;
            statusLabel.setText("✗ Insufficient balance (Short: Rp" + String.format("%,.0f", shortage) + ")");
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            totalLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            payBtn.setDisable(true);
            payBtn.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
        }
    }

    private void processPayment() {
        // Calculate final total
        Promo p = promoBox.getValue();
        double discount = p == null ? 0 : subtotalAmount * p.getDiscountPercentage() / 100;
        double finalTotal = subtotalAmount - discount;
        
        // Final balance check
        if (customerBalance < finalTotal) {
            showBalanceError(customerBalance, finalTotal);
            return;
        }
        
        // Confirm payment
        boolean confirm = showConfirmationDialog(finalTotal, customerBalance);
        if (!confirm) {
            return;
        }
        
        // Process checkout
        boolean success = CheckoutController.checkout(
            customerId,
            p == null ? null : p.getIdPromo(),
            items
        );
        
        if (success) {
            showSuccessDialog(finalTotal, customerBalance - finalTotal);
            stage.close();
        } else {
            showErrorDialog();
        }
    }
    
    private void showBalanceError(double balance, double total) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Insufficient Balance");
        alert.setHeaderText("Cannot Complete Payment");
        alert.setContentText(
            "Payment Failed!\n\n" +
            "Your balance is insufficient:\n" +
            "Required: Rp" + String.format("%,.0f", total) + "\n" +
            "Available: Rp" + String.format("%,.0f", balance) + "\n" +
            "Shortage: Rp" + String.format("%,.0f", (total - balance)) + "\n\n" +
            "Please top up your balance or reduce your order."
        );
        alert.showAndWait();
    }
    
    private boolean showConfirmationDialog(double total, double balance) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Payment");
        confirm.setHeaderText("Payment Authorization");
        confirm.setContentText(
            "Please confirm this payment:\n\n" +
            "Amount: Rp" + String.format("%,.0f", total) + "\n" +
            "Current Balance: Rp" + String.format("%,.0f", balance) + "\n" +
            "New Balance: Rp" + String.format("%,.0f", (balance - total)) + "\n\n" +
            "Do you want to proceed with the payment?"
        );
        
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        
        ButtonType result = confirm.showAndWait().orElse(ButtonType.NO);
        return result == ButtonType.YES;
    }
    
    private void showSuccessDialog(double amount, double newBalance) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Payment Successful");
        alert.setHeaderText("✅ Order Confirmed!");
        alert.setContentText(
            "Payment of Rp" + String.format("%,.0f", amount) + " completed successfully!\n\n" +
            "Your order is being processed.\n" +
            "Your new balance: Rp" + String.format("%,.0f", newBalance) + "\n\n" +
            "Thank you for your purchase!"
        );
        alert.showAndWait();
    }
    
    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Payment Failed");
        alert.setHeaderText("❌ Transaction Error");
        alert.setContentText(
            "We couldn't process your payment.\n" +
            "Please try again or contact customer support."
        );
        alert.showAndWait();
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}