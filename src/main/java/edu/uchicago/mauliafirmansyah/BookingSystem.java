package edu.uchicago.mauliafirmansyah;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jms.ConnectionFactory;
import javax.swing.JFrame;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import edu.uchicago.mauliafirmansyah.app.TablePanel;
import edu.uchicago.mauliafirmansyah.book.Booking;
import edu.uchicago.mauliafirmansyah.fleet.Location;
import edu.uchicago.mauliafirmansyah.pay.CreditCardPayment;
import edu.uchicago.mauliafirmansyah.pay.DigitalWalletPayment;

public class BookingSystem extends JFrame {
    public static final String[] COLUMN_NAMES = { "ID", "Name", "Email", "Status", "Car ID", "Payment ID", "App ID" };

    public class BookingRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("jms:queue:BOOK_REQUEST")
                    .routeId("jms:queue:BOOK_REQUEST")
                    .log("Received JMS message with body: ${body}")
                    .process(exchange -> {
                        String messageBody = exchange.getIn().getBody(String.class);
                        String[] messageBodySplit = messageBody.split(";", -1);
                        String appId = messageBodySplit[0];
                        String name = messageBodySplit[1];
                        String email = messageBodySplit[2];
                        String location = messageBodySplit[3];
                        String carType = messageBodySplit[4];
                        Booking booking = new Booking(appId, name, email, location);
                        bookings.add(booking);
                        messageBody = booking.id + ";" + carType;
                        exchange.getIn().setBody(messageBody, String.class);
                        exchange.getIn().setHeader("location", location);
                        if (name.isEmpty() || email.isEmpty() || location.isEmpty() ||
                                carType.isEmpty()) {
                            exchange.getIn().setHeader("isValid", false);
                        } else {
                            exchange.getIn().setHeader("isValid", true);
                        }
                        refreshTable();
                    })
                    .choice()
                    .when(header("isValid").isEqualTo(false))
                    .to("direct:INVALID_BOOK_REQUEST")
                    .when(header("location").isEqualTo(Location.LOCATION_CHICAGO))
                    .to("jms:queue:BOOK_CAR_REQUEST_CHICAGO")
                    .when(header("location").isEqualTo(Location.LOCATION_NEW_YORK))
                    .to("jms:queue:BOOK_CAR_REQUEST_NEW_YORK")
                    .when(header("location").isEqualTo(Location.LOCATION_LOS_ANGELES))
                    .to("jms:queue:BOOK_CAR_REQUEST_LOS_ANGELES")
                    .otherwise()
                    .to("direct:INVALID_BOOK_REQUEST")
                    .end();
            ;

            from("jms:queue:BOOK_CAR_REPLY")
                    .routeId("jms:queue:BOOK_CAR_REPLY")
                    .log("Received JMS message with body: ${body}")
                    .process(exchange -> {
                        String messageBody = exchange.getIn().getBody(String.class);
                        String[] messageBodySplit = messageBody.split(";", -1);
                        int bookingId = Integer.valueOf(messageBodySplit[0]);
                        Booking booking = getBookingById(bookingId);
                        String status = messageBodySplit[1];
                        if (status.equals("success")) {
                            int carId = Integer.valueOf(messageBodySplit[2]);
                            String carBrand = messageBodySplit[3];
                            String carModel = messageBodySplit[4];
                            messageBody = bookingId + ";booking;success;" + carId + ";" + carBrand + ";" + carModel;
                            booking.carId = carId;
                            booking.status = Booking.STATUS_BOOKED;
                        } else {
                            String reason = "There are no cars available.";
                            messageBody = bookingId + ";booking;failure;" + reason;
                            booking.status = Booking.STATUS_CANCELLED;
                        }
                        exchange.getIn().setBody(messageBody, String.class);
                        exchange.getIn().setHeader("appId", getBookingById(bookingId).appId);
                        refreshTable();
                    })
                    .recipientList(simple("jms:queue:INBOX_${header.appId}"));
            ;

            from("direct:INVALID_BOOK_REQUEST")
                    .routeId("direct:INVALID_BOOK_REQUEST")
                    .log("Received Direct message with body: ${body}")
                    .process(exchange -> {
                        String messageBody = exchange.getIn().getBody(String.class);
                        String[] messageBodySplit = messageBody.split(";", -1);
                        int bookingId = Integer.valueOf(messageBodySplit[0]);
                        Booking booking = getBookingById(bookingId);
                        String reason = "Invalid booking request.";
                        booking.status = Booking.STATUS_CANCELLED;
                        messageBody = bookingId + ";booking;failure;" + reason;
                        exchange.getIn().setBody(messageBody, String.class);
                        exchange.getIn().setHeader("appId", getBookingById(bookingId).appId);
                        refreshTable();
                    })
                    .recipientList(simple("jms:queue:INBOX_${header.appId}"));

