package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.tool.LineTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

public class LineSettingsPanel extends SettingsPanel  {
    GridBagConstraints c = new GridBagConstraints();
    JTextField thickness;
    LineTool lineTool;
    public LineSettingsPanel(LineTool lineTool) {
        super(new GridBagLayout());
        setPreferredSize(new Dimension(200, 20));
        this.lineTool = lineTool;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        add(new JLabel(LineTool.THICKNESS_SETTING), c);

        thickness = new JTextField(7);
        thickness.setText(String.valueOf(lineTool.getThickness()));
        c.gridx = 1;
        thickness.addFocusListener(new FocusListener() {
            int oldVal;

            @Override
            public void focusGained(FocusEvent e) {
                oldVal = Integer.parseInt(thickness.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    Integer.parseInt(thickness.getText());
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(LineSettingsPanel.this, "Only numbers and minus sign are allowed in text field");
                    thickness.setText(String.valueOf(oldVal));
                }
            }
        });
        thickness.addActionListener(e -> {
            try {
                Integer.parseInt(thickness.getText());
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(LineSettingsPanel.this, "Only numbers and minus sign are allowed in text field");
            }
        });

        add(thickness, c);
    }

    @Override
    void applySettings() throws IOException {
        String error = lineTool.setThickness(Integer.parseInt(thickness.getText()));
        if (error != null) throw new IOException(error);
    }
}
