package ru.nsu.fit.ejsvald.filters;

import java.awt.image.BufferedImage;

public class EmbossingFilter extends KernelFilter {
    private static final double[][] matrix = {{0, 1, 0}, {-1, 0, 1}, {0, -1, 0}};
    public EmbossingFilter() {
        super(matrix, 1, 128, 3);
    }
}
