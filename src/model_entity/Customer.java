package model_entity;

public class Customer extends User {
    private double balance;
    
    public Customer() {
        super();
        this.setRole("customer");
    }
    
    public Customer(String idUser, String fullName, String email, String password,
                   String phone, String address, String gender) {
        super(idUser, fullName, email, password, phone, address, "customer", gender);
        this.balance = 0.0;
    }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public boolean canAfford(double amount) {
        return amount > 0 && this.balance >= amount;
    }
}