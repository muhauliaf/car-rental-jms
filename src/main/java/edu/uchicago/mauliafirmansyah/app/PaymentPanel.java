package edu.uchicago.mauliafirmansyah.app;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.uchicago.mauliafirmansyah.pay.Payment;
import edu.uchicago.mauliafirmansyah.utils.Randomizer;

public class PaymentPanel extends JPanel {
    public JComboBox<String> methodMenu;
    public JTextField numberField;
    public JButton payButton;
    public JButton generateButton;

    public PaymentPanel() {
        setLayout(new GridLayout(3, 2, 10, 10));
        methodMenu = new JComboBox<>(Payment.METHODS);
        numberField = new JTextField(20);
        generateButton = new JButton("Generate");
        payButton = new JButton("Pay");
        add(new JLabel("Payment Method:"));
        add(methodMenu);
        add(new JLabel("Card/Wallet Number:"));
        add(numberField);
        add(generateButton);
        add(payButton);
    }

    public void generateRandomPayment() {
        String method = Payment.METHODS[Randomizer.getInstance().rand().nextInt(Payment.METHODS.length)];
        String cardNumber = Randomizer.getInstance().randNumberString(16);
        methodMenu.setSelectedItem(method);
        numberField.setText(cardNumber);
    }
}
