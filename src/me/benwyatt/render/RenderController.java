package me.benwyatt.render;

import me.benwyatt.render.math.Mesh;
import me.benwyatt.render.math.Vector3;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RenderController {

    /**
     * The parameters passed in to vertex and pixel shaders
     */
    private HashMap<String, Object> params;
    /**
     * Whether a render pass is currently active
     */
    private boolean isPassActive;
    /**
     * The surface to render to
     */
    private RenderSurface surface;
    /**
     * The vertex (fragment) shader to be used
     */
    private VertexShader vertexShader;
    /**
     * The pixel shader to be used
     */
    private PixelShader pixelShader;

    /**
     * The depth buffer used to ensure correct back-to-front ordering
     */
    private float[][] depthBuffer;
    /**
     * Whether the depth buffer should be used
     */
    private boolean useDepthBuffer = true;

    public RenderController() {
        params = new HashMap<>();
    }

    /**
     * A method to be overridden in subclasses to pass in rendering parameters.
     * This function is called when a render pass is initiated.
     */
    public void setupParams() {}

    /**
     * Begins a render pass, locking in the rendering surface and shaders to be used during the pass.
     * @param surface The rendering surface to be used
     * @param vertexShader The vertex (fragment) shader to be used
     * @param pixelShader The pixel shader to be used
     * @param <T> The type of the output of the vertex shader
     */
    public<T extends VertexOutput> void beginRenderPass(RenderSurface surface, VertexShader<T> vertexShader, PixelShader<T> pixelShader) {
        if (this.isPassActive) throw new RenderException("Cannot begin a render pass while one is already active");
        this.isPassActive = true;
        this.surface = surface;
        this.vertexShader = vertexShader;
        this.pixelShader = pixelShader;
        depthBuffer = new float[surface.getWidth()][surface.getHeight()];
        for (float[] arr : depthBuffer) Arrays.fill(arr, Float.MAX_VALUE);
        setupParams();
    }

    /**
     * Ends a render pass, allowing a new one with a different surface or shaders to be initiated.
     */
    public void endRenderPass() {
        this.isPassActive = false;
    }

    /**
     * Renders a model using the stored shaders, parameters, and render surface.
     * @param model
     */
    public void render(Model model) {
        if (!isPassActive) throw new RenderException("Cannot render while a render pass is not active");
        Mesh mesh = model.getMesh();
        Vector3[]  vertices = mesh.getVertices();
        for (int[] t : mesh.getTriangles()) {
            Vector3 a = vertices[t[0]];
            Vector3 b = vertices[t[1]];
            Vector3 c = vertices[t[2]];
            VertexOutput aVert = vertexShader.getVertex(a, params);
            VertexOutput bVert = vertexShader.getVertex(b, params);
            VertexOutput cVert = vertexShader.getVertex(c, params);
            if (model.getRenderMethod()==RenderMethod.RASTERIZED) {
                rasterize(aVert, bVert, cVert);
            }
        }
    }

    /**
     * Computes the triple cross product of 3 2D points, taking z to be zero.
     * This function is useful for computing barycentric coordinates.
     * @param x Point 1's x
     * @param y Point 1's y
     * @param a Point 2
     * @param b Point 3
     * @return The triple cross product of all three points.
     */
    private static float sign(float x, float y, Vector3 a, Vector3 b) {
        return (x - b.getX()) * (a.getY() - b.getY()) - (a.getX() - b.getX()) * (y - b.getY());
    }

    /**
     * Takes a point in -1 to 1 screen space and converts it to full screen space (0 to width and 0 to height).
     * @param v The vector in normalized screen space
     * @return The vector in full screen space
     */
    private Vector3 toScreen(Vector3 v) {
        float width = surface.getWidth();
        float height = surface.getHeight();
        return new Vector3(-v.getX() * width + width/2f, (v.getY() * height + height/2f), v.getZ());
    }

    /**
     * Rasterizes a triangle.
     * @param aV A point of the triangle
     * @param bV A point of the triangle
     * @param cV A point of the triangle
     */
    private void rasterize(VertexOutput aV, VertexOutput bV, VertexOutput cV) {
        Vector3 aBase = aV.position;
        Vector3 bBase = bV.position;
        Vector3 cBase = cV.position;
        Vector3 a = toScreen(aBase);
        Vector3 b = toScreen(bBase);
        Vector3 c = toScreen(cBase);
        Vector3 left = a.getX() < b.getX() ? a : b;
        left = left.getX() < c.getX() ? left : c;
        float right = a.getX() > b.getX() ? a.getX() : b.getX();
        right = right > c.getX() ? right : c.getX();
        float top = a.getY() > b.getY() ? a.getY() : b.getY();
        top = top > c.getY() ? top : c.getY();
        float bottom = a.getY() < b.getY() ? a.getY() : b.getY();
        bottom = bottom < c.getY() ? bottom : c.getY();

        float area = sign(a.getX(), a.getY(), b, c);

        Class<?> cl = aV.getClass();
        ArrayList<Field> interpolate = new ArrayList<>();
        for (Field f : cl.getFields()) {
            if (f.getAnnotation(Interpolate.class) != null) interpolate.add(f);
        }

        VertexOutput vOut = (VertexOutput)aV.clone();

        for (int x = Math.max((int)left.getX()-1, 0); x <= right+1 && x < surface.getWidth(); x++) {
            for (int y = Math.max((int)bottom-1, 0); y <= top+1 && y < surface.getHeight(); y++) {
                float w0 = sign(x, y, a, b);
                float w1 = sign(x, y, b, c);
                float w2 = sign(x, y, c, a);
                if (w0 <= 0 && w1 <= 0 && w2 <= 0) {
                    w0 /= area;
                    w1 /= area;
                    w2 /= area;
                    float recipZ = a.getZ() * w0 + b.getZ() * w1 + c.getZ() * w2;
                    float z = 1 / recipZ;
                    if (useDepthBuffer) {
                        if (z > depthBuffer[x][y]) continue;
                        depthBuffer[x][y] = z;
                    }
                    w0 = Math.min(w0, 1);
                    w1 = Math.min(w1, 1);
                    w2 = Math.min(w2, 1);
                    for (Field f : interpolate) {
                        Vector3 aInt;
                        Vector3 bInt;
                        Vector3 cInt;
                        try {
                            aInt = (Vector3)f.get(aV);
                            bInt = (Vector3)f.get(bV);
                            cInt = (Vector3)f.get(cV);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Illegal access");
                        }
                        float xInt = aInt.getX() * w0 + bInt.getX() * w1 + cInt.getX() * w2;
                        float yInt = aInt.getY() * w0 + bInt.getY() * w1 + cInt.getY() * w2;
                        float zInt = aInt.getZ() * w0 + bInt.getZ() * w1 + cInt.getZ() * w2;
                        try {
                            f.set(vOut, new Vector3(x, y, z));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Illegal access");
                        }
                    }
                    vOut.position = new Vector3(Math.min(w0, 1), Math.min(w1, 1), Math.min(w2, 1));
                    Color color = pixelShader.getColor(vOut, params);
                    surface.setPixel(x, y, color);
                }
            }
        }
    }

    public boolean doesUseDepthBuffer() {
        return useDepthBuffer;
    }

    public void setUseDepthBuffer(boolean useDepthBuffer) {
        this.useDepthBuffer = useDepthBuffer;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

}
