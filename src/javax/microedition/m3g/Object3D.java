package javax.microedition.m3g;

public class Object3D {
    private int userId;

    public int animate(int worldTime) {
        return 0;
    }

    public Object3D duplicate() {
        return this;
    }

    public Object3D find(int userId) {
        return this.userId == userId ? this : null;
    }

    public int getUserID() {
        return userId;
    }

    public void setUserID(int userId) {
        this.userId = userId;
    }
}