            from("jms:queue:PAY_REQUEST")
                    .routeId("jms:queue:PAY_REQUEST")
                    .log("Received JMS message with body: ${body}")
                    .process(exchange -> {
                        String messageBody = exchange.getIn().getBody(String.class);
                        String[] messageBodySplit = messageBody.split(";", -1);
                        // String appId = messageBodySplit[0];
                        int bookingId = Integer.valueOf(messageBodySplit[1]);
                        String method = messageBodySplit[2];
                        String number = messageBodySplit[3];
                        messageBody = bookingId + ";" + number;
                        exchange.getIn().setBody(messageBody, String.class);
                        exchange.getIn().setHeader("method", method);
                        if (method.isEmpty() || number.isEmpty()) {
                            exchange.getIn().setHeader("isValid", false);
                        } else {
                            exchange.getIn().setHeader("isValid", true);
                        }
                    })
                    .choice()
                    .when(header("isValid").isEqualTo(false))
                    .to("direct:INVALID_PAY_REQUEST")
                    .when(header("method").isEqualTo(CreditCardPayment.METHOD))
                    .to("jms:queue:PAY_REQUEST_CREDIT_CARD")
                    .when(header("method").isEqualTo(DigitalWalletPayment.METHOD))
                    .to("jms:queue:PAY_REQUEST_DIGITAL_WALLET")
                    .otherwise()
                    .to("direct:INVALID_PAY_REQUEST")
                    .end();
            ;

            from("direct:INVALID_PAY_REQUEST")
                    .routeId("direct:INVALID_PAY_REQUEST")
                    .log("Received Direct message with body: ${body}")
                    .process(exchange -> {
                        String messageBody = exchange.getIn().getBody(String.class);
                        String[] messageBodySplit = messageBody.split(";", -1);
                        int bookingId = Integer.valueOf(messageBodySplit[0]);
                        Booking booking = getBookingById(bookingId);
                        String reason = "Invalid payment request.";
                        booking.status = Booking.STATUS_CANCELLED;
                        messageBody = bookingId + ";payment;failure;" + reason;
                        exchange.getIn().setBody(messageBody, String.class);
                        exchange.getIn().setHeader("appId", getBookingById(bookingId).appId);
                        refreshTable();
                    })
                    .multicast()
                    .to("direct:CANCEL_CAR_REQUEST")
                    .recipientList(simple("jms:queue:INBOX_${header.appId}"))
                    .end();

            from("direct:CANCEL_CAR_REQUEST")
                    .routeId("direct:CANCEL_CAR_REQUEST")
                    .log("Received Direct message with body: ${body}")
                    .process(exchange -> {
                        String messageBody = exchange.getIn().getBody(String.class);
                        String[] messageBodySplit = messageBody.split(";", -1);
                        int bookingId = Integer.valueOf(messageBodySplit[0]);
                        Booking booking = getBookingById(bookingId);
                        int carId = booking.carId;
                        String location = booking.location;
                        messageBody = bookingId + ";" + carId;
                        exchange.getIn().setBody(messageBody, String.class);
                        exchange.getIn().setHeader("location", location);
                    })
                    .choice()
                    .when(header("location").isEqualTo(Location.LOCATION_CHICAGO))
                    .to("jms:queue:CANCEL_CAR_REQUEST_CHICAGO")
                    .when(header("location").isEqualTo(Location.LOCATION_NEW_YORK))
                    .to("jms:queue:CANCEL_CAR_REQUEST_NEW_YORK")
                    .when(header("location").isEqualTo(Location.LOCATION_LOS_ANGELES))
                    .to("jms:queue:CANCEL_CAR_REQUEST_LOS_ANGELES")
                    .end();

            from("jms:queue:PAY_REPLY")
                    .routeId("jms:queue:PAY_REPLY")
                    .log("Received JMS message with body: ${body}")
                    .process(exchange -> {
                        String messageBody = exchange.getIn().getBody(String.class);
                        String[] messageBodySplit = messageBody.split(";", -1);
                        int bookingId = Integer.valueOf(messageBodySplit[0]);
                        Booking booking = getBookingById(bookingId);
                        String status = messageBodySplit[1];
                        if (status.equals("success")) {
                            int paymentId = Integer.valueOf(messageBodySplit[2]);
                            messageBody = bookingId + ";payment;success;" + paymentId;
                            booking.paymentId = paymentId;
                            booking.status = Booking.STATUS_PAID;
                        } else {
                            String reason = "There was an error when processing the payment.";
                            messageBody = bookingId + ";payment;failure;" + reason;
                            booking.status = Booking.STATUS_CANCELLED;
                        }
                        exchange.getIn().setBody(messageBody, String.class);
                        exchange.getIn().setHeader("appId", getBookingById(bookingId).appId);
                        refreshTable();
                    })
                    .recipientList(simple("jms:queue:INBOX_${header.appId}"));
            ;
        }
    }

    public TablePanel tablePanel;

    public CamelContext context;
    public BookingRouteBuilder routeBuilder;
    public ConnectionFactory connectionFactory;

    public List<Booking> bookings;

    public Booking getBookingById(int id) {
        for (Booking booking : bookings) {
            if (booking.id == id) {
                return booking;
            }
        }
        return null;
    }

    public void refreshTable() {
        tablePanel.table.setRowCount(0);
        for (Booking booking : bookings) {
            Object[] row = {
                    booking.id,
                    booking.name,
                    booking.email,
                    booking.status,
                    booking.carId,
                    booking.paymentId,
                    booking.appId
            };
            tablePanel.table.addRow(row);
        }
    }

    public BookingSystem() throws Exception {
        bookings = Collections.synchronizedList(new ArrayList<>());
        context = new DefaultCamelContext();
        routeBuilder = new BookingRouteBuilder();
        connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(routeBuilder);

        setTitle("Car Rental Booking Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        tablePanel = new TablePanel(COLUMN_NAMES, "Bookings");
        add(tablePanel);
        refreshTable();

        context.start();
        setVisible(true);
        Thread.sleep(1000 * 3600);
        context.stop();
    }

    public static void main(String args[]) throws Exception {
        new BookingSystem();
    }
}
