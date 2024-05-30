package edu.uchicago.mauliafirmansyah.fleet;

public class Truck extends Car {
    public double towingCapacity;

    public Truck(String _brand, String _model, double _towingCapacity) {
        super(_brand, _model);
        towingCapacity = _towingCapacity;
    }

    public static final String BODY_TYPE = "Truck";
    public String getBodyType() {
        return BODY_TYPE;
    }

    public String detailsText() {
        StringBuilder detail = new StringBuilder();
        detail.append("Towing capacity : " + towingCapacity + "\n");
        return detail.toString().strip();
    }
}
