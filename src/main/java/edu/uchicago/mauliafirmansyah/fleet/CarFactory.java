package edu.uchicago.mauliafirmansyah.fleet;

import edu.uchicago.mauliafirmansyah.utils.Randomizer;

public class CarFactory {
    public static final String[] CAR_TYPES = { Sedan.BODY_TYPE, SUV.BODY_TYPE, Truck.BODY_TYPE };
    public static final String[] CAR_BRANDS = { "Toyota", "Honda", "Ford", "Chevrolet", "Nissan" };
    public static final String[] SEDAN_MODELS = { "Camry", "Accord", "Fusion", "Malibu", "Altima" };
    public static final String[] SUV_MODELS = { "RAV4", "CR-V", "Escape", "Equinox", "Rogue" };
    public static final String[] TRUCK_MODELS = { "Tacoma", "Ridgeline", "F-150", "Silverado", "Frontier" };

    public static Car generate() {
        int carType = Randomizer.getInstance().rand().nextInt(3);
        int brand = Randomizer.getInstance().rand().nextInt(CAR_BRANDS.length);
        String brandName = CAR_BRANDS[brand];

        switch (carType) {
            case 0:
                String sedanModel = SEDAN_MODELS[brand];
                double trunkSize = 10.0 + (20.0 - 10.0) * Randomizer.getInstance().rand().nextDouble();
                return new Sedan(brandName, sedanModel, trunkSize);
            case 1:
                String suvModel = SUV_MODELS[brand];
                boolean allWheelDrive = Randomizer.getInstance().rand().nextBoolean();
                return new SUV(brandName, suvModel, allWheelDrive);
            default:
                String truckModel = TRUCK_MODELS[brand];
                double towingCapacity = 5000.0 + (15000.0 - 5000.0) * Randomizer.getInstance().rand().nextDouble();
                return new Truck(brandName, truckModel, towingCapacity);
        }
    }
}
