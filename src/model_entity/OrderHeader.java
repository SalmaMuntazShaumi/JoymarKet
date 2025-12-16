package model_entity;

import java.time.LocalDateTime;

public class OrderHeader {
    private String idOrder;
    private String idCustomer;
    private String idPromo;
    private String status; // PENDING, PAID
    private double totalAmount;
    private LocalDateTime orderedAt;

    public OrderHeader() {}

    public OrderHeader(String idOrder, String idCustomer, String idPromo,
                       String status, double totalAmount, LocalDateTime orderedAt) {
        this.idOrder = idOrder;
        this.idCustomer = idCustomer;
        this.idPromo = idPromo;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderedAt = orderedAt;
    }

    // Getters & Setters
    public String getIdOrder() { return idOrder; }
    public void setIdOrder(String idOrder) { this.idOrder = idOrder; }

    public String getIdCustomer() { return idCustomer; }
    public void setIdCustomer(String idCustomer) { this.idCustomer = idCustomer; }

    public String getIdPromo() { return idPromo; }
    public void setIdPromo(String idPromo) { this.idPromo = idPromo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderedAt() { return orderedAt; }
    public void setOrderedAt(LocalDateTime orderedAt) { this.orderedAt = orderedAt; }
}
