package com.pjt.rebis.ui.booking;

public class Ratings {
    private double Sum;
    private int Votes;

    public Ratings() {
    }

    public  Ratings(double sum, int votes) {
        this.Sum = sum;
        this.Votes = votes;
    }

    public double getSum() {
        return Sum;
    }

    public void setSum(double sum) {
        this.Sum = sum;
    }

    public int getVotes() {
        return Votes;
    }

    public void setVotes(int votes) {
        this.Votes = votes;
    }
}
