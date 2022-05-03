package ru.nsu.fit.ejsvald.editor;

import ru.nsu.fit.ejsvald.ModelFrame;
import ru.nsu.fit.ejsvald.ModelPanel;

import javax.swing.*;
import java.awt.*;

public class EditorFrame extends JDialog {
    private final SplinePanel splinePanel;
    private final EditorSettingsPanel settingsPanel;
    public EditorFrame(ModelFrame modelFrame, boolean modal) {
        super(modelFrame, modal);
        setMinimumSize(new Dimension(500, 400));
        setLayout(new GridBagLayout());
        splinePanel = new SplinePanel(modelFrame);
        settingsPanel = splinePanel.getSettingsPanel();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 6;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(splinePanel, c);

        c.weighty = 0;
        c.gridy = 1;
        add(settingsPanel, c);

        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;

        JButton addButton = new JButton("Add");
        addButton.setPreferredSize(new Dimension(75, 23));
        c.gridx = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(10,0,10,5);
        add(addButton, c);

        JButton toLeftButton = new JButton("<");
        toLeftButton.setPreferredSize(new Dimension(55, 23));
        c.gridx = 1;
        c.weightx = 0;
        add(toLeftButton, c);

        JButton toRightButton = new JButton(">");
        toRightButton.setPreferredSize(new Dimension(55, 23));
        c.gridx = 2;
        c.weightx = 0;
        add(toRightButton, c);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(75, 23));
        c.gridx = 3;
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        add(cancelButton, c);

        JButton defaultButton = new JButton("Default");
        defaultButton.setPreferredSize(new Dimension(75, 23));
        c.gridx = 4;
        c.weightx = 0;
        add(defaultButton, c);

        JButton applyButton = new JButton("Apply");
        applyButton.setPreferredSize(new Dimension(65, 23));
        c.gridx = 5;
        c.insets = new Insets(10,0,10,10);
        c.weightx = 0;
        add(applyButton, c);

        addButton.addActionListener(e -> {
            splinePanel.addPointNextToActive();
            splinePanel.drawSpline();
            splinePanel.repaint();
        });
        toLeftButton.addActionListener(e -> {
            splinePanel.setActivePoint(splinePanel.getActivePoint() - 1);
            settingsPanel.setActivePoint(splinePanel.getActivePoint());
            splinePanel.drawSpline();
            splinePanel.repaint();
        });
        toRightButton.addActionListener(e -> {
            splinePanel.setActivePoint(splinePanel.getActivePoint() + 1);
            settingsPanel.setActivePoint(splinePanel.getActivePoint());
            splinePanel.drawSpline();
            splinePanel.repaint();
        });

        applyButton.addActionListener(e ->
        {
            ModelPanel newModelPanel = new ModelPanel(splinePanel.getPoints(), settingsPanel.getmLines(),
                    settingsPanel.getLineApproxNum(), settingsPanel.getCircleApproxNum());
            newModelPanel.setPanelSize(modelFrame.getModelPanel().getSize());
            newModelPanel.fitInScreen();
            modelFrame.setModelPanel(newModelPanel);
            newModelPanel.repaint();
            dispose();
        });
        defaultButton.addActionListener(e -> settingsPanel.setDefault());
        cancelButton.addActionListener(e -> dispose());
        pack();
    }

    public SplinePanel getSplinePanel() {
        return splinePanel;
    }

    public EditorSettingsPanel getSettingsPanel() {
        return settingsPanel;
    }
}
