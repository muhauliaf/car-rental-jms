package edu.uchicago.mauliafirmansyah.fleet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Location {
    public static final String LOCATION_CHICAGO = "Chicago";
    public static final String LOCATION_NEW_YORK = "New York";
    public static final String LOCATION_LOS_ANGELES = "Los Angeles";
    public static final String[] LOCATIONS = { LOCATION_CHICAGO, LOCATION_NEW_YORK, LOCATION_LOS_ANGELES };
    private static int nextId = 1;
    public int id;
    public String name;
    public List<Car> cars;

    public Location(String _name) {
        id = nextId++;
        name = _name;
        cars = Collections.synchronizedList(new ArrayList<>());
    }

    public String summaryText() {
        StringBuilder summaryText = new StringBuilder();
        summaryText.append("Rental Place:");
        summaryText.append("Location : " + name + "\n");
        if (!cars.isEmpty()) {
            summaryText.append("List of cars :\n");
            for (Car car : cars) {
                summaryText.append("> " + car.summaryText() + "\n");
            }
        }
        return summaryText.toString().strip();
    }
}
