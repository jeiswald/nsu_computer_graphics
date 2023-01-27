package ru.nsu.fit.ejsvald;

public class Camera {
    private Coordinates xCam = new Coordinates(0, 0, -1);
    private Coordinates yCam = new Coordinates(0, 1, 0);
    private Coordinates zCam = new Coordinates(1, 0, 0);

    private Coordinates viewPoint;

    private double viewPortWidth;
    private double viewPortHeight;

    private double SW = 2;
    private double SH = 2;
    private double Zf = (int) (40.0 / Math.sin(15 * Math.PI / 180)) / 2;
    private double Zb = 160;

    private double[][] rotationMatrix = new double[][]{{0, 0, -1, 0}, {0, 1, 0, 0}, {1, 0, 0, 0}, {0, 0, 0, 1}};

    private final Coordinates positionInWorld;

    public Camera(Coordinates positionInWorld, Coordinates viewPoint) {
        this.positionInWorld = positionInWorld;
        this.viewPoint = viewPoint;

        double camDist = (40.0 / Math.sin(15 * Math.PI / 180)) / 2;
        Zf = 30.0 / 2;
        Zb = camDist + 30 + 30;
    }

    public Camera(Coordinates positionInWorld, Coordinates viewPoint, double viewPortWidth, double viewPortHeight) {
        this.positionInWorld = positionInWorld;
        this.viewPoint = viewPoint;

        this.viewPortWidth = viewPortWidth;
        this.viewPortHeight = viewPortHeight;

        double camDist = (40.0 / Math.sin(15 * Math.PI / 180)) / 2;
        Zf = 30.0 / 2;
        Zb = camDist + 30 + 30;
    }

    public Camera(Coordinates positionInWorld, Coordinates xCam, Coordinates yCam, Coordinates zCam) {
        this.positionInWorld = positionInWorld;
        this.xCam = xCam;
        this.yCam = yCam;
        this.zCam = zCam;
        rotationMatrix = new double[][]{{xCam.x, xCam.y, xCam.z, 0}, {yCam.x, yCam.y, yCam.z, 0},
                {zCam.x, zCam.y, zCam.z, 0}, {0, 0, 0, 1}};
    }

    public void rotate(double xAngle, double yAngle, double zAngle) {
        positionInWorld.translate(-viewPoint.x, -viewPoint.y, -viewPoint.z);
        Coordinates rotVector = Coordinates.calcNormVector(zCam, new Coordinates(0, 1, 0));
        double rotOxAngle = Math.PI - Coordinates.calcAngle(rotVector, new Coordinates(1, 0, 0));
        positionInWorld.translate(viewPoint.x, viewPoint.y, viewPoint.z);
        if (rotVector.z < 0) {
            rotOxAngle = -rotOxAngle;
        }
        positionInWorld.rotateY(rotOxAngle).rotateX(xAngle).rotateY(-rotOxAngle);
        positionInWorld.rotateY(yAngle);

        xCam.rotateY(rotOxAngle).rotateX(xAngle).rotateY(-rotOxAngle);
        yCam.rotateY(rotOxAngle).rotateX(xAngle).rotateY(-rotOxAngle);
        zCam.rotateY(rotOxAngle).rotateX(xAngle).rotateY(-rotOxAngle);
        xCam.rotateY(yAngle);
        yCam.rotateY(yAngle);
        zCam.rotateY(yAngle);

        rotationMatrix = new double[][]{{xCam.x, xCam.y, xCam.z, 0}, {yCam.x, yCam.y, yCam.z, 0},
                {zCam.x, zCam.y, zCam.z, 0}, {0, 0, 0, 1}};

    }

    public void translate(int tx, int ty, int tz) {
        positionInWorld.translate(tx * xCam.x, tx * xCam.y, tx * xCam.z);
        positionInWorld.translate(ty * yCam.x, ty * yCam.y, ty * yCam.z);
        positionInWorld.translate(tz * zCam.x, tz * zCam.y, tz * zCam.z);
    }

    public void shiftTowardViewPoint(double coeff) {
        positionInWorld.translate(-viewPoint.x, -viewPoint.y, -viewPoint.z);
        positionInWorld.scale(coeff, coeff, coeff);
        positionInWorld.translate(viewPoint.x, viewPoint.y, viewPoint.z);
    }

    public Coordinates translateToCamCoordinates(Coordinates point) {
        return new Coordinates(point).translate(-positionInWorld.x, -positionInWorld.y, -positionInWorld.z)
                .applyMatrix(rotationMatrix);
    }

    public Coordinates calcRealSizeProjection(Coordinates point, int boxSize) {
        Coordinates projectedPoint = new Coordinates(point);
        return projectedPoint.scale(1.0 / boxSize, 1.0 / boxSize, 1.0 / boxSize)
                .project(SW, SH, Zf, Zb).normalize(projectedPoint.w)
                .scale(boxSize, boxSize, boxSize);
    }

    public void scaleZf(double coeff) {
        Zf *= coeff;
    }

    public void scaleZb(double coeff) {
        Zb *= coeff;
    }

    public Coordinates getxCam() {
        return xCam;
    }

    public Coordinates getyCam() {
        return yCam;
    }

    public Coordinates getzCam() {
        return zCam;
    }

    public double getViewPortWidth() {
        return viewPortWidth;
    }

    public double getViewPortHeight() {
        return viewPortHeight;
    }

    public double getZf() {
        return Zf;
    }

    public Coordinates getPositionInWorld() {
        return positionInWorld;
    }
}