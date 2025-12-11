package model;

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
    
    public void topUpBalance(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }
    
    public boolean deductBalance(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }
}