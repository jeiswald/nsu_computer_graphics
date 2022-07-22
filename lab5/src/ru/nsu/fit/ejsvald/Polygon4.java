package ru.nsu.fit.ejsvald;

public class Polygon4 {
    public Coordinates p1;
    public Coordinates p2;
    public Coordinates p3;
    public Coordinates p4;

    private Coordinates normal = null;
    private double dParam;

    public Polygon4(Coordinates p1, Coordinates p2, Coordinates p3, Coordinates p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    public Polygon4(Polygon4 polygon4) {
        this.p1 = polygon4.p1;
        this.p2 = polygon4.p2;
        this.p3 = polygon4.p3;
        this.p4 = polygon4.p4;
    }

    private Coordinates calcOrientation() {
        Coordinates vector1 = new Coordinates(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        Coordinates vector2 = new Coordinates(p3.x - p2.x, p3.y - p2.y, p3.z - p2.z);
        normal = new Coordinates(vector1.y * vector2.z - vector1.z * vector2.y, vector1.z * vector2.x - vector1.x * vector2.z,
                (-1) * (vector1.x * vector2.y - vector1.y * vector2.x));
        return normal;
    }

    public double calcDPlaneParam() {
        dParam = normal.x * p1.x + normal.y * p1.y + normal.z * p1.z;
        return dParam;
    }

    public Coordinates getOrientation() {
        if (normal == null) calcOrientation();
        return normal;
    }

    public static double getAngle(Coordinates v1, Coordinates v2) {
        double cos = (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z) / (calcVectorLength(v1) * calcVectorLength(v2));
        return Math.acos(cos);
    }

    public static double calcVectorLength(Coordinates vector) {
        return Math.sqrt(vector.x * vector.x + vector.y*vector.y + vector.z*vector.z);
    }
}
