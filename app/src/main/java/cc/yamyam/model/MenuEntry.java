package cc.yamyam.model;

/**
 * Created by siyuan on 17.08.15.
 */
public class MenuEntry {
    private String id;
    private String label;
    private int icon;

    public MenuEntry() {

    }

    public MenuEntry(String id, String label, int icon) {
        this.id = id;
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
