package me.benwyatt.render;

import me.benwyatt.render.math.Matrix;
import me.benwyatt.render.math.Vector3;

public class Camera {

    private Vector3 pos;
    private Vector3 target;
    private Vector3 up;
    private Vector3 rotation;

    public Camera() {
        this(new Vector3(), new Vector3(), new Vector3(0, 1, 0), new Vector3());
    }

    public Camera(Vector3 pos, Vector3 target, Vector3 up, Vector3 rotation) {
        this.pos = pos;
        this.target = target;
        this.up = up;
        this.rotation = rotation;
    }

    public Matrix view() {
        Vector3 pos = Matrix.buildRotation(rotation).transform(this.pos);
        Vector3 vz = pos.subtract(target).normalize();
        Vector3 vx = up.cross(vz).normalize();
        Vector3 vy = vz.cross(vx);
        return new Matrix(vx.getX(), vy.getX(), vz.getX(), pos.getX(),
                          vx.getY(), vy.getY(), vz.getY(), pos.getY(),
                          vx.getZ(), vy.getZ(), vz.getZ(), pos.getZ(),
                          0, 0, 0, 1).invert();
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public Vector3 getTarget() {
        return target;
    }

    public void setTarget(Vector3 target) {
        this.target = target;
    }

    public Vector3 getUp() {
        return up;
    }

    public void setUp(Vector3 up) {
        this.up = up;
    }
}
