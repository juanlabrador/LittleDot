package domains.services;

/**
 * Created by juanlabrador on 10/09/15.
 */
public class EntryData {

    private int group;
    private int value;
    private float detailValue;
    private String text = "";
    private boolean skipped = false;

    public EntryData() {
    }

    public EntryData(int group, int value, float detailValue, String text, boolean skipped) {
        this.group = group;
        this.value = value;
        this.detailValue = detailValue;
        if (text != null && !text.isEmpty())
            this.text = text;
        this.skipped = skipped;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public float getDetailValue() {
        return detailValue;
    }

    public void setDetailValue(float detailValue) {
        this.detailValue = detailValue;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getSkipped() {
        return skipped;
    }

    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

}
