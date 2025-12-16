package controller;

import model.OrderModel;
import model.PromoModel;
import model_entity.CartItem;
import model_entity.Promo;

import java.util.List;

public class CheckoutController {

	public static boolean checkout(
	        String idCustomer,
	        String idPromo,
	        List<CartItem> items
	) {
	    double total = 0;
	    for (CartItem item : items) {
	        total += item.getTotalPrice();
	    }

	    if (idPromo != null) {
	        Promo promo = PromoModel.getPromoById(idPromo);
	        if (promo != null) {
	            total -= total * promo.getDiscountPercentage() / 100.0;
	        }
	    }

	    String orderId = OrderModel.createOrder(idCustomer, idPromo, items);
	    if (orderId == null) return false;

	    if (!OrderModel.createOrderDetails(orderId, items)) return false;

	    return OrderModel.payOrder(orderId, idCustomer, total, items);
	}

}
