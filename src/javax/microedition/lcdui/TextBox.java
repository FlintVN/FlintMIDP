package javax.microedition.lcdui;

public class TextBox extends Screen {
    private String text;
    private int maxSize;
    public TextBox(String title, String text, int maxSize, int constraints) {
        super(title); this.text = text; this.maxSize = maxSize;
    }
    public String getString() { return text; }
    public void setString(String s) { text = s; }
    public int getMaxSize() { return maxSize; }
    public void setMaxSize(int m) { maxSize = m; }
    public int size() { return text == null ? 0 : text.length(); }
}
