package ru.nsu.fit.ejsvald.setting;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public abstract class SettingsPanel extends JPanel {

    public SettingsPanel(LayoutManager layout) {
        super(layout);
    }

    abstract void applySettings() throws IOException;
}
