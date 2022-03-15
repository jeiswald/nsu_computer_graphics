package ru.nsu.fit.ejsvald.tool;

import ru.nsu.fit.ejsvald.setting.Setting;
import ru.nsu.fit.ejsvald.setting.SettingsPanel;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public interface Tool {
    SettingsPanel getSettings();

    String getName();

    void doJob(int x, int y, Color color, EventType eventType);
}
