package model_entity;

public class Delivery {

    private String idOrder;
    private String idCourier;
    private String status;

    // tambahan untuk dashboard courier
    private String customerName;
    private String customerAddress;

    // ================= CONSTRUCTOR =================

    public Delivery() {}

    // constructor lama (JANGAN DIHAPUS)
    public Delivery(String idOrder, String idCourier, String status) {
        this.idOrder = idOrder;
        this.idCourier = idCourier;
        this.status = status;
    }

    // constructor baru (untuk dashboard courier)
    public Delivery(
        String idOrder,
        String status,
        String customerName,
        String customerAddress
    ) {
        this.idOrder = idOrder;
        this.status = status;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
    }

    // ================= GETTER SETTER =================

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getIdCourier() {
        return idCourier;
    }

    public void setIdCourier(String idCourier) {
        this.idCourier = idCourier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
}
