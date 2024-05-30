package edu.uchicago.mauliafirmansyah.fleet;

public class Sedan extends Car {
    public double trunkSize;

    public Sedan(String _brand, String _model, double _trunkSize) {
        super(_brand, _model);
        trunkSize = _trunkSize;
    }

    public static final String BODY_TYPE = "Sedan";
    public String getBodyType() {
        return BODY_TYPE;
    }

    public String detailsText() {
        StringBuilder detail = new StringBuilder();
        detail.append("Trunk size : " + trunkSize + "\n");
        return detail.toString().strip();
    }
}
