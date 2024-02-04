package ru.nsu.fit.ejsvald;

public class Coordinates {
    private double[] vector = new double[4];

    public Coordinates(int x, int y, int z) {
        vector[0] = x;
        vector[1] = y;
        vector[2] = z;
        vector[3] = 1;
    }

    public Coordinates(int[] coordinates) {
        vector[0] = coordinates[0];
        vector[1] = coordinates[1];
        vector[2] = coordinates[2];
        vector[3] = 1;
    }

    public Coordinates(Coordinates coordinates) {
        System.arraycopy(coordinates.vector, 0, vector, 0, vector.length);
    }

    public Coordinates translate(int tx, int ty, int tz) {
        double[][] projectionMatrix = {{1, 0, 0, tx}, {0, 1, 0, ty}, {0, 0, 1, tz}, {0, 0, 0, 1}};
        applyMatrix(projectionMatrix);
        return this;
    }

    public Coordinates rotateX(double angle) {
        double[][] matrix = {{1, 0, 0, 0}, {0, Math.cos(angle), -Math.sin(angle), 0},
                {0, Math.sin(angle), Math.cos(angle), 0}, {0, 0, 0, 1}};
        applyMatrix(matrix);
        return this;
    }

    public Coordinates rotateY(double angle) {
        double[][] matrix = {{Math.cos(angle), 0, Math.sin(angle), 0}, {0, 1, 0, 0},
                {-Math.sin(angle), 0, Math.cos(angle), 0}, {0, 0, 0, 1}};
        applyMatrix(matrix);
        return this;
    }

    public Coordinates rotateZ(double angle) {
        double[][] matrix = {{Math.cos(angle), -Math.sin(angle), 0, 0},
                {Math.sin(angle), Math.cos(angle), 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        applyMatrix(matrix);
        return this;
    }

    public Coordinates project(int Sw, int Sh, int Zf, int Zb) {
        double[][] boxProjectionMatrix = {{2.0 * Zf / Sw, 0, 0, 0}, {0, 2.0 * Zf / Sh, 0, 0},
                {0, 0, (double) Zb / (Zb - Zf), (double) (-Zf * Zb) / (Zb - Zf)}, {0, 0, 1, 0}};
        applyMatrix(boxProjectionMatrix);
        return this;
    }

    public Coordinates scale(double scaleParameter) {
        double[][] projectionMatrix = {{scaleParameter, 0, 0, 1}, {0, scaleParameter, 0, 1}, {0, 0, scaleParameter, 1}, {0, 0, 0, 1}};
        applyMatrix(projectionMatrix);
        return this;
    }

    public Coordinates normalize(double normParam) {
        for (var i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / normParam;
        }
        return this;
    }

    public double getX() {
        return vector[0];
    }

    public double getY() {
        return vector[1];
    }

    public double getZ() {
        return vector[2];
    }

    public double getW() {
        return vector[3];
    }

    public void setX(double x) {
        vector[0] = x;
    }

    public void setY(double y) {
        vector[1] = y;
    }
    private void applyMatrix(double[][] matrix) {
        vector = mulMatrixToVector(vector, matrix, 4);
    }

    private static double[] mulMatrixToVector(double[] vector, double[][] matrix, int size) {
        double[] vectorToReturn = new double[size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                vectorToReturn[i] += matrix[i][j] * vector[j];
            }
        }
        return vectorToReturn;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getX()).append(',');
        sb.append(getY()).append(',');
        sb.append(getZ()).append('\n');
        return sb.toString();
    }
}
