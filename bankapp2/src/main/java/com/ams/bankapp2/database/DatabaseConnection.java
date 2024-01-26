package com.ams.bankapp2.database;

import com.ams.bankapp2.model.LogEntry;
import com.ams.bankapp2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DatabaseConnection {

    private final JdbcTemplate jdbcTemplate;
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    @Autowired
    public DatabaseConnection(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUser(String username, String password) {
        String query = "SELECT * FROM db WHERE username = ? AND password = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{username, password}, new UserRowMapper());
        } catch (Exception e) {
            return null;
        }
    }

    public User getUser(int userId) {
        String query = "SELECT * FROM db WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{userId}, new UserRowMapper());
        } catch (Exception e) {
            return null;
        }
    }

    public User getUser(String username) {
        String query = "SELECT * FROM db WHERE username = ?";
        return jdbcTemplate.queryForObject(query, new Object[]{username}, new UserRowMapper());
    }

    @Transactional
    public boolean transferMoney(User sender, String receiverUsername, double amount) {
        if (amount <= 0 || sender.getBalance() < amount) {
            return false; // Invalid amount or insufficient funds
        }

        String updateSenderQuery = "UPDATE db SET balance = balance - ? WHERE id = ? AND balance >= ?";
        String updateReceiverQuery = "UPDATE db SET balance = balance + ? WHERE username = ?";

        try {
            boolean receiverExists = checkIfUserExists(receiverUsername);

            if (!receiverExists) {
                return false; // Receiver does not exist
            }

            // Perform the transfer
            jdbcTemplate.update(updateReceiverQuery, amount, receiverUsername);
            jdbcTemplate.update(updateSenderQuery, amount, sender.getId(), amount);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean checkIfUserExists(String username) {
        String query = "SELECT COUNT(*) FROM db WHERE username = ?";

        int count = jdbcTemplate.queryForObject(query, Integer.class, username);
        return count > 0;
    }


    public boolean addUser(String username, String password) {
        String query = "INSERT INTO db (username, password, balance) VALUES (?, ?, 0.00)";
        try{
            int rowAffected = jdbcTemplate.update(query, username, password);
            return rowAffected > 0;
        }catch (Exception e){
            return false;
        }
    }

    public boolean deleteUser(String username) {
        String query = "DELETE FROM db WHERE username = ?";
        return jdbcTemplate.update(query, username) > 0;
    }

    public boolean updateUserPassword(String username, String newPassword) {
        String query = "UPDATE db SET password = ? WHERE username = ?";
        return jdbcTemplate.update(query, newPassword, username) > 0;
    }

    public boolean updateBalanceDeposit(int userId, double amount) {
        String query = "UPDATE db SET balance = balance + ? WHERE id = ?";
        return jdbcTemplate.update(query, amount, userId) > 0;
    }

    public boolean updateBalanceWithdraw(int userId, double amount) {
        String query = "UPDATE db SET balance = balance - ? WHERE id = ? AND balance >= ?";
        return jdbcTemplate.update(query, amount, userId, amount) > 0;
    }

    public List<User> getAllUsers() {
        String sql = "SELECT id, username, password, balance FROM db";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    private static final class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String password = rs.getString("password");
            double balance = rs.getDouble("balance");
            // The User class needs to be adapted to work with this setup
            return new User(id, username, password, balance);
        }
    }

    public boolean validatePassword(int userId, String password) {
        String query = "SELECT password FROM db WHERE id = ?";
        try {
            String storedPassword = jdbcTemplate.queryForObject(query, new Object[]{userId}, String.class);
            return storedPassword != null && storedPassword.equals(password);
        } catch (Exception e) {
            return false;
        }
    }

    public List<LogEntry> findByUsername(String username) {
        String sql = "SELECT * FROM log WHERE username = ?";
        return jdbcTemplate.query(sql, new Object[]{username}, new LogEntryRowMapper());
    }

    public class LogEntryRowMapper implements RowMapper<LogEntry> {

        @Override
        public LogEntry mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            LogEntry logEntry = new LogEntry();
            logEntry.setId(resultSet.getInt("id"));
            logEntry.setUsername(resultSet.getString("username"));
            logEntry.setTypeOfTransaction(resultSet.getString("type_of_transaction"));
            logEntry.setAmount(resultSet.getBigDecimal("amount"));
            logEntry.setDateTime(resultSet.getTimestamp("date_time").toLocalDateTime());
            return logEntry;
        }
    }

    public void addToLog(String username, double amount, String type){
        String insertLogSql = "INSERT INTO log (username, type_of_transaction, amount, date_time) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertLogSql, username, type, BigDecimal.valueOf(amount), LocalDateTime.now());
    }

    public void deleteUserLog(String username){
        String deleteLogSql = "DELETE FROM log WHERE username = ?";
        jdbcTemplate.update(deleteLogSql, username);
    }

}