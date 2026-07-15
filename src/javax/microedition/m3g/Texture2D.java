package javax.microedition.m3g;

public class Texture2D extends Object3D {
    private Image2D image;

    public Texture2D(Image2D image) {
        if (image == null) {
            throw new NullPointerException();
        }
        this.image = image;
    }

    public void setImage(Image2D image) {
        if (image == null) {
            throw new NullPointerException();
        }
        this.image = image;
    }
}
