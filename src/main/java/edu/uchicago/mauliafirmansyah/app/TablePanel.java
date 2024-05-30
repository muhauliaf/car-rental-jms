package edu.uchicago.mauliafirmansyah.app;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TablePanel extends JPanel{
    public static final int TABLE_ROWS = 10;
    public DefaultTableModel table;
    public JLabel label;

    public TablePanel(String[] columnNames, String labelText) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        label = new JLabel(labelText);
        add(label);
        table = new DefaultTableModel(columnNames, 0);
        JTable tableBox = new JTable(table);
        tableBox.setPreferredScrollableViewportSize(
                new Dimension(
                        tableBox.getPreferredSize().width,
                        tableBox.getRowHeight() * TABLE_ROWS));
        JScrollPane scrollPane = new JScrollPane(tableBox);
        add(scrollPane);
    }
}
