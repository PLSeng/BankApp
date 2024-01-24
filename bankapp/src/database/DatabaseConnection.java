package database;

import model.User;

import java.sql.*;
import java.text.DecimalFormat;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sql12677790";
    private static final String USER = "root";
    private static final String PASSWORD = "Lim078927401";
    private static Connection connection = null;
    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String username, String password) {
        String query = "SELECT * FROM db WHERE username = ? AND password = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                double balance = resultSet.getDouble("balance");
                return new User(id, username, password, balance, this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(String username) {
        String query = "SELECT * FROM db WHERE username = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                double balance = resultSet.getDouble("balance");
                String password = resultSet.getString("password");
                return new User(id, username, password, balance, this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void viewAllUsers() {
        String query = "SELECT id, username, balance FROM db";
        try (PreparedStatement statement = getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            String leftAlignFormat = "| %-4d | %-15s | %-10s |%n";

            System.out.format("+------+-----------------+------------+%n");
            System.out.format("| ID   | Username        | Balance    |%n");
            System.out.format("+------+-----------------+------------+%n");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                double balance = resultSet.getDouble("balance");
                String formattedBalance = decimalFormat.format(balance);
                System.out.format(leftAlignFormat, id, username, formattedBalance);
            }
            System.out.format("+------+-----------------+------------+%n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean transferMoney(User sender, String receiverUsername, double amount) {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false); // Start transaction

            // Check if sender has enough balance
            if (sender.getBalance() < amount) {
                return false;
            }

            // Update sender's balance
            String updateSenderQuery = "UPDATE db SET balance = balance - ? WHERE username = ?";
            try (PreparedStatement updateSenderStmt = conn.prepareStatement(updateSenderQuery)) {
                updateSenderStmt.setDouble(1, amount);
                updateSenderStmt.setString(2, sender.getUsername());
                int senderRowsAffected = updateSenderStmt.executeUpdate();

                // Update receiver's balance
                String updateReceiverQuery = "UPDATE db SET balance = balance + ? WHERE username = ?";
                try (PreparedStatement updateReceiverStmt = conn.prepareStatement(updateReceiverQuery)) {
                    updateReceiverStmt.setDouble(1, amount);
                    updateReceiverStmt.setString(2, receiverUsername);
                    int receiverRowsAffected = updateReceiverStmt.executeUpdate();

                    if (senderRowsAffected == 1 && receiverRowsAffected == 1) {
                        conn.commit(); // Commit transaction
                        return true;
                    } else {
                        conn.rollback(); // Rollback transaction in case of error
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on exception
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public boolean addUser(String username, String password) {
        String query = "INSERT INTO db (username, password, balance) VALUES (?, ?, 0.00)";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password); // In a real application, password should be hashed
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.print("");
        }
        return false;
    }

    public boolean deleteUser(String username) {
        String query = "DELETE FROM db WHERE username = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, username);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserPassword(String username, String newPassword) {
        String query = "UPDATE db SET password = ? WHERE username = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBalanceDeposit(int userId, double amount) {
        String query = "UPDATE db SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBalanceWithdraw(int userId, double amount) {
        String query = "UPDATE db SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, userId);
            statement.setDouble(3, amount); // Ensures sufficient balance for withdrawal
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
