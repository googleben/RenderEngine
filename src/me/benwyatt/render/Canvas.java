package me.benwyatt.render;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Canvas extends Component implements MouseListener, MouseMotionListener, RenderSurface {

    private Color background;
    private BufferedImage canvas;
    private int width;
    private int height;

    private boolean dragging;
    private Point dragStart;
    private List<BiConsumer<Integer, Integer>> dragListeners = new ArrayList<>();

    public Canvas(int width, int height, Color background) {
        this.width = width;
        this.height = height;
        this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.background = background;
        addMouseListener(this);
        addMouseMotionListener(this);
        clear();
    }

    public Canvas(int width, int height) {
        this(width, height, Color.BLACK);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.drawImage(canvas, 0, 0, null);
    }

    public void setPixel(int x, int y, Color c) {
        if (x >= 0 && x < width && y >= 0 && y < height) canvas.setRGB(x, y, c.getRGB());
    }

    public Color getPixel(int x, int y) {
        return new Color(canvas.getRGB(x, y));
    }

    public void clear() {
        Graphics2D g = (Graphics2D)canvas.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
    }

    public void addDragListener(BiConsumer<Integer, Integer> func) {
        dragListeners.add(func);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if ((mouseEvent.getButton() & MouseEvent.BUTTON2) != 0) {
            dragging = true;
            dragStart = mouseEvent.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton()==MouseEvent.BUTTON2) {
            dragging = false;
            dragStart = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        if (dragging) {
            Point p = mouseEvent.getPoint();
            int dx = p.x - dragStart.x;
            int dy = p.y - dragStart.y;
            dragStart = p;
            for (BiConsumer<Integer, Integer> c : dragListeners) c.accept(dx, dy);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
