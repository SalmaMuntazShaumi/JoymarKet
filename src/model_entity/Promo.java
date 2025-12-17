package model_entity;

public class Promo {
    private String idPromo;
    private String code;
    private String headline;
    private int discountPercentage;

    public Promo() {}

    public Promo(String idPromo, String code, String headline, int discountPercentage) {
        this.idPromo = idPromo;
        this.code = code;
        this.headline = headline;
        this.discountPercentage = discountPercentage;
    }

    public String getIdPromo() { return idPromo; }
    public void setIdPromo(String idPromo) { this.idPromo = idPromo; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String toString() {
        return code + " - " + discountPercentage + "% (" + headline + ")";
    }
}
