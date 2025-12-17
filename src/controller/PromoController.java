package controller;

import model.PromoModel;
import model_entity.Promo;
import java.util.List;

public class PromoController {

    public List<Promo> getAllPromos() {
        return PromoModel.getAllPromos();
    }
}
