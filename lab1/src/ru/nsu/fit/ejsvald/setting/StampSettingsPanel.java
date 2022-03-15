package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.tool.StampTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

public class StampSettingsPanel extends SettingsPanel {
    GridBagConstraints c = new GridBagConstraints();
    StampTool stampTool;
    JTextField vertexes, thickness, rotation, size;

    public StampSettingsPanel(StampTool stampTool) {
        super(new GridBagLayout());
        this.stampTool = stampTool;

        setPreferredSize(new Dimension(300, 250));
        vertexes = addTextField(StampTool.VERTEXES_SETTING, 0, 0, stampTool.getVertexes());
        thickness = addTextField(StampTool.THICKNESS_SETTING, 0, 1, stampTool.getThickness());
        rotation = addTextFieldAndSLider(StampTool.ROTATION_SETTING, 0, 2, stampTool.getRotation(), -180, 180);
        size = addTextFieldAndSLider(StampTool.SIZE_SETTING, 0, 3, stampTool.getSize(), 10, 250);
    }

    private JTextField addTextFieldAndSLider(String name, int x, int y, int defaultValue, int bound1, int bound2) {
        c.weightx = 1;
        c.gridx = x;
        c.gridy = y;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 0, 0, 10);
        add(new JLabel(name), c);

        JTextField textField = new JTextField(7);
        textField.setText(String.valueOf(defaultValue));
        c.gridx = x + 1;
        add(textField, c);

        c.gridx = x + 2;
        JSlider slider = new JSlider(JSlider.HORIZONTAL, bound1, bound2, defaultValue);
        textField.addFocusListener(new FocusListener() {
            int oldVal;

            @Override
            public void focusGained(FocusEvent e) {
                oldVal = Integer.parseInt(textField.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    slider.setValue(Integer.parseInt(textField.getText()));
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(StampSettingsPanel.this, "Only numbers and minus sign are allowed in text field");
                    textField.setText(String.valueOf(oldVal));
                }
            }
        });
        textField.addActionListener(e -> {
            try {
                slider.setValue(Integer.parseInt(textField.getText()));
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(StampSettingsPanel.this, "Only numbers and minus sign are allowed in text field");
            }
        });
        slider.addChangeListener(e -> {
            textField.setText(String.valueOf(slider.getValue()));
        });
        add(slider, c);
        return textField;
    }

    private JTextField addTextField(String name, int x, int y, int defaultValue) {
        c.weightx = 1;
        c.gridx = x;
        c.gridy = y;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 0, 0, 10);
        add(new JLabel(name), c);
        JTextField textField = new JTextField(7);
        textField.setText(String.valueOf(defaultValue));
        textField.addFocusListener(new FocusListener() {
            int oldVal;

            @Override
            public void focusGained(FocusEvent e) {
                oldVal = Integer.parseInt(textField.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    Integer.parseInt(textField.getText());
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(StampSettingsPanel.this, "Only numbers and minus sign are allowed in text field");
                    textField.setText(String.valueOf(oldVal));
                }
            }
        });
        textField.addActionListener(e -> {
            try {
                Integer.parseInt(textField.getText());
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(StampSettingsPanel.this, "Only numbers and minus sign are allowed in text field");
            }
        });
        c.gridx = x + 1;
        c.gridy = y;
        add(textField, c);
        return textField;
    }

    @Override
    public void applySettings() throws IOException {
        StringBuilder builder = new StringBuilder();
        String error = stampTool.setVertexes(Integer.parseInt(vertexes.getText()));
        if (error != null) {
            builder.append(error);
        }
        error = stampTool.setThickness(Integer.parseInt(thickness.getText()));
        if (error != null) {
            builder.append(error);
        }
        error = stampTool.setRotation(Integer.parseInt(rotation.getText()));
        if (error != null) {
            builder.append(error);
        }
        error = stampTool.setSize(Integer.parseInt(size.getText()));
        if (error != null) {
            builder.append(error);
        }
        if (!builder.toString().isEmpty()) throw new IOException(builder.toString());
    }

}
