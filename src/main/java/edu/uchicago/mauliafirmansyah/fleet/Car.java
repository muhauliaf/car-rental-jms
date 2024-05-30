package edu.uchicago.mauliafirmansyah.fleet;

public abstract class Car {
    private static int nextId = 1;
    public int id;
    public String brand;
    public String model;
    public String status;
    public int bookingId;
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_UNAVAILABLE = "unavailable";

    Car(String _brand, String _model) {
        id = nextId++;
        brand = _brand;
        model = _model;
        status = STATUS_AVAILABLE;
    }

    public String summaryText() {
        StringBuilder summaryText = new StringBuilder();
        summaryText.append("Car ID " + id + ":\n");
        summaryText.append("Body Type : " + getBodyType() + "\n");
        summaryText.append("Brand : " + brand + "\n");
        summaryText.append("Model : " + model + "\n");
        summaryText.append(detailsText() + "\n");
        return summaryText.toString().strip();
    }

    public abstract String getBodyType();

    public abstract String detailsText();
}
