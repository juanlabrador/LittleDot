package domains;

import java.util.List;

/**
 * Created by juanlabrador on 04/09/15.
 */
public class GroupDot {

    private DotCategory dotCategory;
    private List<DotCategory> group;
    private String button;
    private String date;

    public GroupDot() {
    }

    public GroupDot(String button, String date) {
        this(null, null, button, date);
    }

    public GroupDot(DotCategory dotCategory, List<DotCategory> group) {
        this(dotCategory, group, null, null);
    }

    public GroupDot(DotCategory dotCategory) {
        this(dotCategory, null, null, null);
    }

    public GroupDot(DotCategory dotCategory, List<DotCategory> group, String button, String date) {
        this.dotCategory = dotCategory;
        this.group = group;
        this.button = button;
        this.date = date;
    }

    public DotCategory getDotCategory() {
        return dotCategory;
    }

    public void setDotCategory(DotCategory dotCategory) {
        this.dotCategory = dotCategory;
    }

    public List<DotCategory> getGroup() {
        return group;
    }

    public void setGroup(List<DotCategory> group) {
        this.group = group;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
