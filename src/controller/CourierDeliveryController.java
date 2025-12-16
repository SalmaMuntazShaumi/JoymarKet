package controller;

import java.util.List;
import model.DeliveryModel;
import model_entity.Delivery;

public class CourierDeliveryController {

    public static List<Delivery> getDeliveries(String courierId, String status) {
        return DeliveryModel.getCourierDeliveries(courierId, status);
    }

    public static boolean acceptDelivery(String orderId, String courierId) {
        return DeliveryModel.courierAccept(orderId, courierId);
    }

    public static boolean completeDelivery(String orderId) {
        return DeliveryModel.completeDelivery(orderId);
    }
}

