package me.benwyatt.render.math;

import java.awt.*;

public class Mesh {

    private Vector3[] vertices;
    private int[][] triangles;

    public static final Mesh cube = new Mesh(new Vector3[] {
            new Vector3(1, -1, 1),
            new Vector3(1, -1, -1),
            new Vector3(1, 1, -1),
            new Vector3(1, 1, 1),
            new Vector3(-1, -1, 1),
            new Vector3(-1, -1, -1),
            new Vector3(-1, 1, -1),
            new Vector3(-1, 1, 1)
        },
        new int[][] {
            {4, 0, 3},
            {4, 3, 7},
            {0, 1, 2},
            {0, 2, 3},
            {1, 5, 6},
            {1, 6, 2},
            {5, 4, 7},
            {5, 7, 6},
            {7, 3, 2},
            {7, 2, 6},
            {0, 5, 1},
            {0, 4, 5}
        });

    private Color[] colors = {
            new Color(100, 100, 100),
            new Color(100, 100, 0),
            new Color(100, 0, 100),
            new Color(100, 0, 0),
            new Color(0, 100, 100),
            new Color(0, 100, 0),
            new Color(0, 0, 100),
            new Color(255, 255, 255),
            new Color(255, 255, 0),
            new Color(255, 0, 255),
            new Color(255, 0, 0),
            new Color(0, 255, 255),
            new Color(0, 255, 0),
            new Color(0, 0, 255)
    };
    private int colorInd = 0;


    public Mesh(Vector3[] vertices, int[][] traingles) {
        this.vertices = vertices;
        this.triangles = traingles;
        for (int[] arr : triangles)
            if (arr.length!=3)
                throw new IllegalArgumentException("Triangle length must be 3");
    }

    public Vector3[] getVertices() {
        return vertices;
    }

    public int[][] getTriangles() {
        return triangles;
    }

}
