package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.filters.pencil.PencilTool;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class PencilSetPanel extends SettingsPanel {
    private JCheckBox robertsCheckBox;
    private JCheckBox defaultEdgeDCheckBox;

    private JCheckBox reverseBlackWhiteBox;

    private JTextField gradientStepTF = new JTextField();

    private JSlider gradientStepSlider = new JSlider();

    private JTextField maxGrayLevelTF = new JTextField();

    private JSlider maxGrayLevelSlider = new JSlider();

    private JTextField binParamTF = new JTextField();

    private JSlider binParamSlider = new JSlider();

    private JTextField strokesStepTF = new JTextField();

    private JSlider strokesStepSlider = new JSlider();
    private PencilTool pencilTool;

    public PencilSetPanel(PencilTool pencilTool) {
        super(new GridBagLayout());
        this.pencilTool = pencilTool;
        initPanel();
    }

    private void initPanel() {
        defaultEdgeDCheckBox = new JCheckBox("default", pencilTool.isDefaultEdgeDet());
        defaultEdgeDCheckBox.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (robertsCheckBox != null && robertsCheckBox.isSelected()) {
                    robertsCheckBox.setSelected(false);
                }
            } else {
                if (robertsCheckBox != null && !robertsCheckBox.isSelected()) {
                    robertsCheckBox.setSelected(true);
                }
            }
            removeAll();
            fillPanel();
            parentComponent.pack();
            repaint();
        });
        robertsCheckBox = new JCheckBox("Roberts", pencilTool.isRobertsEdgeDet());
        robertsCheckBox.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (defaultEdgeDCheckBox != null && defaultEdgeDCheckBox.isSelected()) {
                    defaultEdgeDCheckBox.setSelected(false);
                }
            } else {
                if (defaultEdgeDCheckBox != null && !defaultEdgeDCheckBox.isSelected()) {
                    defaultEdgeDCheckBox.setSelected(true);
                }
            }
            removeAll();
            fillPanel();
            parentComponent.pack();
            repaint();
        });
        reverseBlackWhiteBox = new JCheckBox("reverse Black&White applying", pencilTool.isReverseBW());
        createTextFieldAndSlider(gradientStepTF, gradientStepSlider, pencilTool.getGradGap(), 1, 5, 1);
        createTextFieldAndSlider(strokesStepTF, strokesStepSlider,  pencilTool.getStrokeStep(), 1, 10, 1);
        createTextFieldAndSlider(maxGrayLevelTF, maxGrayLevelSlider,
                pencilTool.getMaxGrayLevel(), 20, 255, 1);
        createTextFieldAndSlider(binParamTF, binParamSlider, pencilTool.getBinParam(), 10,
                Double.parseDouble(maxGrayLevelTF.getText()), 1);
        maxGrayLevelSlider.addChangeListener(e -> {
            createTextFieldAndSlider(binParamTF, binParamSlider, Double.parseDouble(binParamTF.getText()), 10,
                    maxGrayLevelSlider.getValue(), 1);
            removeAll();
            fillPanel();
            parentComponent.pack();
            repaint();
        });
        fillPanel();
    }

    private void fillPanel() {
        int count = 0;
        addLabel(0, count++, new JLabel("Edge detection: "));
        addCheckBox(0, count, defaultEdgeDCheckBox);
        addCheckBox(1, count++, robertsCheckBox);
        addCheckBox(0, count++, reverseBlackWhiteBox);
        if (defaultEdgeDCheckBox.isSelected()) {
            addTextFieldAndSlider("Gradient step", 0, count++, gradientStepTF, gradientStepSlider);
        }
        addTextFieldAndSlider("Stroke step", 0, count++, strokesStepTF, strokesStepSlider);
        addTextFieldAndSlider("Max gray level", 0, count++, maxGrayLevelTF, maxGrayLevelSlider);
        addTextFieldAndSlider("Bin parameter", 0, count++, binParamTF, binParamSlider);
    }

    @Override
    void applySettings() throws IOException {
        pencilTool.setDefaultEdgeDet(defaultEdgeDCheckBox.isSelected());
        pencilTool.setRobertsEdgeDet(robertsCheckBox.isSelected());
        pencilTool.setReverseBW(reverseBlackWhiteBox.isSelected());
        if (defaultEdgeDCheckBox.isSelected()) {
            pencilTool.setGradGap(Integer.parseInt(gradientStepTF.getText()));
        }
        pencilTool.setBinParam(Integer.parseInt(binParamTF.getText()));
        pencilTool.setStrokeStep(Integer.parseInt(strokesStepTF.getText()));
        pencilTool.setMaxGrayLevel(Integer.parseInt(maxGrayLevelTF.getText()));
    }
}
