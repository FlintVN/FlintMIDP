package javax.microedition.m3g;

public final class Graphics3D {
    private static final Graphics3D INSTANCE = new Graphics3D();
    private Object target;

    private Graphics3D() {
    }

    public static Graphics3D getInstance() {
        return INSTANCE;
    }

    public void bindTarget(Object target) {
        if (target == null) {
            throw new NullPointerException();
        }
        if (this.target != null) {
            throw new IllegalStateException("A rendering target is already bound");
        }
        this.target = target;
    }

    public void clear(Background background) {
        requireTarget();
    }

    public void releaseTarget() {
        target = null;
    }

    public void render(Node node, Transform transform) {
        requireTarget();
    }

    public void setCamera(Camera camera, Transform transform) {
    }

    private void requireTarget() {
        if (target == null) {
            throw new IllegalStateException("No rendering target is bound");
        }
    }
}
