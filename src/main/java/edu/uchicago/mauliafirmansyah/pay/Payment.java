package edu.uchicago.mauliafirmansyah.pay;

public class Payment {
    private static int nextId = 1;
    public static final String[] METHODS = { CreditCardPayment.METHOD, DigitalWalletPayment.METHOD };

    public int id;
    public int bookingId;
    public PaymentStrategy paymentStrategy;
    public String status;

    public Payment(int _bookingId, String payMethod, String payNumber) {
        id = nextId++;
        bookingId = _bookingId;
        if (payMethod.equals(CreditCardPayment.METHOD)) {
            paymentStrategy = new CreditCardPayment(payNumber);
        } else if (payMethod.equals(DigitalWalletPayment.METHOD)){
            paymentStrategy = new DigitalWalletPayment(payNumber);
        }
        status = "success";
    }
}
