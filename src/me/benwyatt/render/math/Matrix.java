package me.benwyatt.render.math;

import java.util.Arrays;

public class Matrix {

    private float[][] values;

    public Matrix() {
        values = new float[4][4];
    }

    public Matrix(float r1c1, float r1c2, float r1c3, float r1c4,
                  float r2c1, float r2c2, float r2c3, float r2c4,
                  float r3c1, float r3c2, float r3c3, float r3c4,
                  float r4c1, float r4c2, float r4c3, float r4c4) {
        values = new float[][] {
                {r1c1, r1c2, r1c3, r1c4},
                {r2c1, r2c2, r2c3, r2c4},
                {r3c1, r3c2, r3c3, r3c4},
                {r4c1, r4c2, r4c3, r4c4}
        };
    }

    public Matrix(float r1c1, float r1c2, float r1c3,
                  float r2c1, float r2c2, float r2c3,
                  float r3c1, float r3c2, float r3c3) {
        this(r1c1, r1c2, r1c3, 0, r2c1, r2c2, r2c3, 0, r3c1, r3c2, r3c3, 0, 0, 0, 0, 1);
    }

    public float get(int r, int c) {
        return values[r][c];
    }

    public void set(int r, int c, float val) {
        values[r][c] = val;
    }

    public Vector4 transform(Vector4 v) {

        float x = v.getX()*values[0][0]+v.getY()*values[0][1]+v.getZ()*values[0][2]+v.getW()*values[0][3];
        float y = v.getX()*values[1][0]+v.getY()*values[1][1]+v.getZ()*values[1][2]+v.getW()*values[1][3];
        float z = v.getX()*values[2][0]+v.getY()*values[2][1]+v.getZ()*values[2][2]+v.getW()*values[2][3];
        float w = v.getX()*values[3][0]+v.getY()*values[3][1]+v.getZ()*values[3][2]+v.getW()*values[3][3];
        return new Vector4(x, y, z, w);
    }

    public Vector3 transform(Vector3 v) {
        return transform(v.toVector4()).toVector3();
    }

    public static Matrix buildScale(float x, float y, float z) {
        return new Matrix(x, 0, 0,
                          0, y, 0,
                          0, 0, z);
    }

    public static Matrix buildScale(Vector3 scale) {
        return buildScale(scale.getX(), scale.getY(), scale.getZ());
    }

    public static Matrix buildPerspective(float fov, float aspect, float near, float far) {
        float o_o_dep = 1 / (far-near);
        float a = 1 / (float)Math.tan(.5 * fov);
        float b = a / aspect;
        float c = far * o_o_dep;
        float d = (-far * near) * o_o_dep;
        return new Matrix(b, 0, 0, 0,
                          0, a, 0, 0,
                          0, 0, c, d,
                          0, 0, 1, 0);
    }

    public static Matrix buildRotationX(float angle) {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        return new Matrix(1, 0, 0,
                          0, cos, -sin,
                          0, sin, cos);
    }

    public static Matrix buildRotationY(float angle) {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        return new Matrix(cos, 0, sin,
                          0, 1, 0,
                          -sin, 0, cos);
    }

    public static Matrix buildRotationZ(float angle) {
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        return new Matrix(cos, -sin, 0,
                          sin, cos, 0,
                          0, 0, 1);
    }

    public static Matrix buildRotation(float angleX, float angleY, float angleZ) {
        return buildRotationX(angleX)
                .multiply(buildRotationY(angleY))
                .multiply(buildRotationZ(angleZ));
    }

    public static Matrix buildRotation(Vector3 rotation) {
        return buildRotation(rotation.getX(), rotation.getY(), rotation.getZ());
    }

    public static Matrix buildPosition(float x, float y, float z) {
        return new Matrix(1, 0, 0, x,
                          0, 1, 0, y,
                          0, 0, 1, z,
                          0, 0, 0, 1);
    }

    public static Matrix buildPosition(Vector3 position) {
        return buildPosition(position.getX(), position.getY(), position.getZ());
    }

