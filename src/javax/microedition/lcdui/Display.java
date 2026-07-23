package javax.microedition.lcdui;

import board.Lcd;

/* MIDP Display + the (synchronous) paint pump. One screen-sized RGB565 buffer is shared
 * by all paints; repaint() renders the current Canvas into it and pushes it to the panel
 * via Lcd.present(). No render thread for v1 — the game's own loop thread calls repaint(). */
public class Display {
    private static Display instance;

    private static int sw, sh;
    private static String presentMode;
    private static byte[] screenBuf;
    private static Graphics screenGfx;

    private Displayable current;

    private Display() {}

    /** Launcher boot: bring up the LCD and logical MIDP framebuffer. (Not MIDP API.)
     *  Game launchers select their expected dimensions with flint.lcdui.width and
     *  flint.lcdui.height. Native Lcd.present maps the logical buffer to the
     *  physical panel, so a 320x240 game still receives a landscape Canvas on
     *  FlintOS's 240x320 display. */
    static synchronized void initScreen() {
        int panelWidth = Lcd.width();
        int panelHeight = Lcd.height();
        int width = displayDimension("flint.lcdui.width", panelWidth);
        int height = displayDimension("flint.lcdui.height", panelHeight);
        String mode = System.getProperty("flint.lcdui.present");
        initScreen(width, height, mode);
    }

    /** Explicit launcher configuration. Keeps game display mode in Main. */
    static synchronized void initScreen(int width, int height, String mode) {
        if(screenBuf != null) return;
        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid MIDP display: " + width + "x" + height);
        }

        /* Java launcher owns game selection. Configure native presentation
         * before the framebuffer is made, then use resulting logical size. */
        int panelWidth = Lcd.width();
        int panelHeight = Lcd.height();
        sw = width;
        sh = height;
        presentMode = mode;
        if(presentMode == null || presentMode.length() == 0) presentMode = "direct";
        Lcd.init(sw, sh, presentMode);

        long byteCount = (long)sw * (long)sh * 2L;
        if(byteCount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("MIDP display is too large: " + sw + "x" + sh);
        }
        screenBuf = new byte[(int)byteCount];
        flint.drawing.Graphics fg = flint.drawing.Graphics.create(sw, sh, screenBuf);
        screenGfx = new Graphics(fg, sw, sh);
        System.out.println("MIDP display: logical " + sw + "x" + sh
                + ", panel " + panelWidth + "x" + panelHeight
                + ", present " + presentMode);
        if(instance == null) instance = new Display();
    }

    private static int displayDimension(String property, int fallback) {
        String value = System.getProperty(property);
        if(value == null || value.length() == 0) return fallback;
        try {
            int dimension = Integer.parseInt(value);
            if(dimension <= 0) throw new NumberFormatException(value);
            return dimension;
        } catch(NumberFormatException exception) {
            System.out.println("Invalid " + property + ": " + value + "; using " + fallback);
            return fallback;
        }
    }

    private static void present() {
        Lcd.present(screenBuf);
    }

    public static Display getDisplay(javax.microedition.midlet.MIDlet m) {
        if(instance == null) instance = new Display();
        return instance;
    }

    public void setCurrent(Displayable d) {
        System.out.println("[setCurrent] " + (d == null ? "null" : d.getClass().getName()));
        current = d;
        if(d instanceof Canvas) {
            ((Canvas) d).showNotify();
            requestPaint((Canvas) d);
        }
    }
    public void setCurrent(Alert alert, Displayable next) {
        /* No alert UI yet — go straight to the next displayable. */
        setCurrent(next);
    }
    public Displayable getCurrent() { return current; }

    public boolean isColor() { return true; }
    public int numColors() { return 65536; }
    public int numAlphaLevels() { return 1; }
    public void callSerially(Runnable r) { if(r != null) r.run(); }
    public boolean flashBacklight(int ms) { return false; }
    public boolean vibrate(int ms) { return false; }

    /* ---- GameCanvas hooks: shared screen framebuffer + present ---- */
    private static long flushCount = 0;
    public static Graphics gameGraphics() { return screenGfx; }
    public static void flush() {
        if(screenBuf == null) return;
        present();
        if((flushCount++ % 30) == 0) System.out.println("flush #" + flushCount);
    }

    /* ---- internal pump ---- */
    static int screenWidth()  { return sw; }
    static int screenHeight() { return sh; }
    static Displayable currentShown() { return instance == null ? null : instance.current; }

    private static int paintCount = 0;
    private static boolean painting;
    static synchronized void requestPaint(Canvas c) {
        if(screenGfx == null || c == null) return;
        if(painting) return;
        painting = true;
        try {
            screenGfx.reset();
            c.paint(screenGfx);
            present();
            if((paintCount++ % 10) == 0)
                System.out.println("paint #" + paintCount);
        } catch(Throwable error) {
            System.out.print("Paint error: ");
            error.printStackTrace();
            present();
        } finally {
            painting = false;
        }
    }
}
