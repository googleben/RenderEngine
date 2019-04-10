package me.benwyatt.render.math;

public class Vector4 {

    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4() {
        this(0, 0, 0, 0);
    }

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4 dot(Vector4 v) {
        return new Vector4(x*v.x, y*v.y, z*v.z, w*v.w);
    }

    public Vector3 toVector3() {
        return new Vector3(x/w, y/w, z/w);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }
}
