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

    public Coordinates(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        w = 1;
    }

    public Coordinates scale(double sx, double sy, double sz) {
        double[][] matrix = {{sx, 0, 0, 0}, {0, sy, 0, 0}, {0, 0, sz, 0}, {0, 0, 0, 1}};
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, matrix, 4);
        x = result[0];
        y = result[1];
        z = result[2];
        w = result[3];
        return this;
    }

    public Coordinates translate(double tx, double ty, double tz) {
        double[][] matrix = {{1, 0, 0, tx}, {0, 1, 0, ty}, {0, 0, 1, tz}, {0, 0, 0, 1}};
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, matrix, 4);
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

    public Coordinates project(double Sw, double Sh, double Zf, double Zb) {
        double[][] boxProjectionMatrix = {{2.0 * Zf / Sw, 0, 0, 0}, {0, 2.0 * Zf / Sh, 0, 0},
                {0, 0, Zb / (Zb - Zf), (-Zf * Zb) / (Zb - Zf)}, {0, 0, 1, 0}};
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, boxProjectionMatrix, 4);
        x = result[0];
        y = result[1];
        z = result[2];
        w = result[3];
        return this;
    }

    public Coordinates applyMatrix(double[][] matrix) {
        double[] vector = {x, y, z, w};
        double[] result = mulMatrixToVector(vector, matrix, 4);
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

    public static double calcAngle(Coordinates v1, Coordinates v2) {
        double cos = (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z) / (calcVectorLength(v1) * calcVectorLength(v2));
        cos = (double) Math.round(cos * 100000) / 100000;
        return Math.acos(cos);
    }

    public static double calcInnerProduct(Coordinates v1, Coordinates v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static double calcVectorLength(Coordinates vector) {
        return Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
    }

    public static Coordinates calcNormVector(Coordinates vector1, Coordinates vector2) {
        return new Coordinates(vector1.y * vector2.z - vector1.z * vector2.y, vector1.z * vector2.x - vector1.x * vector2.z,
                (-1) * (vector1.x * vector2.y - vector1.y * vector2.x));
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
