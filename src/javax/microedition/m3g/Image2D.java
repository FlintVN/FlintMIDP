package javax.microedition.m3g;

public class Image2D extends Object3D {
    public static final int ALPHA = 96;
    public static final int LUMINANCE = 97;
    public static final int LUMINANCE_ALPHA = 98;
    public static final int RGB = 99;
    public static final int RGBA = 100;

    private final int format;
    private final Object image;

    public Image2D(int format, Object image) {
        if (image == null) {
            throw new NullPointerException();
        }
        this.format = format;
        this.image = image;
    }
}
