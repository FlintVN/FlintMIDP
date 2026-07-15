package board;

/* Native PNG decoder bridge. decode() returns the pixel buffer in Flint's RGB565/ARGB565
 * layout and fills wh = {width, height, format(2=RGB565,3=ARGB565)}, or null on failure. */
public final class Png {
    public static native byte[] decode(byte[] data, int off, int len, int[] wh);
}
