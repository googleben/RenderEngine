package me.benwyatt.render.math;

public class Vector3 {

    private float x;
    private float y;
    private float z;

    public Vector3() {
        this(0, 0, 0);
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 project2D(Matrix transformation, int width, int height) {
        Vector3 p = transformation.transform(this.toVector4()).toVector3();
        return new Vector3(-p.x * width + width/2f, (p.y * height + height/2f), p.z);
    }

    public float magnitude() {
        return (float)Math.sqrt(x*x+y*y+z*z);
    }

    public float dot(Vector3 v) {
        return x*v.x + y*v.y + z*v.z;
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    public Vector3 add(Vector3 v) {
        return new Vector3(x+v.x, y+v.y, z+v.z);
    }

    public Vector3 subtract(Vector3 v) {
        return new Vector3(x-v.x, y-v.y, z-v.z);
    }

    public Vector3 normalize() {
        float mag = (float)Math.sqrt(x*x + y*y + z*z);
        return new Vector3(x/mag, y/mag, z/mag);
    }

    @Override
    public String toString() {
        return "Vector3 ["+x+", "+y+", "+z+"]";
    }

    public Vector4 toVector4() {
        return new Vector4(x, y, z, 1);
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
}
