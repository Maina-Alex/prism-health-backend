package com.prismhealth.Models;

import java.util.List;

public class EmailData {

    private String name;
    private String email;
    private long code;
    private String carName;

    private double amount;
    private String receipt;
    private List<EmailDetails> carReturn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public List<EmailDetails> getCarReturn() {
        return carReturn;
    }

    public void setCarReturn(List<EmailDetails> carReturn) {
        this.carReturn = carReturn;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    @Override
    public String toString() {
        return "EmailData [amount=" + amount + ", carName=" + carName + ", carReturn=" + carReturn + ", code=" + code
                + ", email=" + email + ", name=" + name + ", receipt=" + receipt + "]";
    }

}
