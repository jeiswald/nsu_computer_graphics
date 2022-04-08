package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.filters.AquarelleFilter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AquarelleSetPanel extends SettingsPanel{
    private AquarelleFilter filter;
    private JTextField matrixSize;

    public AquarelleSetPanel(AquarelleFilter filter) {
        super(new GridBagLayout());
        this.filter = filter;
        matrixSize = addAndCreateTextFieldAndSlider("Aquarelle parameter", 0, 0, filter.getParam(),
                5, 50, 1);
    }

    @Override
    void applySettings() throws IOException {
        filter.setParam(Integer.parseInt(matrixSize.getText()));
    }
}
