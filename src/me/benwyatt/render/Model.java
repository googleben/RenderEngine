package me.benwyatt.render;

import me.benwyatt.render.math.Matrix;
import me.benwyatt.render.math.Mesh;
import me.benwyatt.render.math.Vector3;

import java.util.HashMap;

public class Model {

    private Mesh mesh;
    private RenderMethod renderMethod = RenderMethod.RASTERIZED;
    private Vector3 pos = new Vector3();
    private Vector3 rot = new Vector3();
    private Vector3 scale = new Vector3(1, 1, 1);

    public Model(Mesh mesh) {
        this.mesh = mesh;
    }

    public void setupParams(HashMap<String, Object> params) {
        Matrix position = Matrix.buildPosition(pos);
        Matrix rotation = Matrix.buildRotation(rot);
        Matrix world = position.multiply(rotation);
        params.put("matWorld", getWorldMatrix());
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public RenderMethod getRenderMethod() {
        return renderMethod;
    }

    public void setRenderMethod(RenderMethod renderMethod) {
        this.renderMethod = renderMethod;
    }

    public Matrix getWorldMatrix() {
        return Matrix.buildPosition(pos).multiply(Matrix.buildRotation(rot)).multiply(Matrix.buildScale(scale));
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public Vector3 getRot() {
        return rot;
    }

    public void setRot(Vector3 rot) {
        this.rot = rot;
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
    }

}
