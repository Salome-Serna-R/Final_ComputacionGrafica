package geometry;

import math.Vector4;
import math.UVN4x4;
import math.Matrix4x4;
import display.Scene;
import display.Colour;
import display.Material;

public class Triangle implements IntersectableObject {
    Vector4 v0, v1, v2; // Original vertices
    Vector4 tv0, tv1, tv2; // Transformed to camera coordinates
    int colorIndex;
    int materialIndex;

    public Triangle(Vector4 v0, Vector4 v1, Vector4 v2, int colorIndex, int materialIndex) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
        this.colorIndex = colorIndex;
        this.materialIndex = materialIndex;
    }

    @Override
    public Solution intersect(Ray ray) {
        // Möller–Trumbore ray-triangle intersection
        Vector4 edge1 = Vector4.subtract(tv1, tv0);
        Vector4 edge2 = Vector4.subtract(tv2, tv0);

        Vector4 h = Vector4.crossProduct(ray.direction, edge2);
        double a = Vector4.dotProduct(edge1, h);
        if (Math.abs(a) < 1e-6) return null; // Parallel

        double f = 1.0 / a;
        Vector4 s = Vector4.subtract(ray.origin, tv0);
        double u = f * Vector4.dotProduct(s, h);
        if (u < 0.0 || u > 1.0) return null;

        Vector4 q = Vector4.crossProduct(s, edge1);
        double v = f * Vector4.dotProduct(ray.direction, q);
        if (v < 0.0 || u + v > 1.0) return null;

        double t = f * Vector4.dotProduct(edge2, q);
        if (t > 0.0001) {
            Vector4 point = ray.evaluate(t);
            Vector4 normal = Vector4.crossProduct(edge1, edge2);
            normal.normalize();
            Colour color = Scene.colors.get(colorIndex);
            Material mat = Scene.materials.get(materialIndex);
            return new Solution(point, normal, color, mat, t);
        } else {
            return null;
        }
    }

    @Override
    public void setCamera() {
        UVN4x4 uvn = Scene.camera.uvn;
        tv0 = Matrix4x4.times(uvn, v0);
        tv1 = Matrix4x4.times(uvn, v1);
        tv2 = Matrix4x4.times(uvn, v2);
    }

    public void reset() {
        tv0 = v0;
        tv1 = v1;
        tv2 = v2;
    }
}
