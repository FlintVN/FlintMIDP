package javax.microedition.lcdui;

public abstract class Item {
    private String label;
    private Form owner;
    Item(String label) { this.label = label; }
    public String getLabel() { return label; }
    public void setLabel(String l) { label = l; }
    public Form getOwner() { return owner; }
    void setOwner(Form f) { owner = f; }
}
