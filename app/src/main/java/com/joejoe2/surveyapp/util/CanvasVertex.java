package com.joejoe2.surveyapp.util;

public class CanvasVertex {
    private float x, y;
    private int id;

    public CanvasVertex(float x, float y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public String toString()
    {
        return x + "-" + y + "-" +id;
    }

    public int hashCode()
    {
        return toString().substring(0, toString().lastIndexOf("-")).hashCode();
    }

    public boolean equals(Object o)
    {
        return (o instanceof CanvasVertex) && (toString().substring(0, toString().lastIndexOf("-")).equals(o.toString().substring(0, o.toString().lastIndexOf("-"))));
    }
}
