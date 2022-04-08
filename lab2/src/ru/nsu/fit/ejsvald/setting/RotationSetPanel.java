package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.filters.RotationTool;

import javax.swing.*;
import java.awt.*;

public class RotationSetPanel extends SettingsPanel {
    private final RotationTool tool;
    private JTextField angle;
    
    public RotationSetPanel(RotationTool tool) {
        super(new GridBagLayout());
        this.tool = tool;
        initPanel();
    }

    private void initPanel() {
        angle = addAndCreateTextFieldAndSlider("Rotation angle", 0, 0, tool.getAngle(),
                -180, 180, 1);
    }

    @Override
    void applySettings() {
        tool.setAngle(Integer.parseInt(angle.getText()));
    }
}
