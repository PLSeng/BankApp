package com.ams.bankapp2.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LogEntry {
    private int id;
    private String username;
    private String typeOfTransaction;
    private BigDecimal amount;
    private LocalDateTime dateTime;

    // Constructors
    public LogEntry() {
    }

    public LogEntry(int id, String username, String typeOfTransaction, BigDecimal amount, LocalDateTime dateTime) {
        this.id = id;
        this.username = username;
        this.typeOfTransaction = typeOfTransaction;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    // Getters and Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTypeOfTransaction() {
        return typeOfTransaction;
    }

    public void setTypeOfTransaction(String typeOfTransaction) {
        this.typeOfTransaction = typeOfTransaction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTime(String dateTime) {
        return this.dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", typeOfTransaction='" + typeOfTransaction + '\'' +
                ", amount=" + amount +
                ", dateTime=" + dateTime +
                '}';
    }
}

