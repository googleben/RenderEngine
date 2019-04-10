package me.benwyatt.render.math;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Mesh {

    private Vector3[] vertices;
    private int[][] triangles;
    private Vector3[] normals;
    private int[][] normalIndices;

    public static final Mesh CUBE = new Mesh(new File("cube.obj"));
    public static final Mesh CYLINDER = new Mesh(new File("cylinder.obj"));
    public static final Mesh MONKEY = new Mesh(new File("monkey.obj"));

    public Mesh(Vector3[] vertices, int[][] triangles, Vector3[] normals, int[][] normalIndices) {
        this.vertices = vertices;
        this.triangles = triangles;
        this.normals = normals;
        this.normalIndices = normalIndices;
        for (int[] arr : this.triangles)
            if (arr.length!=3)
                throw new IllegalArgumentException("Triangle length must be 3");
    }

    public Mesh(File file) {
        Scanner sc;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find file "+file.getAbsolutePath());
        }
        ArrayList<Vector3> vertices = new ArrayList<>();
        ArrayList<int[]> faces = new ArrayList<>();
        ArrayList<Vector3> normals = new ArrayList<>();
        ArrayList<int[]> normalIndices = new ArrayList<>();
        while (sc.hasNextLine()) {
            String lineS = sc.nextLine();
            if (lineS.isEmpty()) continue;
            String[] line = lineS.split(" ");
            String marker = line[0];
            if (marker.equals("v")) {
                float x = Float.parseFloat(line[1]);
                float y = Float.parseFloat(line[2]);
                float z = Float.parseFloat(line[3]);
                vertices.add(new Vector3(x, y, z));
            }
            if (marker.equals("vn")) {
                float x = Float.parseFloat(line[1]);
                float y = Float.parseFloat(line[2]);
                float z = Float.parseFloat(line[3]);
                normals.add(new Vector3(x, y, z));
            }
            if (marker.equals("f")) {
                int[] face = new int[line.length-1];
                int[] normal = new int[line.length-1];
                for (int i = 1; i < line.length; i++) {
                    String[] sp = line[i].split("//");
                    face[i-1] = Integer.parseInt(sp[0])-1;
                    normal[i-1] = Integer.parseInt(sp[1])-1;
                }
                faces.add(face);
                normalIndices.add(normal);
            }
        }
        this.vertices = vertices.toArray(new Vector3[0]);
        this.triangles = faces.toArray(new int[0][]);
        this.normals = normals.toArray(new Vector3[0]);
        this.normalIndices = normalIndices.toArray(new int[0][]);
    }

    public Vector3[] getVertices() {
        return vertices;
    }

    public int[][] getTriangles() {
        return triangles;
    }

}
