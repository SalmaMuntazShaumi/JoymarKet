package model_entity;

import java.time.LocalDateTime;

public class Notification {

    private String idNotification;
    private String idCustomer;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(String idNotification,
                        String idCustomer,
                        String message,
                        boolean isRead,
                        LocalDateTime createdAt) {
        this.idNotification = idNotification;
        this.idCustomer = idCustomer;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
