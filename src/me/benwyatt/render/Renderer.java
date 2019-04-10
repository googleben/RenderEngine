package me.benwyatt.render;

import me.benwyatt.render.math.Matrix;
import me.benwyatt.render.math.Mesh;
import me.benwyatt.render.math.Vector3;

import javax.swing.*;
import java.awt.*;

public class Renderer extends JFrame {

    public static void main(String[] args) {
        new Renderer(800, 600).start();
    }

    private Model[] models;
    private Canvas canvas;
    private Camera camera;
    private RenderController controller = new RenderController();

    public Renderer(int width, int height) {
        canvas = new Canvas(width, height);
        add(canvas);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        models = new Model[] {
                new Model(Mesh.cube)
        };
        canvas.addDragListener((dx, dy) -> {
            Vector3 rot = camera.getRotation();
            rot.setX(rot.getX()-((int)((rot.getY()+3.14/2)/(2*3.14) % 2) == 0 ? 1 : 1) * dy/400f);
            rot.setY(rot.getY()-dx/400f);
        });
        camera = new Camera();
        camera.setPos(new Vector3(0, 0, 10));
        pack();
        setVisible(true);
    }

    @Override
    public Dimension getPreferredSize() {
        return canvas.getPreferredSize();
    }

    public void start() {
        new Thread(() -> {
            double timePerFrame = 1000d/60d;
            while (true) {
                long start = System.currentTimeMillis();
                runRender();
                long diff = System.currentTimeMillis()-start;
                if (diff < timePerFrame) {
                    try {
                        Thread.sleep((long)(timePerFrame-diff));
                    } catch (InterruptedException ignored) {}
                }
                endRender();
            }

        }).start();
    }

    private void endRender() {
        canvas.paint(getGraphics());
    }

    public void runRender() {
        canvas.clear();
        Matrix view = camera.view();
        Matrix proj = Matrix.buildPerspective(0.78f, canvas.getWidth()/(float)canvas.getHeight(), 0.01f, 100);
        controller.beginRenderPass(canvas,
                (inPos, params) -> new VertexOutput(
                        ((Matrix)params.get("matProjection"))
                                .multiply((Matrix)params.get("matWorld"))
                                .transform(inPos)),
                (vertexOut, params) -> new Color(vertexOut.position.getX(), vertexOut.position.getY(), vertexOut.position.getZ())
                );
        controller.getParams().put("matProjection", proj.multiply(view));
        for (Model m : models) {
            m.setupParams(controller.getParams());
            controller.render(m);
        }
        controller.endRenderPass();
    }

}
