package javax.microedition.m3g;

public class Transform {
    private final float[] matrix = new float[16];

    public Transform() {
        for (int i = 0; i < 16; i++) {
            matrix[i] = i % 5 == 0 ? 1.0f : 0.0f;
        }
    }

    public void set(float[] matrix) {
        if (matrix == null || matrix.length < 16) {
            throw new IllegalArgumentException("A transform requires 16 values");
        }
        System.arraycopy(matrix, 0, this.matrix, 0, 16);
    }
}
