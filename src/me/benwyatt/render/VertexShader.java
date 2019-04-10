package me.benwyatt.render;

import me.benwyatt.render.math.Vector3;

import java.util.HashMap;

@FunctionalInterface
public interface VertexShader<T extends VertexOutput> {

    T getVertex(Vector3 inPos, HashMap<String, Object> params);

}
