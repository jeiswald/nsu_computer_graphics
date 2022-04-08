package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.filters.GammaCorrection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GammaSetPanel extends SettingsPanel{
    private JTextField gammaField;
    private final GammaCorrection filter;

    public GammaSetPanel(GammaCorrection filter) {
        super(new GridBagLayout());
        this.filter = filter;
        initPanel();
    }

    private void initPanel() {
        gammaField = addAndCreateTextFieldAndSlider("Gamma", 0, 0, filter.getGamma(),
                0.1, 10, 10);
    }

    @Override
    void applySettings() throws IOException {
        filter.setGamma(Double.parseDouble(gammaField.getText()));
    }
}
