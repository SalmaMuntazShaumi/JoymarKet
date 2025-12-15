package controller;

import repository.CustomerRepository;

public class CustomerController {

    private CustomerRepository repo = new CustomerRepository();

    public boolean topUp(String Idcustomer, String amountStr) {
        if (amountStr.isEmpty()) return false;

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return false;
        }

        if (amount < 10000) return false;

        return repo.topUpBalance(Idcustomer, amount);
    }
}
