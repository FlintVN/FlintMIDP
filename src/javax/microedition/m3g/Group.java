package javax.microedition.m3g;

public class Group extends Node {
    private Node[] children = new Node[4];
    private int childCount;

    public void addChild(Node child) {
        if (child == null) {
            throw new NullPointerException();
        }
        if (childCount == children.length) {
            Node[] expanded = new Node[children.length * 2];
            System.arraycopy(children, 0, expanded, 0, childCount);
            children = expanded;
        }
        children[childCount++] = child;
    }

    public Node getChild(int index) {
        if (index < 0 || index >= childCount) {
            throw new IndexOutOfBoundsException();
        }
        return children[index];
    }

    public int getChildCount() {
        return childCount;
    }

    public Object3D find(int userId) {
        Object3D found = super.find(userId);
        for (int i = 0; found == null && i < childCount; i++) {
            found = children[i].find(userId);
        }
        return found;
    }
}
