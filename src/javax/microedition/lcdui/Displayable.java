package javax.microedition.lcdui;

public abstract class Displayable {
    private CommandListener commandListener;

    public int getWidth()  { return Display.screenWidth(); }
    public int getHeight() { return Display.screenHeight(); }
    public boolean isShown() { return Display.currentShown() == this; }

    public void addCommand(Command c) {}
    public void removeCommand(Command c) {}
    public void setCommandListener(CommandListener l) { commandListener = l; }
    protected CommandListener getCommandListener() { return commandListener; }
}
