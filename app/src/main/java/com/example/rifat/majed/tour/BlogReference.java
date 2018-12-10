package com.example.rifat.majed.tour;

/**
 * Created by User on 08-May-17.
 */

public class BlogReference {
    public String destination;
    public String budget;
    public String fromDate;
    public String toDate;
    public String image;
    public String username;
    public String itemDetails;
    public String expense;
    public String mDate;
    public String moment;
    public String moments_date;
    public String balance;


    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getMoment() {
        return moment;
    }

    public void setMoment(String moment) {
        this.moment = moment;
    }

    public String getMoments_date() {
        return moments_date;
    }

    public void setMoments_date(String moments_date) {
        this.moments_date = moments_date;
    }

    public BlogReference(String destination, String budget, String fromDate, String toDate, String image, String username, String itemDetails, String expense, String mDate, String balance, String moment, String moments_date) {
        this.destination = destination;
        this.budget = budget;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.image = image;
        this.username = username;
        this.itemDetails = itemDetails;
        this.expense = expense;
        this.mDate = mDate;
        this.balance = balance;
        this.moment = moment;
        this.moments_date = moments_date;



    }

    public BlogReference() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getImage() {
      
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(String itemDetails) {
        this.itemDetails = itemDetails;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }
}
