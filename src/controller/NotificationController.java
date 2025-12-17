package controller;

import model.NotificationModel;
import model_entity.Notification;

import java.util.List;

public class NotificationController {

    public List<Notification> getByCustomer(String idCustomer) {
        return NotificationModel.getByCustomer(idCustomer);
    }

    public void markAsRead(String idNotification) {
        NotificationModel.markAsRead(idNotification);
    }
}
