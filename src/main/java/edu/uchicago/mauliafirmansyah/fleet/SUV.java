package edu.uchicago.mauliafirmansyah.fleet;

public class SUV extends Car {
    public boolean allWheelDrive;

    public SUV(String _brand, String _model, boolean _allWheelDrive) {
        super(_brand, _model);
        allWheelDrive = _allWheelDrive;
    }

    public static final String BODY_TYPE = "SUV";
    public String getBodyType() {
        return BODY_TYPE;
    }

    public String detailsText() {
        StringBuilder detail = new StringBuilder();
        detail.append("All wheel drive : " + allWheelDrive + "\n");
        return detail.toString().strip();
    }
}