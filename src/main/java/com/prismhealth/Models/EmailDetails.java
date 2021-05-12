package com.prismhealth.Models;

import java.util.Date;

public class EmailDetails {
    private String carName;
    private String location;
    private Date date;
    private Date returnBy;
    private String phone;
    private Date returnDate;

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getReturnBy() {
        return returnBy;
    }

    public void setReturnBy(Date returnBy) {
        this.returnBy = returnBy;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "EmailDetails [carName=" + carName + ", date=" + date + ", location=" + location + ", phone=" + phone
                + ", returnBy=" + returnBy + ", returnDate=" + returnDate + "]";
    }

}
