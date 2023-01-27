package ru.nsu.fit.ejsvald.setting;

import ru.nsu.fit.ejsvald.filters.ImageScaler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.AffineTransformOp;
import java.io.IOException;

public class FitInSetPanel extends SettingsPanel {
    private final ImageScaler scaler;
    private JCheckBox bilinear;
    private JCheckBox bicubic;
    private JCheckBox neighbor;

    public FitInSetPanel(ImageScaler scaler) {
        super(new GridBagLayout());
        this.scaler = scaler;
        bilinear = addAndCreateCheckBox("Bilinear", 0, 0, false);
        bilinear.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (bicubic != null && bicubic.isSelected()) {
                    bicubic.setSelected(false);
                }
                if (neighbor != null && neighbor.isSelected()) {
                    neighbor.setSelected(false);
                }
            } else {
                if (bicubic != null && bicubic.isSelected()) {
                    bicubic.setSelected(true);
                }
                if (neighbor != null && neighbor.isSelected()) {
                    neighbor.setSelected(true);
                }
            }
        });
        bicubic = addAndCreateCheckBox("Bicubic", 0, 1, false);
        bicubic.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (bilinear != null && bilinear.isSelected()) {
                    bilinear.setSelected(false);
                }
                if (neighbor != null && neighbor.isSelected()) {
                    neighbor.setSelected(false);
                }
            } else {
                if (bilinear != null && bilinear.isSelected()) {
                    bilinear.setSelected(true);
                }
                if (neighbor != null && neighbor.isSelected()) {
                    neighbor.setSelected(true);
                }
            }
        });
        neighbor = addAndCreateCheckBox("Neighbor", 0, 2, false);
        neighbor.addItemListener(e -> {
            JCheckBox cb = (JCheckBox) e.getSource();
            if (cb.isSelected()) {
                if (bilinear != null && bilinear.isSelected()) {
                    bilinear.setSelected(false);
                }
                if (bicubic != null && bicubic.isSelected()) {
                    bicubic.setSelected(false);
                }
            } else {
                if (bilinear != null && bilinear.isSelected()) {
                    bilinear.setSelected(true);
                }
                if (bicubic != null && bicubic.isSelected()) {
                    bicubic.setSelected(true);
                }
            }
        });
        switch (scaler.getTransformType()) {
            case AffineTransformOp.TYPE_BICUBIC:
                bicubic.setSelected(true);
                break;
            case AffineTransformOp.TYPE_BILINEAR:
                bilinear.setSelected(true);
                break;
            case AffineTransformOp.TYPE_NEAREST_NEIGHBOR:
                neighbor.setSelected(true);
                break;
        }
    }

    @Override
    public void applySettings() throws IOException {
        if (bilinear.isSelected()) {
            scaler.setTransformType(AffineTransformOp.TYPE_BILINEAR);
        } else if (bicubic.isSelected()) {
            scaler.setTransformType(AffineTransformOp.TYPE_BICUBIC);
        } else if (neighbor.isSelected()) {
            scaler.setTransformType(AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        }
    }
}
