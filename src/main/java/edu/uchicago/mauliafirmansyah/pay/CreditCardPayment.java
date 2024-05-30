package edu.uchicago.mauliafirmansyah.pay;

public class CreditCardPayment implements PaymentStrategy {

    public static final String METHOD = "Credit Card";
    public String number;

    public CreditCardPayment(String _number){
        number = _number;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public String getNumber() {
        return number;
    }
}