    public Matrix multiply(Matrix m) {
        Matrix ans = new Matrix();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                float f = 0;
                for (int i = 0; i < 4; i++) {
                    f += m.get(i, c)*get(r, i);
                }
                ans.set(r, c, f);
            }
        }
        return ans;
    }

    public Matrix invert() {
        float A2323 = values[2][2] * values[3][3] - values[2][3] * values[3][2];
        float A1323 = values[2][1] * values[3][3] - values[2][3] * values[3][1];
        float A1223 = values[2][1] * values[3][2] - values[2][2] * values[3][1];
        float A0323 = values[2][0] * values[3][3] - values[2][3] * values[3][0];
        float A0223 = values[2][0] * values[3][2] - values[2][2] * values[3][0];
        float A0123 = values[2][0] * values[3][1] - values[2][1] * values[3][0];
        float A2313 = values[1][2] * values[3][3] - values[1][3] * values[3][2];
        float A1313 = values[1][1] * values[3][3] - values[1][3] * values[3][1];
        float A1213 = values[1][1] * values[3][2] - values[1][2] * values[3][1];
        float A2312 = values[1][2] * values[2][3] - values[1][3] * values[2][2];
        float A1312 = values[1][1] * values[2][3] - values[1][3] * values[2][1];
        float A1212 = values[1][1] * values[2][2] - values[1][2] * values[2][1];
        float A0313 = values[1][0] * values[3][3] - values[1][3] * values[3][0];
        float A0213 = values[1][0] * values[3][2] - values[1][2] * values[3][0];
        float A0312 = values[1][0] * values[2][3] - values[1][3] * values[2][0];
        float A0212 = values[1][0] * values[2][2] - values[1][2] * values[2][0];
        float A0113 = values[1][0] * values[3][1] - values[1][1] * values[3][0];
        float A0112 = values[1][0] * values[2][1] - values[1][1] * values[2][0];

        float det = values[0][0] * (values[1][1] * A2323 - values[1][2] * A1323 + values[1][3] * A1223)
                - values[0][1] * (values[1][0] * A2323 - values[1][2] * A0323 + values[1][3] * A0223)
                + values[0][2] * (values[1][0] * A1323 - values[1][1] * A0323 + values[1][3] * A0123)
                - values[0][3] * (values[1][0] * A1223 - values[1][1] * A0223 + values[1][2] * A0123);

        return new Matrix(
                det *  ( values[1][1] * A2323 - values[1][2] * A1323 + values[1][3] * A1223 ),
                det * -( values[0][1] * A2323 - values[0][2] * A1323 + values[0][3] * A1223 ),
                det *  ( values[0][1] * A2313 - values[0][2] * A1313 + values[0][3] * A1213 ),
                det * -( values[0][1] * A2312 - values[0][2] * A1312 + values[0][3] * A1212 ),
                det * -( values[1][0] * A2323 - values[1][2] * A0323 + values[1][3] * A0223 ),
                det *  ( values[0][0] * A2323 - values[0][2] * A0323 + values[0][3] * A0223 ),
                det * -( values[0][0] * A2313 - values[0][2] * A0313 + values[0][3] * A0213 ),
                det *  ( values[0][0] * A2312 - values[0][2] * A0312 + values[0][3] * A0212 ),
                det *  ( values[1][0] * A1323 - values[1][1] * A0323 + values[1][3] * A0123 ),
                det * -( values[0][0] * A1323 - values[0][1] * A0323 + values[0][3] * A0123 ),
                det *  ( values[0][0] * A1313 - values[0][1] * A0313 + values[0][3] * A0113 ),
                det * -( values[0][0] * A1312 - values[0][1] * A0312 + values[0][3] * A0112 ),
                det * -( values[1][0] * A1223 - values[1][1] * A0223 + values[1][2] * A0123 ),
                det *  ( values[0][0] * A1223 - values[0][1] * A0223 + values[0][2] * A0123 ),
                det * -( values[0][0] * A1213 - values[0][1] * A0213 + values[0][2] * A0113 ),
                det *  ( values[0][0] * A1212 - values[0][1] * A0212 + values[0][2] * A0112 )
        );
    }

    @Override
    public String toString() {
        return Arrays.deepToString(values);
    }
}
