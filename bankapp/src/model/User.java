package model;


import database.DatabaseConnection;

public class User {
    private int id;
    private String username;
    private String password;
    private double balance;
    private DatabaseConnection dbConnection;

    public User(int id, String username, String password, double balance, DatabaseConnection dbConnection) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.dbConnection = dbConnection;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount > 0 && dbConnection.updateBalanceDeposit(this.id, amount)) {
            this.balance += amount;
        } else {
            System.out.println("Invalid deposit amount or database update failed.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= this.balance && dbConnection.updateBalanceWithdraw(this.id, amount)) {
            this.balance -= amount;
            return true;
        } else {
            return false;
        }
    }

    public boolean transfer(String receiverUsername, double amount) {
        if (amount <= 0 || amount > this.balance) {
            System.out.println("Invalid transfer amount or insufficient funds.");
            return false;
        }

        boolean transferSuccess = dbConnection.transferMoney(this, receiverUsername, amount);
        if (transferSuccess) {
            this.balance -= amount; // Update sender's balance
            return true;
        } else {
            return false;
        }
    }

    public void setPassword(String password) {
        if (dbConnection.updateUserPassword(this.username, password)) {
            this.password = password;
        } else {
            System.out.println("Password update failed.");
        }
    }
}
