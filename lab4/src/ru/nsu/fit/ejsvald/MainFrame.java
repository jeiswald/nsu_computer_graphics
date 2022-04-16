package ru.nsu.fit.ejsvald;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final MainPanel mainPanel;
    public MainFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 400));
        setLayout(new GridBagLayout());
        mainPanel = new MainPanel();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(mainPanel, constraints);
        pack();
    }
    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
