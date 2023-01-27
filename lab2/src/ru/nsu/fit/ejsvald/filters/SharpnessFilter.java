package ru.nsu.fit.ejsvald.filters;

public class SharpnessFilter extends KernelFilter {
    private static final double[][] matrix = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
    public SharpnessFilter() {
        super(matrix, 1, 0, 3);
    }
}
