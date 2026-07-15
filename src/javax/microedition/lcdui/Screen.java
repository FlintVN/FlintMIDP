package javax.microedition.lcdui;

/* Base for the high-level screens (Alert/Form/List/TextBox). The game subclasses these
 * (mostly for registration/options/dialog UI); for the Canvas-based main flow they are
 * minimal no-render stubs. */
public abstract class Screen extends Displayable {
    private String title;
    Screen(String title) { this.title = title; }
    public String getTitle() { return title; }
    public void setTitle(String t) { title = t; }
}
