package ru.nsu.fit.ejsvald.setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class SettingDialog extends JDialog {
    private boolean ok = false;
    private boolean cancel = false;

    public SettingDialog(Frame owner, SettingsPanel panel, String title, boolean modal) {
        super(owner, title, modal);
        panel.setParentComponent(this);
        setMinimumSize(new Dimension(250, 100));
        setLayout(new GridBagLayout());
        addWindowListener(new WindowListener() {
                              @Override
                              public void windowOpened(WindowEvent e) {

                              }

                              @Override
                              public void windowClosing(WindowEvent e) {
                                  dispose();
                              }

                              @Override
                              public void windowClosed(WindowEvent e) {

                              }

                              @Override
                              public void windowIconified(WindowEvent e) {

                              }

                              @Override
                              public void windowDeiconified(WindowEvent e) {

                              }

                              @Override
                              public void windowActivated(WindowEvent e) {

                              }

                              @Override
                              public void windowDeactivated(WindowEvent e) {

                              }
                          }
        );
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 20, 20, 20);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1;
        add(panel, c);
        JButton b1 = new JButton("OK");
        b1.setPreferredSize(new Dimension(65, 23));
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 10, 5);
        c.gridwidth = 1;
        c.gridy = 1;
        add(b1, c);
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(65, 23));
        c.insets = new Insets(0, 0, 10, 10);
        c.weightx = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 1;
        add(cancelBtn, c);
        b1.addActionListener(e ->
        {
            try {
                panel.applySettings();
                ok = true;
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(this, ioException.getMessage());
                return;
            }
            this.dispose();
        });
        cancelBtn.addActionListener(e -> {
            cancel = true;
            this.dispose();
        });
    }

    public boolean isCancel() {
        return cancel;
    }

    public boolean isOk() {
        return ok;
    }
}
