package javax.microedition.lcdui;

public class List extends Screen {
    public static final int EXCLUSIVE = 1, MULTIPLE = 2, IMPLICIT = 3;
    public List(String title, int listType) { super(title); }
    public List(String title, int listType, String[] elements, Image[] imageElements) { super(title); }
    public int append(String str, Image img) { return 0; }
    public void delete(int i) {}
    public void deleteAll() {}
    public int size() { return 0; }
    public int getSelectedIndex() { return -1; }
    public void setSelectedIndex(int i, boolean selected) {}
    public String getString(int i) { return null; }
}
