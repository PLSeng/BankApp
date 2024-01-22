package com.ams.bankapp2.service;

import com.ams.bankapp2.database.DatabaseConnection;
import com.ams.bankapp2.model.User;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public UserService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public User authenticateUser(String username, String password) {
        return databaseConnection.getUser(username, password);
    }

    public boolean depositToUserAccount(int userId, double amount) {
        if (amount <= 0) {
            return false; // Amount must be positive
        }
        return databaseConnection.updateBalanceDeposit(userId, amount);
    }

    public boolean withdrawFromUserAccount(int userId, double amount) {
        if (amount <= 0) {
            return false; // Amount must be positive
        }
        return databaseConnection.updateBalanceWithdraw(userId, amount);
    }

    public boolean transferBetweenUsers(User sender, String receiverUsername, double amount) {
        return databaseConnection.transferMoney(sender, receiverUsername, amount);
    }

    public boolean registerNewUser(String username, String password) {
        return databaseConnection.addUser(username, password);
    }

    public User getUserById(int userId) {
        return databaseConnection.getUser(userId);
    }

    public boolean validatePassword(int id, String password) {
        return databaseConnection.validatePassword(id, password);
    }

    public boolean checkIfUserExists(String username) {
        return databaseConnection.checkIfUserExists(username);
    }

    public boolean deleteUser(String username) {
        return databaseConnection.deleteUser(username);
    }

    public boolean updateUserPassword(String username, String newPassword) {
        return databaseConnection.updateUserPassword(username, newPassword);
    }
}
