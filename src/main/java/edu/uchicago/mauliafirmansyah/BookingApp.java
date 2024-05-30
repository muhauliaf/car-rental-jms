package edu.uchicago.mauliafirmansyah;

import javax.jms.ConnectionFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import edu.uchicago.mauliafirmansyah.app.BookingPanel;
import edu.uchicago.mauliafirmansyah.app.LoadingPanel;
import edu.uchicago.mauliafirmansyah.app.PaymentPanel;
import edu.uchicago.mauliafirmansyah.app.ResponsePanel;
import edu.uchicago.mauliafirmansyah.utils.Randomizer;

import java.awt.CardLayout;

public class BookingApp extends JFrame {

    public static final String APP_ID = Randomizer.getInstance().randString(8);

    public JPanel cardPanel;
    public CardLayout cardLayout;
    public BookingPanel bookingPanel;
    public PaymentPanel paymentPanel;
    public ResponsePanel bookResponsePanel;
    public ResponsePanel payResponsePanel;
    public ResponsePanel failResponsePanel;
    public LoadingPanel loadingPanel;

    public CamelContext context;
    public AppRouteBuilder routeBuilder;
    public ConnectionFactory connectionFactory;

    public int bookingId;

    public class AppRouteBuilder extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("jms:queue:INBOX_" + APP_ID)
            .routeId("jms:queue:INBOX_" + APP_ID)
            .log("Received JMS message with body: ${body}")
                    .process(exchange -> {
                        String messageBody = exchange.getIn().getBody(String.class);
                        String[] messageBodySplit = messageBody.split(";",-1);
                        bookingId = Integer.valueOf(messageBodySplit[0]);
                        String action = messageBodySplit[1];
                        String status = messageBodySplit[2];
                        if (action.equals("booking")) {
                            if (status.equals("success")) {
                                // int carId = Integer.valueOf(messageBodySplit[3]);
                                String carBrand = messageBodySplit[4];
                                String carModel = messageBodySplit[5];
                                String titleText = "Booking successful (Booking ID = " + bookingId + ")";
                                String bodyText = "You have successfully booked a " + carBrand + " " + carModel + ". " +
                                        "Proceed to payment.";
                                bookResponsePanel.titleLabel.setText(titleText);
                                bookResponsePanel.bodyLabel.setText(bodyText);
                                cardLayout.show(cardPanel, "booksuccess");
                            } else if (status.equals("failure")) {
                                String reason = messageBodySplit[3];
                                failResponsePanel.titleLabel.setText("Booking failed");
                                failResponsePanel.bodyLabel.setText(reason);
                                cardLayout.show(cardPanel, "failure");
                            }
                        } else if (action.equals("payment")) {
                            if (status.equals("success")) {
                                int paymentId = Integer.valueOf(messageBodySplit[3]);
                                String titleText = "Payment successful (Payment ID = " + paymentId + ")";
                                String bodyText = "You have successfully paid your booking. " +
                                        "Enjoy your car!";
                                payResponsePanel.titleLabel.setText(titleText);
                                payResponsePanel.bodyLabel.setText(bodyText);
                                cardLayout.show(cardPanel, "paysuccess");
                            } else if (status.equals("failure")) {
                                String reason = messageBodySplit[3];
                                failResponsePanel.titleLabel.setText("Payment failed");
                                failResponsePanel.bodyLabel.setText(reason);
                                cardLayout.show(cardPanel, "failure");
                            }
                        }
                    });
        }

    }

    public BookingApp() throws Exception {
        context = new DefaultCamelContext();
        routeBuilder = new AppRouteBuilder();
        connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(routeBuilder);

        setTitle("Car Rental App (App ID = " + APP_ID + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        bookingPanel = new BookingPanel();
        paymentPanel = new PaymentPanel();
        bookResponsePanel = new ResponsePanel();
        payResponsePanel = new ResponsePanel();
        failResponsePanel = new ResponsePanel();
        loadingPanel = new LoadingPanel();

        cardPanel.add(bookingPanel, "booking");
        cardPanel.add(paymentPanel, "payment");
        cardPanel.add(bookResponsePanel, "booksuccess");
        cardPanel.add(payResponsePanel, "paysuccess");
        cardPanel.add(failResponsePanel, "failure");
        cardPanel.add(loadingPanel, "loading");

        bookingPanel.generateButton.addActionListener(e -> {
            bookingPanel.generateRandomBooking();
        });
        bookingPanel.bookButton.addActionListener(e -> {
            String name = bookingPanel.nameField.getText();
            String email = bookingPanel.emailField.getText();
            String location = bookingPanel.locationMenu.getSelectedItem().toString();
            String carType = bookingPanel.carMenu.getSelectedItem().toString();
            String bookMessage = APP_ID + ";" + name + ";" + email + ";" + location + ";" + carType;
            context.createProducerTemplate().sendBody("jms:queue:BOOK_REQUEST", bookMessage);
            cardLayout.show(cardPanel, "loading");
        });
        paymentPanel.generateButton.addActionListener(e -> {
            paymentPanel.generateRandomPayment();
        });
        paymentPanel.payButton.addActionListener(e -> {
            String method = paymentPanel.methodMenu.getSelectedItem().toString();
            String number = paymentPanel.numberField.getText();
            String payMessage = APP_ID + ";" + bookingId + ";" + method + ";" + number;
            context.createProducerTemplate().sendBody("jms:queue:PAY_REQUEST", payMessage);
            cardLayout.show(cardPanel, "loading");
        });
        bookResponsePanel.okButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "payment");
        });
        payResponsePanel.okButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "booking");
        });
        failResponsePanel.okButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "booking");
        });
        add(cardPanel);

        context.start();
        setVisible(true);
        Thread.sleep(1000 * 3600);
        context.stop();
    }

    public static void main(String[] args) throws Exception {
        new BookingApp();
    }
}
