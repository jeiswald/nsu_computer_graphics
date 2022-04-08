package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.filters.Dither;
import ru.nsu.fit.ejsvald.filters.DitherFloydSteinberg;
import ru.nsu.fit.ejsvald.filters.OrderedDithering;

import javax.swing.*;
import java.awt.*;

public class DitheringSetPanel extends SettingsPanel {
    private JTextField floydRField = new JTextField();
    private JTextField floydGField = new JTextField();
    private JTextField floydBField = new JTextField();

    private JSlider floydRSlider = new JSlider();
    private JSlider floydGSlider = new JSlider();
    private JSlider floydBSlider = new JSlider();

    private JTextField ordRField = new JTextField();
    private JTextField ordGField = new JTextField();
    private JTextField ordBField = new JTextField();

    private JSlider ordRSlider = new JSlider();
    private JSlider ordGSlider = new JSlider();
    private JSlider ordBSlider = new JSlider();

    private JCheckBox floydBox;
    private JCheckBox orderedBox;
    private Dither dither;
    private DitherFloydSteinberg floydSteinberg;
    private OrderedDithering orderedDithering;


    public DitheringSetPanel(Dither dither) {
        super(new GridBagLayout());
        floydSteinberg = dither.getFloydSteinberg();
        orderedDithering = dither.getOrderedDithering();
        this.dither = dither;
        initPanel();
    }

    private void initPanel() {
        floydBox = addAndCreateCheckBox("Floyd-Steinberg", 0, 0, dither.isFloyd());
        floydBox.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (orderedBox != null && orderedBox.isSelected()) {
                    orderedBox.setSelected(false);
                }
            } else {
                if (orderedBox != null && !orderedBox.isSelected()) {
                    orderedBox.setSelected(true);
                }
            }
            removeAll();
            fillPanel();
            parentComponent.pack();
            repaint();
        });
        orderedBox = addAndCreateCheckBox("Ordered", 1, 0, dither.isOrdered());
        orderedBox.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (floydBox != null && floydBox.isSelected()) {
                    floydBox.setSelected(false);
                }
            } else {
                if (floydBox != null && !floydBox.isSelected()) {
                    floydBox.setSelected(true);
                }
            }
            removeAll();
            fillPanel();
            parentComponent.pack();
            repaint();
        });
        createTextFieldAndSlider(floydRField, floydRSlider,
                floydSteinberg.getRColorNum(), 2, 128, 1);
        createTextFieldAndSlider(floydGField, floydGSlider,
                floydSteinberg.getGColorNum(), 2, 128, 1);
        createTextFieldAndSlider(floydBField, floydBSlider,
                floydSteinberg.getBColorNum(), 2, 128, 1);
        createTextFieldAndSlider(ordRField, ordRSlider, orderedDithering.getRColorNum(), 2, 128, 1);
        createTextFieldAndSlider(ordGField, ordGSlider, orderedDithering.getGColorNum(), 2, 128, 1);
        createTextFieldAndSlider(ordBField, ordBSlider, orderedDithering.getBColorNum(), 2, 128, 1);
        fillPanel();
    }

    private void fillPanel() {
        addCheckBox(0, 0, floydBox);
        addCheckBox(1, 0, orderedBox);
        if (floydBox.isSelected()) {
            addTextFieldAndSlider("Red colors", 0, 1, floydRField, floydRSlider);
            addTextFieldAndSlider("Green colors", 0, 2, floydGField, floydGSlider);
            addTextFieldAndSlider("Blue colors", 0, 3, floydBField, floydBSlider);
            return;
        }
        if (orderedBox.isSelected()) {
            addTextFieldAndSlider("Red colors", 0, 1, ordRField, ordRSlider);
            addTextFieldAndSlider("Green colors", 0, 2, ordGField, ordGSlider);
            addTextFieldAndSlider("Blue colors", 0, 3, ordBField, ordBSlider);
        }
    }

    @Override
    void applySettings() {
        dither.setFloyd(floydBox.isSelected());
        dither.setOrdered(orderedBox.isSelected());
        if (floydBox.isSelected()) {
            floydSteinberg.setRColorNum(Integer.parseInt(floydRField.getText()));
            floydSteinberg.setGColorNum(Integer.parseInt(floydGField.getText()));
            floydSteinberg.setBColorNum(Integer.parseInt(floydBField.getText()));
            return;
        }
        if (orderedBox.isSelected()) {
            orderedDithering.setRColorNum(Integer.parseInt(ordRField.getText()));
            orderedDithering.setGColorNum(Integer.parseInt(ordGField.getText()));
            orderedDithering.setBColorNum(Integer.parseInt(ordBField.getText()));
        }
    }
}
