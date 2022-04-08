package ru.nsu.fit.ejsvald.setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

public abstract class SettingsPanel extends JPanel {
    protected JDialog parentComponent;

    public SettingsPanel(LayoutManager layout) {
        super(layout);
    }

    abstract void applySettings() throws IOException;

    protected JCheckBox addAndCreateCheckBox(String name, int x, int y, boolean defaultValue) {
        JCheckBox cb = new JCheckBox(name, defaultValue);
        addCheckBox(x, y, cb);
        return cb;
    }

    protected void addCheckBox(int x, int y, JCheckBox checkBox) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        add(checkBox, c);
    }

    protected JTextField addAndCreateTextFieldAndSlider(String name, int x, int y, double defaultValue,
                                                        double bound1, double bound2, int sliderDivider) {
        JTextField textField = new JTextField();
        addAndCreateTextFieldAndSlider(name, x, y, textField, null, defaultValue, bound1, bound2, sliderDivider);
        return textField;
    }

    protected void addAndCreateTextFieldAndSlider(String name, int x, int y, JTextField textField, JSlider slider,
                                                  double defaultValue, double bound1, double bound2, int sliderDivider) {
        if (slider == null) {
            slider = new JSlider();
        }
        createTextFieldAndSlider(textField, slider, defaultValue, bound1, bound2, sliderDivider);
        addTextFieldAndSlider(name, x, y, textField, slider);
    }

    protected void createTextFieldAndSlider(JTextField textField, JSlider slider,
                                                  double defaultValue, double bound1, double bound2, int sliderDivider) {
        if (textField == null) {
            textField = new JTextField();
        }
        textField.setColumns(7);
        if (sliderDivider == 1) {
            textField.setText(String.valueOf((int) defaultValue));
        } else {
            textField.setText(String.valueOf(defaultValue));
        }

        if (slider == null) {
            slider = new JSlider();
        }
        slider.setOrientation(JSlider.HORIZONTAL);
        slider.setMinimum((int) (bound1 * sliderDivider));
        slider.setMaximum((int) (bound2 * sliderDivider));
        slider.setValue((int) (defaultValue * sliderDivider));
        JTextField finalTextField = textField;
        JSlider finalSlider = slider;
        textField.addFocusListener(new FocusListener() {
            double oldVal;

            @Override
            public void focusGained(FocusEvent e) {
                oldVal = Double.parseDouble(finalTextField.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    finalSlider.setValue((int) (Double.parseDouble(finalTextField.getText()) * sliderDivider));
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(SettingsPanel.this, "Only numbers and minus sign are allowed in text field");
                    if (sliderDivider == 1) {
                        finalTextField.setText(String.valueOf((int) oldVal));
                    } else {
                        finalTextField.setText(String.valueOf(oldVal));
                    }
                }
            }
        });
        textField.addActionListener(e -> {
            try {
                finalSlider.setValue((int) (Double.parseDouble(finalTextField.getText()) * sliderDivider));
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(SettingsPanel.this, "Only numbers and minus sign are allowed in text field");
            }
        });
        slider.addChangeListener(e -> {
            if (sliderDivider == 1) {
                finalTextField.setText(String.valueOf(finalSlider.getValue()));
            } else {
                finalTextField.setText(String.valueOf((double) finalSlider.getValue() / sliderDivider));
            }
        });
    }

    protected void addTextFieldAndSlider(String name, int x, int y, JTextField textField, JSlider slider) {
        if (name == null || textField == null || slider == null) throw new NullPointerException();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.gridx = x;
        c.gridy = y;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 0, 0, 10);
        add(new JLabel(name), c);

        c.gridx = x + 1;
        add(textField, c);

        c.gridx = x + 2;
        add(slider, c);
    }

    protected JTextField addAndCreateTextField(String name, int x, int y, int defaultValue) {
        GridBagConstraints c = new GridBagConstraints();
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
                    JOptionPane.showMessageDialog(SettingsPanel.this, "Only numbers and minus sign are allowed in text field");
                    textField.setText(String.valueOf(oldVal));
                }
            }
        });
        textField.addActionListener(e -> {
            try {
                Integer.parseInt(textField.getText());
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(SettingsPanel.this, "Only numbers and minus sign are allowed in text field");
            }
        });
        c.gridx = x + 1;
        c.gridy = y;
        add(textField, c);
        return textField;
    }

    public void setParentComponent(JDialog parentComponent) {
        this.parentComponent = parentComponent;
    }
}
