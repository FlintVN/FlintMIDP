package javax.microedition.m3g;

public class Mesh extends Node {
    private Appearance[] appearances = new Appearance[1];

    public Appearance getAppearance(int index) {
        if (index < 0 || index >= appearances.length) {
            throw new IndexOutOfBoundsException();
        }
        return appearances[index];
    }

    public void setAppearance(int index, Appearance appearance) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (index >= appearances.length) {
            Appearance[] expanded = new Appearance[index + 1];
            System.arraycopy(appearances, 0, expanded, 0, appearances.length);
            appearances = expanded;
        }
        appearances[index] = appearance;
    }
}
