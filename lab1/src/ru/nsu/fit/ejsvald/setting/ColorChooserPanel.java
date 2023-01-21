package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.MainPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ColorChooserPanel extends JPanel implements ChangeListener {
    protected JColorChooser colorChooser;
    public ColorChooserPanel(JDialog dialog, MainPanel mainPanel) {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        colorChooser = new JColorChooser(mainPanel.getCurColor());
        colorChooser.getSelectionModel().addChangeListener(this);
        colorChooser.setBorder(BorderFactory.createTitledBorder(
                "Choose Color"));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 0;
        add(colorChooser, c);

        JButton b1 = new JButton("OK");
        c.fill = 0;
        c.gridwidth = 1;
        c.weightx = 1.0;

        c.gridx = 3;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        add(b1, c);
        JButton cancel = new JButton("Cancel");
        c.fill = 0;
        c.gridx = 4;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        add(cancel, c);
        b1.addActionListener(e -> {
            mainPanel.setCurColor(colorChooser.getColor());
            dialog.dispose();
        });
        cancel.addActionListener(e -> dialog.dispose());
    }
    @Override
    public void stateChanged(ChangeEvent e) {
    }

}
