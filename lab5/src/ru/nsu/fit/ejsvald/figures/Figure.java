package ru.nsu.fit.ejsvald.figures;

import ru.nsu.fit.ejsvald.Polygon4;

import java.util.List;

public abstract class Figure {
    protected List<Polygon4> polygons;

    public abstract List<Polygon4> getPolygons();
}
