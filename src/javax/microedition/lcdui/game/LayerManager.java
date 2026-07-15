package javax.microedition.lcdui.game;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/* Holds a stack of layers and paints them through a view window (camera). Index 0 is the
 * front-most layer (painted last). */
public class LayerManager {
    private final Vector<Layer> layers = new Vector<>();
    private int vx, vy, vw = Integer.MAX_VALUE, vh = Integer.MAX_VALUE;

    public LayerManager() {}

    public void append(Layer l) { layers.addElement(l); }
    public void insert(Layer l, int index) { layers.insertElementAt(l, index); }
    public void remove(Layer l) { layers.removeElement(l); }
    public Layer getLayerAt(int index) { return layers.elementAt(index); }
    public int getSize() { return layers.size(); }
    public void setViewWindow(int x, int y, int width, int height) { vx = x; vy = y; vw = width; vh = height; }

    public void paint(Graphics g, int x, int y) {
        int cx = g.getClipX(), cy = g.getClipY(), cw = g.getClipWidth(), ch = g.getClipHeight();
        g.clipRect(x, y, vw, vh);
        g.translate(x - vx, y - vy);
        for (int i = layers.size() - 1; i >= 0; i--) {   // back (last) -> front (0)
            Layer l = layers.elementAt(i);
            if (l.isVisible()) l.paint(g);
        }
        g.translate(-(x - vx), -(y - vy));
        g.setClip(cx, cy, cw, ch);
    }
}
