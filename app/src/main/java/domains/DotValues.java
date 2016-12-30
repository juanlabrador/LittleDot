package domains;

/**
 * Created by juanlabrador on 10/09/15.
 */
public class DotValues {

    private int group;
    private int value;
    private float detailValue;
    private String text;
    private int skipped;

    public DotValues() {
    }

    public DotValues(int group, int value, float detailValue, String text, int skipped) {
        this.group = group;
        this.value = value;
        this.detailValue = detailValue;
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

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    @Override
    public String toString() {
        return "{ group:" + group + ", value:" + value + ", detailValue:" + detailValue +
                ", text:" + text + "skipped:" + skipped + "}";
    }
}
