package javax.microedition.m3g;

public class Appearance extends Object3D {
    private Texture2D[] textures = new Texture2D[2];

    public Texture2D getTexture(int index) {
        return index < textures.length ? textures[index] : null;
    }

    public void setTexture(int index, Texture2D texture) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (index >= textures.length) {
            Texture2D[] expanded = new Texture2D[index + 1];
            System.arraycopy(textures, 0, expanded, 0, textures.length);
            textures = expanded;
        }
        textures[index] = texture;
    }
}
