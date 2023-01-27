package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.filters.BlurFilter;
import ru.nsu.fit.ejsvald.filters.EdgeDetectionFilter;

import javax.swing.*;
import java.awt.*;

public class BlurSetPanel extends SettingsPanel {
    private final BlurFilter filter;
    private JCheckBox gaussBox;
    private JTextField sigmaField;
    private JSlider sigmaSlider;
    private JTextField windowSizeField;
    private JSlider windowSizeSlider;

    public BlurSetPanel(BlurFilter filter) {
        super(new GridBagLayout());
        this.filter = filter;
        initPanel();
    }

    @Override
    void applySettings() {
        if (gaussBox.isSelected()) {
            filter.setSigma(Double.parseDouble(sigmaField.getText()));
            filter.setGauss(true);
        } else {
            filter.setMatrixSize(Integer.parseInt(windowSizeField.getText()));
            filter.setGauss(false);
        }
    }

    private void initPanel() {
        gaussBox = addAndCreateCheckBox("Gaussian blur", 0, 0, filter.isGauss());
        if (gaussBox.isSelected()) {
            sigmaField = new JTextField();
            sigmaSlider = new JSlider();
            addAndCreateTextFieldAndSlider("Sigma", 0, 1, sigmaField, sigmaSlider,
                    filter.getSigma(), 0, 30, 10);
        } else {
            windowSizeField = new JTextField();
            windowSizeSlider = new JSlider();
            addAndCreateTextFieldAndSlider("Window size", 0, 1,
                    windowSizeField, windowSizeSlider, filter.getMatrixSize(), 3, 11, 1);
        }
        gaussBox.addItemListener(e -> {
                    JCheckBox cb = (JCheckBox) e.getSource();
                    removeAll();
                    fillPanel();
                    parentComponent.pack();
                    repaint();
                }
        );
    }

    private void fillPanel() {
        addCheckBox(0, 0, gaussBox);
        if (gaussBox.isSelected()) {
            if (sigmaSlider == null && sigmaField == null) {
                sigmaField = new JTextField();
                sigmaSlider = new JSlider();
                addAndCreateTextFieldAndSlider("Sigma", 0, 1, sigmaField, sigmaSlider,
                        filter.getSigma(), 0, 30, 10);
            } else {
                addTextFieldAndSlider(EdgeDetectionFilter.BINARY_PAR_SETTING, 0, 4, sigmaField, sigmaSlider);
            }
        } else {
            if (windowSizeField == null && windowSizeSlider == null) {
                windowSizeField = new JTextField();
                windowSizeSlider = new JSlider();
                addAndCreateTextFieldAndSlider("Window size", 0, 1,
                        windowSizeField, windowSizeSlider, filter.getMatrixSize(), 3, 11, 1);
            } else {
                addTextFieldAndSlider("Window size", 0, 1, windowSizeField, windowSizeSlider);
            }
        }
    }
}
