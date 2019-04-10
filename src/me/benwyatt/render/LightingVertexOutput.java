package me.benwyatt.render;

import me.benwyatt.render.math.Vector3;

public class LightingVertexOutput extends VertexOutput {

    @Interpolate
    public Vector3 normal;

    public LightingVertexOutput(Vector3 position, Vector3 normal) {
        super(position);
        this.normal = normal;
    }

}
