package ru.nsu.fit.ejsvald.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class EditorSettingsPanel extends JPanel {
    public static final int DEFAULT_LINE_APPROX_NUM = 5;
    public static final int DEFAULT_CIRCLE_APPROX_NUM = 5;
    public static final int DEFAULT_M_LINES_NUM = 10;
    public static final String NUMBER_FORMAT_ERROR = "Only integer numbers are allowed in here";
    private final JSpinner lineApproxNum;
    private final JSpinner circleApproxNum;
    private final JSpinner mLines;
    private final SplinePanel splinePanel;
    private final JTextField activePoint;
    private final JTextField xActivePoint;
    private final JTextField yActivePoint;

    public EditorSettingsPanel(SplinePanel splinePanel) {
        super(new GridBagLayout());
        this.splinePanel = splinePanel;

        activePoint = new JTextField();
        activePoint.setEditable(false);
        xActivePoint = new JTextField();
        yActivePoint = new JTextField();

        int lineApproxValue = splinePanel.getModelFrame().getModelPanel().getLineApproxNum();
        lineApproxNum = new JSpinner(new SpinnerNumberModel(lineApproxValue, 1, 100, 1));
        int circleApproxValue = splinePanel.getModelFrame().getModelPanel().getCircleApproxNum();
        circleApproxNum = new JSpinner(new SpinnerNumberModel(circleApproxValue, 1, 100, 1));
        lineApproxNum.addChangeListener(e -> {
            int n = (int) lineApproxNum.getValue();
            if (n > 100) {
                lineApproxNum.setValue(100);
            } else if (n < 1) {
                lineApproxNum.setValue(1);
            }
            splinePanel.setApproxNum(n);
            splinePanel.drawSpline();
            splinePanel.repaint();
        });
        circleApproxNum.addChangeListener(e -> {
            int n = (int) circleApproxNum.getValue();
            if (n > 100) {
                circleApproxNum.setValue(100);
            } else if (n < 1) {
                circleApproxNum.setValue(1);
            }
        });
        int linesValue = splinePanel.getModelFrame().getModelPanel().getmLines();
        mLines = new JSpinner(new SpinnerNumberModel(linesValue, 3, 100, 1));
        mLines.addChangeListener(e -> {
            int n = (int) mLines.getValue();
            if (n > 100) {
                mLines.setValue(100);
            } else if (n < 3) {
                mLines.setValue(1);
            }
        });
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 0;
        c.insets = new Insets(10, 5, 0, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("point: "), c);

        activePoint.setPreferredSize(new Dimension(55, 27));
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.33;
        c.gridx = 1;
        add(activePoint, c);

        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 2;
        add(new JLabel("x: "), c);

        xActivePoint.setPreferredSize(new Dimension(55, 27));
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.33;
        c.gridx = 3;
        add(xActivePoint, c);

        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 4;
        add(new JLabel("y: "), c);

        yActivePoint.setPreferredSize(new Dimension(55, 27));
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.33;
        c.gridx = 5;
        add(yActivePoint, c);

        c.weightx = 0;
        c.insets = new Insets(10, 5, 0, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("n: "), c);

        lineApproxNum.setPreferredSize(new Dimension(55, 27));
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.33;
        c.gridx = 1;
        add(lineApproxNum, c);

        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 2;
        add(new JLabel("m: "), c);

        circleApproxNum.setPreferredSize(new Dimension(55, 27));
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.33;
        c.gridx = 3;
        add(circleApproxNum, c);

        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.gridx = 4;
        add(new JLabel("lines: "), c);

        mLines.setPreferredSize(new Dimension(55, 27));
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0.33;
        c.gridx = 5;
        add(mLines, c);

        xActivePoint.addActionListener(e -> {
            try {
                validateActivePointCoordinates();
                splinePanel.drawSpline();
                splinePanel.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, NUMBER_FORMAT_ERROR);
            }
        });
        xActivePoint.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    validateActivePointCoordinates();
                    splinePanel.drawSpline();
                    splinePanel.repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EditorSettingsPanel.this, NUMBER_FORMAT_ERROR);
                }
            }
        });

        yActivePoint.addActionListener(e -> {
            try {
                validateActivePointCoordinates();
                splinePanel.drawSpline();
                splinePanel.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, NUMBER_FORMAT_ERROR);
            }
        });

        yActivePoint.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    validateActivePointCoordinates();
                    splinePanel.drawSpline();
                    splinePanel.repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EditorSettingsPanel.this, NUMBER_FORMAT_ERROR);
                }
            }
        });
    }

    public void setDefault() {
        lineApproxNum.setValue(DEFAULT_LINE_APPROX_NUM);
        circleApproxNum.setValue(DEFAULT_CIRCLE_APPROX_NUM);
        mLines.setValue(DEFAULT_M_LINES_NUM);
    }

    public int getmLines() {
        return (int) mLines.getValue();
    }

    public int getCircleApproxNum() {
        return (int) circleApproxNum.getValue();
    }

    public int getLineApproxNum() {
        return (int) lineApproxNum.getValue();
    }

    public void setActivePoint(int number) {
        activePoint.setText(String.valueOf(number));
        setActivePointCoordinates((int) splinePanel.getPoint(number).x, (int) splinePanel.getPoint(number).y);
    }

    public void setActivePointCoordinates(int xActivePoint, int yActivePoint) {
        this.xActivePoint.setText(String.valueOf(xActivePoint));
        this.yActivePoint.setText(String.valueOf(yActivePoint));
    }

    public void validateActivePointCoordinates() {
        if (activePoint.getText().isEmpty()) return;
        splinePanel.setPointCoordinates(Integer.parseInt(activePoint.getText()),
                Integer.parseInt(xActivePoint.getText()), Integer.parseInt(yActivePoint.getText()));
    }
}
