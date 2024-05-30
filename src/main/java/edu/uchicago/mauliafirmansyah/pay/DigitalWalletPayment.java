package edu.uchicago.mauliafirmansyah.pay;

public class DigitalWalletPayment implements PaymentStrategy {

    public static final String METHOD = "Digital Wallet";
    public String number;

    public DigitalWalletPayment(String _number){
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
