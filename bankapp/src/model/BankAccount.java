package model;

public class BankAccount {
    private int accountId;
    private double balance;

    public BankAccount(int accountId, double balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public boolean transfer(BankAccount receiverAccount, double amount) {
        if (withdraw(amount)) {
            receiverAccount.deposit(amount);
            return true;
        }
        return false;
    }

    public int getAccountId() {
        return accountId;
    }

    public double getBalance() {
        return balance;
    }
}
