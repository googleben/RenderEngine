package me.benwyatt.render;

import me.benwyatt.render.math.Vector3;

public class VertexOutput implements Cloneable {

    public Vector3 position;

    public VertexOutput(Vector3 position) {
        this.position = position;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed");
        }
    }

}
