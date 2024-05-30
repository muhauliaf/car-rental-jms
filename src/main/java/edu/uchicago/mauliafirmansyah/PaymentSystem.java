package edu.uchicago.mauliafirmansyah;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.swing.JFrame;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import edu.uchicago.mauliafirmansyah.app.TablePanel;
import edu.uchicago.mauliafirmansyah.pay.CreditCardPayment;
import edu.uchicago.mauliafirmansyah.pay.DigitalWalletPayment;
import edu.uchicago.mauliafirmansyah.pay.Payment;

public class PaymentSystem extends JFrame {
    public static final String[] COLUMN_NAMES = { "ID", "Method", "Card/Wallet Number", "Status", "Booking ID" };

    public class PaymentRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("jms:queue:PAY_REQUEST_CREDIT_CARD")
                    .routeId("jms:queue:PAY_REQUEST_CREDIT_CARD")
                    .log("Received JMS message with body: ${body}")
                    .process(paymentProcessors.get(CreditCardPayment.METHOD))
                    .to("jms:queue:PAY_REPLY");

            from("jms:queue:PAY_REQUEST_DIGITAL_WALLET")
                    .routeId("jms:queue:PAY_REQUEST_DIGITAL_WALLET")
                    .log("Received JMS message with body: ${body}")
                    .process(paymentProcessors.get(DigitalWalletPayment.METHOD))
                    .to("jms:queue:PAY_REPLY");
        }
    }

    public class PaymentProcessor implements Processor {
        public String method;

        public PaymentProcessor(String _method) {
            method = _method;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            String messageBody = exchange.getIn().getBody(String.class);
            String[] messageBodySplit = messageBody.split(";", -1);
            int bookingId = Integer.valueOf(messageBodySplit[0]);
            String number = messageBodySplit[1];

            Payment payment = new Payment(bookingId, method, number);
            payments.add(payment);
            refreshTable();

            messageBody = bookingId + ";success;" + payment.id;
            exchange.getIn().setBody(messageBody, String.class);
        }
    }

    public TablePanel tablePanel;

    public CamelContext context;
    public PaymentRouteBuilder routeBuilder;
    public Map<String, PaymentProcessor> paymentProcessors;
    public ConnectionFactory connectionFactory;

    public List<Payment> payments;

    public void refreshTable() {
        tablePanel.table.setRowCount(0);
        for (Payment payment : payments) {
            Object[] row = {
                    payment.id,
                    payment.paymentStrategy.getMethod(),
                    payment.paymentStrategy.getNumber(),
                    payment.status,
                    payment.bookingId
            };
            tablePanel.table.addRow(row);
        }
    }

    public PaymentSystem() throws Exception {
        payments = Collections.synchronizedList(new ArrayList<>());
        context = new DefaultCamelContext();
        routeBuilder = new PaymentRouteBuilder();
        paymentProcessors = Collections.synchronizedMap(new HashMap<>());
        for (String method : Payment.METHODS) {
            paymentProcessors.put(method, new PaymentProcessor(method));
        }
        connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(routeBuilder);

        setTitle("Car Rental Payment Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        tablePanel = new TablePanel(COLUMN_NAMES, "Payment");
        add(tablePanel);
        refreshTable();

        context.start();
        setVisible(true);
        Thread.sleep(1000 * 3600);
        context.stop();
    }

    public static void main(String args[]) throws Exception {
        new PaymentSystem();
    }
}