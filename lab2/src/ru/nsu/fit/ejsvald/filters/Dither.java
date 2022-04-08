package ru.nsu.fit.ejsvald.filters;

import ru.nsu.fit.ejsvald.setting.DitheringSetPanel;

import java.awt.image.BufferedImage;

public class Dither extends Filter {
    public static String NAME = "Dither";

    private final DitherFloydSteinberg floydSteinberg;
    private final OrderedDithering orderedDithering;
    private boolean isFloyd = true;
    private boolean isOrdered = false;

    public Dither() {
        floydSteinberg = new DitherFloydSteinberg();
        orderedDithering = new OrderedDithering();
        settingsPanel = new DitheringSetPanel(this);
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        if (isFloyd) {
            return floydSteinberg.apply(image);
        }
        if (isOrdered) {
            return orderedDithering.apply(image);
        }
        return image;
    }

    public DitherFloydSteinberg getFloydSteinberg() {
        return floydSteinberg;
    }

    public OrderedDithering getOrderedDithering() {
        return orderedDithering;
    }

    public boolean isFloyd() {
        return isFloyd;
    }

    public void setFloyd(boolean floyd) {
        isFloyd = floyd;
    }

    public boolean isOrdered() {
        return isOrdered;
    }

    public void setOrdered(boolean ordered) {
        isOrdered = ordered;
    }

}
