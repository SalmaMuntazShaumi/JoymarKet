package model_entity;

import java.time.LocalDateTime;

public class Notification {
    private String idNotification;
    private String idCustomer;
    private String message;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(String idNotification, String idCustomer,
                        String message, LocalDateTime createdAt) {
        this.idNotification = idNotification;
        this.idCustomer = idCustomer;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getIdNotification() { return idNotification; }
    public void setIdNotification(String idNotification) { this.idNotification = idNotification; }

    public String getIdCustomer() { return idCustomer; }
    public void setIdCustomer(String idCustomer) { this.idCustomer = idCustomer; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
