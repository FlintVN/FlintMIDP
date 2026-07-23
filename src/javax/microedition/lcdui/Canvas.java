package javax.microedition.lcdui;

public abstract class Canvas extends Displayable {
    /* game actions */
    public static final int UP = 1, LEFT = 2, RIGHT = 5, DOWN = 6, FIRE = 8;
    public static final int GAME_A = 9, GAME_B = 10, GAME_C = 11, GAME_D = 12;
    /* key codes */
    public static final int KEY_NUM0 = 48, KEY_NUM1 = 49, KEY_NUM2 = 50, KEY_NUM3 = 51,
                            KEY_NUM4 = 52, KEY_NUM5 = 53, KEY_NUM6 = 54, KEY_NUM7 = 55,
                            KEY_NUM8 = 56, KEY_NUM9 = 57, KEY_STAR = 42, KEY_POUND = 35;

    private boolean fullScreen = false;

    protected Canvas() {}

    @Override public int getWidth()  { return Display.screenWidth(); }
    @Override public int getHeight() { return Display.screenHeight(); }

    public boolean isDoubleBuffered() { return true; }
    public boolean hasPointerEvents() { return false; }
    public boolean hasPointerMotionEvents() { return false; }
    public boolean hasRepeatEvents() { return false; }
    public void setFullScreenMode(boolean mode) { fullScreen = mode; }

    /* keypad mapping (numeric keypad → game actions); refined when input lands */
    public int getGameAction(int keyCode) {
        switch(keyCode) {
            case KEY_NUM2: return UP;
            case KEY_NUM8: return DOWN;
            case KEY_NUM4: return LEFT;
            case KEY_NUM6: return RIGHT;
            case KEY_NUM5: return FIRE;
            case KEY_NUM1: return GAME_A;
            case KEY_NUM3: return GAME_B;
            case KEY_NUM7: return GAME_C;
            case KEY_NUM9: return GAME_D;
            default: return 0;
        }
    }
    public int getKeyCode(int gameAction) {
        switch(gameAction) {
            case UP: return KEY_NUM2;
            case DOWN: return KEY_NUM8;
            case LEFT: return KEY_NUM4;
            case RIGHT: return KEY_NUM6;
            case FIRE: return KEY_NUM5;
            default: return 0;
        }
    }
    public String getKeyName(int keyCode) { return String.valueOf((char) keyCode); }

    /* overridden by the game */
    protected void keyPressed(int keyCode) {}
    protected void keyReleased(int keyCode) {}
    protected void keyRepeated(int keyCode) {}
    protected void pointerPressed(int x, int y) {}
    protected void pointerReleased(int x, int y) {}
    protected void pointerDragged(int x, int y) {}
    protected void showNotify() {}
    protected void hideNotify() {}
    protected void sizeChanged(int w, int h) {}

    protected abstract void paint(Graphics g);

    public final void repaint() { Display.requestPaint(this); }
    public final void repaint(int x, int y, int w, int h) { Display.requestPaint(this); }
    public final void serviceRepaints() { Display.requestPaint(this); }

    /* dispatch helpers for the input layer (used later) */
    final void dispatchKeyPressed(int keyCode)  { keyPressed(keyCode); }
    final void dispatchKeyReleased(int keyCode) { keyReleased(keyCode); }
}
