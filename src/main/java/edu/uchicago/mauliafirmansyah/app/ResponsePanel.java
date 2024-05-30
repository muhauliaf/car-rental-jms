package edu.uchicago.mauliafirmansyah.app;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class ResponsePanel extends JPanel {
    public JLabel titleLabel;
    public JTextArea bodyLabel;
    public JButton okButton;

    public ResponsePanel() {
        setLayout(new GridLayout(3, 1, 10, 10));
        titleLabel = new JLabel("Response Title");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bodyLabel = new JTextArea("Response Body");
        bodyLabel.setWrapStyleWord(true);
        bodyLabel.setLineWrap(true);
        bodyLabel.setEditable(false);
        bodyLabel.setBackground(getBackground());
        okButton = new JButton("OK");
        add(titleLabel);
        add(bodyLabel);
        add(okButton);
    }
}

