package javax.microedition.m3g;

public class Background extends Object3D {
    private int color;
    private boolean colorClearEnabled = true;
    private Image2D image;

    public int getColor() {
        return color;
    }

    public boolean isColorClearEnabled() {
        return colorClearEnabled;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColorClearEnable(boolean enable) {
        colorClearEnabled = enable;
    }

    public void setImage(Image2D image) {
        this.image = image;
    }
}
