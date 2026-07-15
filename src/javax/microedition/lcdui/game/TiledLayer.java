package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* Grid of tiles drawn from a tilesheet Image. Tile index 0 = empty, 1..N = static tiles
 * (row-major in the sheet), negative = animated tile (maps to a static tile). */
public class TiledLayer extends Layer {
    private final int cols, rows, tileW, tileH, tilesPerRow;
    private final Image image;
    private final int[][] cells;          // [row][col]
    private int[] anim = new int[0];      // animated tile -> static tile index

    public TiledLayer(int columns, int rows, Image image, int tileWidth, int tileHeight) {
        super(columns * tileWidth, rows * tileHeight);
        this.cols = columns; this.rows = rows;
        this.image = image; this.tileW = tileWidth; this.tileH = tileHeight;
        this.tilesPerRow = Math.max(1, image.getWidth() / tileWidth);
        this.cells = new int[rows][columns];
    }

    public void setCell(int col, int row, int tileIndex) { cells[row][col] = tileIndex; }
    public int getCell(int col, int row) { return cells[row][col]; }
    public void fillCells(int col, int row, int numCols, int numRows, int tileIndex) {
        for (int r = row; r < row + numRows; r++)
            for (int c = col; c < col + numCols; c++) cells[r][c] = tileIndex;
    }
    public int getColumns() { return cols; }
    public int getRows() { return rows; }
    public int getCellWidth() { return tileW; }
    public int getCellHeight() { return tileH; }

    public int createAnimatedTile(int staticTileIndex) {
        int[] n = new int[anim.length + 1];
        System.arraycopy(anim, 0, n, 0, anim.length);
        n[anim.length] = staticTileIndex;
        anim = n;
        return -anim.length;              // animated indices are -1, -2, ...
    }
    public void setAnimatedTile(int animatedTileIndex, int staticTileIndex) { anim[-animatedTileIndex - 1] = staticTileIndex; }
    public int getAnimatedTile(int animatedTileIndex) { return anim[-animatedTileIndex - 1]; }

    public void paint(Graphics g) {
        if (!visible) return;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int t = cells[r][c];
                if (t == 0) continue;
                if (t < 0) t = anim[-t - 1];
                if (t == 0) continue;
                int idx = t - 1;          // 1-based -> 0-based
                int sx = (idx % tilesPerRow) * tileW;
                int sy = (idx / tilesPerRow) * tileH;
                g.drawRegion(image, sx, sy, tileW, tileH, 0, x + c * tileW, y + r * tileH, Graphics.TOP | Graphics.LEFT);
            }
        }
    }
}
