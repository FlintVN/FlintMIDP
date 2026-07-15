package javax.microedition.m3g;

public class Camera extends Node {
    public void setPerspective(float fovy, float aspectRatio, float near, float far) {
        if (fovy <= 0.0f || fovy >= 180.0f || aspectRatio <= 0.0f || near <= 0.0f || far <= near) {
            throw new IllegalArgumentException("Invalid perspective projection");
        }
    }
}
