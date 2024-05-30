package edu.uchicago.mauliafirmansyah.app;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LoadingPanel extends JPanel {
    public JLabel loadingLabel;

    public LoadingPanel() {
        setLayout(new GridLayout(1, 1, 10, 10));
        loadingLabel = new JLabel("Loading...");
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(loadingLabel);
    }
}
