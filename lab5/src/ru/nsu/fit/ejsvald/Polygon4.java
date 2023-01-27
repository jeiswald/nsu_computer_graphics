package ru.nsu.fit.ejsvald;

import java.awt.*;

public class Polygon4 {
    public Coordinates p1;
    public Coordinates p2;
    public Coordinates p3;
    public Coordinates p4;

    private Coordinates normal = null;
    private double dParam;

    private Color color;

    public Polygon4(Coordinates p1, Coordinates p2, Coordinates p3, Coordinates p4) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        color = new Color(111, 154, 255);
    }

    public Polygon4(Coordinates p1, Coordinates p2, Coordinates p3, Coordinates p4, Color color) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.color = color;
    }

    public Polygon4(Polygon4 polygon4) {
        this.p1 = polygon4.p1;
        this.p2 = polygon4.p2;
        this.p3 = polygon4.p3;
        this.p4 = polygon4.p4;
        this.color = polygon4.color;
    }

    private Coordinates calcOrientation() {
        Coordinates vector1 = new Coordinates(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        Coordinates vector2 = new Coordinates(p3.x - p2.x, p3.y - p2.y, p3.z - p2.z);
        Coordinates vector3 = new Coordinates(p4.x - p3.x, p4.y - p3.y, p4.z - p3.z);
        Coordinates vector4 = new Coordinates(p1.x - p4.x, p1.y - p4.y, p1.z - p4.z);
        normal = new Coordinates(vector1.y * vector2.z - vector1.z * vector2.y, vector1.z * vector2.x - vector1.x * vector2.z,
                (-1) * (vector1.x * vector2.y - vector1.y * vector2.x));
        if (Coordinates.calcInnerProduct(normal, new Coordinates(0, 0, 1)) == 0) {
            normal = new Coordinates(vector2.y * vector3.z - vector2.z * vector3.y, vector2.z * vector3.x - vector2.x * vector3.z,
                    (-1) * (vector2.x * vector3.y - vector2.y * vector3.x));
        }
        if (Coordinates.calcInnerProduct(normal, new Coordinates(0, 0, 1)) == 0) {

            normal = new Coordinates(vector3.y * vector4.z - vector3.z * vector4.y, vector3.z * vector4.x - vector3.x * vector4.z,
                    (-1) * (vector3.x * vector4.y - vector3.y * vector4.x));
        }
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

    public Color getColor() {
        return color;
    }
}
