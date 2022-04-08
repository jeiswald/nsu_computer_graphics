package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.filters.EdgeDetectionFilter;

import javax.swing.*;
import java.awt.*;

public class EdgeDetectionSetPanel extends SettingsPanel {
    private final EdgeDetectionFilter filter;
    private JTextField binaryPar = null;
    private JSlider binarySlider = null;
    private JCheckBox bin;
    private JCheckBox blackWhite;
    private JCheckBox roberts;
    private JCheckBox sobel;

    public EdgeDetectionSetPanel(EdgeDetectionFilter filter) {
        super(new GridBagLayout());
        this.filter = filter;
        initPanel();
    }

    private void initPanel() {
        roberts = addAndCreateCheckBox("Use Roberts operator", 0, 0, filter.isRoberts());
        roberts.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (sobel != null && sobel.isSelected()) {
                    sobel.setSelected(false);
                }
            } else {
                if (sobel != null && !sobel.isSelected()) {
                    sobel.setSelected(true);
                }
            }
        });
        sobel = addAndCreateCheckBox("Use Sobel operator", 0, 1, filter.isSobel());
        sobel.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (roberts != null && roberts.isSelected()) {
                    roberts.setSelected(false);
                }
            } else {
                if (roberts != null && !roberts.isSelected()) {
                    roberts.setSelected(true);
                }
            }
        });
        blackWhite = addAndCreateCheckBox("Black and White", 0, 2, filter.isBlackWhite());
        bin = addAndCreateCheckBox("Binary", 0, 3, filter.isBin());
        if (bin.isSelected()) {
            binaryPar = new JTextField();
            binarySlider = new JSlider();
            addAndCreateTextFieldAndSlider(EdgeDetectionFilter.BINARY_PAR_SETTING, 0, 4, binaryPar, binarySlider,
                    filter.getBinaryPar(), 0, 255, 1);
        }
        bin.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (binaryPar == null && binarySlider == null) {
                    binaryPar = new JTextField();
                    binarySlider = new JSlider();
                    addAndCreateTextFieldAndSlider(EdgeDetectionFilter.BINARY_PAR_SETTING, 0, 4, binaryPar, binarySlider,
                            filter.getBinaryPar(), 0, 255, 1);
                } else {
                    addTextFieldAndSlider(EdgeDetectionFilter.BINARY_PAR_SETTING, 0, 4, binaryPar, binarySlider);
                }
            } else {
                removeAll();
                fillPanel();
            }
            parentComponent.pack();
            repaint();
        });
    }
    private void fillPanel() {
        addCheckBox(0, 0, roberts);
        addCheckBox(0, 1, sobel);
        addCheckBox(0, 2, blackWhite);
        addCheckBox(0, 3, bin);
        if (bin.isSelected()) {
            binaryPar = new JTextField();
            binarySlider = new JSlider();
            addTextFieldAndSlider(EdgeDetectionFilter.BINARY_PAR_SETTING, 0, 4, binaryPar, binarySlider);
        }
    }

    @Override
    void applySettings() {
        filter.setBin(bin.isSelected());
        filter.setBlackWhite(blackWhite.isSelected());
        filter.setRoberts(roberts.isSelected());
        filter.setSobel(sobel.isSelected());
        if (bin.isSelected()) {
            filter.setBinaryPar(Integer.parseInt(binaryPar.getText()));
        }
    }
}
