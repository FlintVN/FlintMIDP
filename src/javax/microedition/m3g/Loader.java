package javax.microedition.m3g;

import java.io.IOException;

public final class Loader {
    private Loader() {
    }

    public static Object3D[] load(String name) throws IOException {
        throw new IOException("M3G scene loading is not implemented: " + name);
    }
}
