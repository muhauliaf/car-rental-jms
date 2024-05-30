package edu.uchicago.mauliafirmansyah.app;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.uchicago.mauliafirmansyah.fleet.CarFactory;
import edu.uchicago.mauliafirmansyah.fleet.Location;
import edu.uchicago.mauliafirmansyah.utils.Randomizer;

public class BookingPanel extends JPanel {
    private static final String[] NAMES = {
            "Aaron", "Abigail", "Adam", "Aiden", "Alexander", "Alexis", "Alice", "Alyssa", "Amanda", "Amelia",
            "Andrew", "Angela", "Anna", "Anthony", "Ashley", "Austin", "Ava", "Barbara", "Benjamin", "Betty",
            "Beverly", "Brandon", "Brian", "Brittany", "Brooklyn", "Bryan", "Caleb", "Cameron", "Carol",
            "Catherine",
            "Charles", "Charlotte", "Chloe", "Christian", "Christopher", "Claire", "Connor", "Daniel", "David",
            "Dylan",
            "Edward", "Elijah", "Elizabeth", "Ella", "Emily", "Emma", "Ethan", "Evelyn", "Gabriel", "Grace",
            "Hannah", "Heather", "Henry", "Isabella", "Jack", "Jacob", "James", "Jason", "Jayden", "Jennifer",
            "Jessica", "John", "Jonathan", "Jordan", "Joseph", "Joshua", "Julia", "Kaitlyn", "Kayla", "Kenneth",
            "Kevin", "Kimberly", "Landon", "Lauren", "Liam", "Lillian", "Logan", "Madeline", "Madison", "Mason",
            "Matthew", "Megan", "Michael", "Mia", "Michelle", "Natalie", "Nathan", "Nicholas", "Noah", "Olivia",
            "Rachel", "Rebecca", "Ryan", "Samantha", "Samuel", "Sarah", "Sophia", "Thomas", "Tyler", "Victoria",
            "William", "Zachary"
    };
    public JTextField nameField;
    public JTextField emailField;
    public JComboBox<String> locationMenu;
    public JComboBox<String> carMenu;
    public JButton bookButton;
    public JButton generateButton;

    public BookingPanel() {
        setLayout(new GridLayout(5, 2, 10, 10));
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        locationMenu = new JComboBox<>(Location.LOCATIONS);
        carMenu = new JComboBox<>(CarFactory.CAR_TYPES);
        bookButton = new JButton("Next");
        generateButton = new JButton("Generate");
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Location:"));
        add(locationMenu);
        add(new JLabel("Car Model:"));
        add(carMenu);
        add(generateButton);
        add(bookButton);
    }

    public void generateRandomBooking() {
        String firstName = NAMES[Randomizer.getInstance().rand().nextInt(NAMES.length)];
            String lastName = NAMES[Randomizer.getInstance().rand().nextInt(NAMES.length)];
            String name = firstName + " " + lastName;
            String email = firstName.toLowerCase() + "_" + lastName.toLowerCase() + "@carrental.com";
            String carType = CarFactory.CAR_TYPES[Randomizer.getInstance().rand().nextInt(CarFactory.CAR_TYPES.length)];
            String location = Location.LOCATIONS[Randomizer.getInstance().rand().nextInt(Location.LOCATIONS.length)];
            nameField.setText(name);
            emailField.setText(email);
            carMenu.setSelectedItem(carType);
            locationMenu.setSelectedItem(location);
    }
}
