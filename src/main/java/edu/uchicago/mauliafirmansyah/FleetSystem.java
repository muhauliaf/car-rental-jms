package edu.uchicago.mauliafirmansyah;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import edu.uchicago.mauliafirmansyah.app.FleetPanel;
import edu.uchicago.mauliafirmansyah.fleet.*;

public class FleetSystem extends JFrame {

    public static final int CAR_COUNT_PER_LOCATION = 10;

    public class FleetBookProcessor implements Processor {
        public String locationName;

        public FleetBookProcessor(String _locationName) {
            locationName = _locationName;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            String messageBody = exchange.getIn().getBody(String.class);
            String[] messageBodySplit = messageBody.split(";", -1);
            int bookingId = Integer.valueOf(messageBodySplit[0]);
            String carType = messageBodySplit[1];
            Car matchedCar = null;
            Location location = Fleet.getInstance().locations.getOrDefault(locationName, null);
            for (Car car : location.cars) {
                if (car.getBodyType().equals(carType) && car.status.equals(Car.STATUS_AVAILABLE)) {
                    car.status = Car.STATUS_UNAVAILABLE;
                    car.bookingId = bookingId;
                    matchedCar = car;
                    break;
                }
            }
            if (matchedCar != null) {
                refreshTable();
                messageBody = bookingId + ";success;" + matchedCar.id + ";" + matchedCar.brand + ";" + matchedCar.model;
            } else {
                messageBody = bookingId + ";failure";
            }
            exchange.getIn().setBody(messageBody, String.class);
        }
    }

    public class FleetCancelProcessor implements Processor {
        public String locationName;

        public FleetCancelProcessor(String _locationName) {
            locationName = _locationName;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            String messageBody = exchange.getIn().getBody(String.class);
            String[] messageBodySplit = messageBody.split(";", -1);
            int bookingId = Integer.valueOf(messageBodySplit[0]);
            int carId = Integer.valueOf(messageBodySplit[1]);
            Location location = Fleet.getInstance().locations.getOrDefault(locationName, null);
            for (Car car : location.cars) {
                if (car.id == carId && car.bookingId == bookingId) {
                    car.status = Car.STATUS_AVAILABLE;
                    car.bookingId = 0;
                    refreshTable();
                    break;
                }
            }
        }
    }

    public class FleetRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {

            from("jms:queue:BOOK_CAR_REQUEST_CHICAGO")
                    .routeId("jms:queue:BOOK_CAR_REQUEST_CHICAGO")
                    .log("Received JMS message with body: ${body}")
                    .process(fleetBookProcessors.get(Location.LOCATION_CHICAGO))
                    .to("jms:queue:BOOK_CAR_REPLY");

            from("jms:queue:BOOK_CAR_REQUEST_NEW_YORK")
                    .routeId("jms:queue:BOOK_CAR_REQUEST_NEW_YORK")
                    .log("Received JMS message with body: ${body}")
                    .process(fleetBookProcessors.get(Location.LOCATION_NEW_YORK))
                    .to("jms:queue:BOOK_CAR_REPLY");

            from("jms:queue:BOOK_CAR_REQUEST_LOS_ANGELES")
                    .routeId("jms:queue:BOOK_CAR_REQUEST_LOS_ANGELES")
                    .log("Received JMS message with body: ${body}")
                    .process(fleetBookProcessors.get(Location.LOCATION_LOS_ANGELES))
                    .to("jms:queue:BOOK_CAR_REPLY");

            from("jms:queue:CANCEL_CAR_REQUEST_CHICAGO")
                    .routeId("jms:queue:CANCEL_CAR_REQUEST_CHICAGO")
                    .log("Received JMS message with body: ${body}")
                    .process(fleetCancelProcessors.get(Location.LOCATION_CHICAGO));

            from("jms:queue:CANCEL_CAR_REQUEST_NEW_YORK")
                    .routeId("jms:queue:CANCEL_CAR_REQUEST_NEW_YORK")
                    .log("Received JMS message with body: ${body}")
                    .process(fleetCancelProcessors.get(Location.LOCATION_NEW_YORK));

            from("jms:queue:CANCEL_CAR_REQUEST_LOS_ANGELES")
                    .routeId("jms:queue:CANCEL_CAR_REQUEST_LOS_ANGELES")
                    .log("Received JMS message with body: ${body}")
                    .process(fleetCancelProcessors.get(Location.LOCATION_LOS_ANGELES));
        }
    }

    public FleetPanel fleetPanel;

    public CamelContext context;
    public FleetRouteBuilder routeBuilder;
    public Map<String, FleetBookProcessor> fleetBookProcessors;
    public Map<String, FleetCancelProcessor> fleetCancelProcessors;
    public ConnectionFactory connectionFactory;

    public void generateFleet() {
        for (String locationName : Location.LOCATIONS) {
            Location location = new Location(locationName);
            for (int i = 0; i < CAR_COUNT_PER_LOCATION; i++) {
                location.cars.add(CarFactory.generate());
            }
            Fleet.getInstance().locations.put(locationName, location);
        }
    }

    public void refreshTable() {
        for (String locationName : Location.LOCATIONS) {
            Location location = Fleet.getInstance().locations.get(locationName);
            DefaultTableModel table = fleetPanel.tables.get(locationName);
            table.setRowCount(0);
            for (Car car : location.cars) {
                Object[] row = { car.id, car.brand, car.model, car.getBodyType(), car.status };
                table.addRow(row);
            }
        }
    }

    public FleetSystem() throws Exception {
        generateFleet();

        context = new DefaultCamelContext();
        routeBuilder = new FleetRouteBuilder();
        fleetBookProcessors = Collections.synchronizedMap(new HashMap<>());
        fleetCancelProcessors = Collections.synchronizedMap(new HashMap<>());
        for (String locationName : Location.LOCATIONS) {
            fleetBookProcessors.put(locationName, new FleetBookProcessor(locationName));
            fleetCancelProcessors.put(locationName, new FleetCancelProcessor(locationName));
        }
        connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        context.addComponent("jms",
                JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        context.addRoutes(routeBuilder);

        setTitle("Car Rental Fleet Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        fleetPanel = new FleetPanel();
        JScrollPane scrollPane = new JScrollPane(fleetPanel);
        add(scrollPane);
        refreshTable();

        context.start();
        setVisible(true);
        Thread.sleep(1000 * 3600);
        context.stop();
    }

    public static void main(String args[]) throws Exception {
        new FleetSystem();
    }
}