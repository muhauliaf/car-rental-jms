package edu.uchicago.mauliafirmansyah.book;

public class Booking {
    private static int nextId = 1;
    public int id;
    public String appId;
    public String name;
    public String email;
    public String location;
    public int paymentId;
    public int carId;
    public String status;

    public static final String STATUS_CREATED = "created";
    public static final String STATUS_BOOKED = "booked";
    public static final String STATUS_PAID = "paid";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String[] STATUSES = {STATUS_CREATED, STATUS_BOOKED, STATUS_PAID, STATUS_CANCELLED};

    public Booking(String _appId, String _name, String _email, String _location) {
        id = nextId++;
        appId = _appId;
        name = _name;
        email = _email;
        location = _location;
        status = STATUS_CREATED;
    }
}
