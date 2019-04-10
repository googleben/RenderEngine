package me.benwyatt.render;

import java.awt.*;
import java.util.HashMap;

@FunctionalInterface
public interface PixelShader<T extends VertexOutput> {

    Color getColor(T vertexOut, HashMap<String, Object> params);

}
