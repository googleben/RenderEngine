package me.benwyatt.render;

import java.awt.*;

public interface RenderSurface {

    void setPixel(int x, int y, Color c);

    int getWidth();
    int getHeight();

}
