package ru.nsu.fit.ejsvald;

public class Coordinates {
    public double x;
    public double y;
    public double z;
    public double w;

    public Coordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        w = 1;
    }

    public Coordinates(int[] coordinates) {
        this.x = coordinates[0];
        this.y = coordinates[1];
        this.z = coordinates[2];
        w = 1;
    }

    public Coordinates(Coordinates coordinates) {
        this.x = coordinates.x;
        this.y = coordinates.y;
        this.z = coordinates.z;
        this.w = coordinates.w;
    }

    public Coordinates translate(int tx, int ty, int tz) {
        double[][] projectionMatrix = {{1, 0, 0, tx}, {0, 1, 0, ty}, {0, 0, 1, tz}, {0, 0, 0, 1}};
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, projectionMatrix, 4);
        x = result[0];
        y = result[1];
        z = result[2];
        w = result[3];
        return this;
    }

    public Coordinates rotateX(double angle) {
        double[][] matrix = {{1, 0, 0, 0}, {0, Math.cos(angle), -Math.sin(angle), 0},
                {0, Math.sin(angle), Math.cos(angle), 0}, {0, 0, 0, 1}};
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, matrix, 4);
        x = result[0];
        y = result[1];
        z = result[2];
        w = result[3];
        return this;
    }

    public Coordinates rotateY(double angle) {
        double[][] matrix = {{Math.cos(angle), 0, Math.sin(angle), 0}, {0, 1, 0, 0},
                {-Math.sin(angle), 0, Math.cos(angle), 0}, {0, 0, 0, 1}};
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, matrix, 4);
        x = result[0];
        y = result[1];
        z = result[2];
        w = result[3];
        return this;
    }

    public Coordinates rotateZ(double angle) {
        double[][] matrix = {{Math.cos(angle), -Math.sin(angle), 0, 0},
                {Math.sin(angle), Math.cos(angle), 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, matrix, 4);
        x = result[0];
        y = result[1];
        z = result[2];
        w = result[3];
        return this;
    }

    public Coordinates project(int Sw, int Sh, int Zf, int Zb) {
        double[][] boxProjectionMatrix = {{2.0 * Zf / Sw, 0, 0, 0}, {0, 2.0 * Zf / Sh, 0, 0},
                {0, 0, (double) Zb / (Zb - Zf), (double) (-Zf * Zb) / (Zb - Zf)}, {0, 0, 1, 0}};
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, boxProjectionMatrix, 4);
        x = result[0];
        y = result[1];
        z = result[2];
        w = result[3];
        return this;
    }

    public Coordinates normalize(double normParam) {
        x = x / normParam;
        y = y / normParam;
        z = z / normParam;
        w = w / normParam;
        return this;
    }

    private double[] mulMatrixToVector(double[] vector, double[][] matrix, int size) {
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
        sb.append(x).append(',');
        sb.append(y).append(',');
        sb.append(z).append('\n');
        return sb.toString();
    }
}
