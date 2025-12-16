package controller;

import model.DeliveryModel;
import model.OrderModel;
import model_entity.OrderHeader;

import java.util.List;

public class AdminOrderController {

    public static boolean acceptOrder(String idOrder) {
        return OrderModel.adminAcceptOrder(idOrder);
    }

    public static boolean cancelOrder(String idOrder) {
        return OrderModel.adminCancelOrder(idOrder);
    }

    public static List<OrderHeader> getPaidOrders() {
        return OrderModel.getOrdersByStatus("PAID");
    }
    public static List<OrderHeader> getProcessingOrders() {
        return OrderModel.getProcessingOrders();
    }
    public static boolean assignCourier(String idOrder, String idCourier) {
        return DeliveryModel.assignCourier(idOrder, idCourier);
    }
}
