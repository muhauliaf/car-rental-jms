package edu.uchicago.mauliafirmansyah.app;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import edu.uchicago.mauliafirmansyah.fleet.Location;

public class FleetPanel extends JPanel {
    public static final String[] COLUMN_NAMES = { "ID", "Brand", "Model", "Body Type", "Status" };
    public Map<String, DefaultTableModel> tables;

    public FleetPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        tables = new HashMap<>();
        for (String locationName : Location.LOCATIONS) {
            JPanel tablePanel = new JPanel();
            tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
            JLabel label = new JLabel(locationName);
            tablePanel.add(label);

            DefaultTableModel table = new DefaultTableModel(COLUMN_NAMES, 0);
            JTable tableBox = new JTable(table);
            tableBox.setPreferredScrollableViewportSize(
                    new Dimension(
                            tableBox.getPreferredSize().width,
                            tableBox.getRowHeight() * 10));
            tables.put(locationName, table);

            JScrollPane tableScrollPane = new JScrollPane(tableBox);
            tablePanel.add(tableScrollPane);

            add(tablePanel);
        }
    }
}
