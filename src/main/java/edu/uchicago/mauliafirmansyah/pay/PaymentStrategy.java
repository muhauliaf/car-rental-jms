package edu.uchicago.mauliafirmansyah.pay;

public interface PaymentStrategy {
    public String getMethod();
    public String getNumber();
}
