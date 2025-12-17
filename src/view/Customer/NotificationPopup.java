package view.Customer;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.util.Duration;

public class NotificationPopup {

    public static void show(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText("New Notification");
        alert.setContentText(message);
        alert.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(4));
        delay.setOnFinished(e -> alert.close());
        delay.play();
    }
}
