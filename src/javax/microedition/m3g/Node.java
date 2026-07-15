package javax.microedition.m3g;

public class Node extends Object3D {
    private boolean renderingEnabled = true;

    public boolean isRenderingEnabled() {
        return renderingEnabled;
    }

    public void setRenderingEnable(boolean enable) {
        renderingEnabled = enable;
    }
}
