package domains.services;

import java.util.List;

/**
 * Created by juanlabrador on 08/10/15.
 */
public class Children {

    private String guid;
    private List<Entries> entries;

    public Children() {
    }

    public Children(String guid, List<Entries> entries) {
        this.guid = guid;
        this.entries = entries;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public List<Entries> getEntries() {
        return entries;
    }

    public void setEntries(List<Entries> entries) {
        this.entries = entries;
    }
}
