package domains.services;

import java.util.List;


/**
 * Created by juanlabrador on 10/09/15.
 */
public class EntryChildren {

    private String guid;
    private String dateModified;
    private String name;
    private String birthDate;
    private int gender;
    private List<Entries> entries;
    private List<String> deletedEntryGuids;

    public EntryChildren() {
    }

    public EntryChildren(String guid, String dateModified, String name, String birthDate, int gender, List<Entries> entries, List<String> deletedEntryGuids) {
        this.guid = guid;
        this.dateModified = dateModified;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.entries = entries;
        this.deletedEntryGuids = deletedEntryGuids;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public List<Entries> getEntries() {
        return entries;
    }

    public void setEntries(List<Entries> entries) {
        this.entries = entries;
    }

    public List<String> getDeletedEntryGuids() {
        return deletedEntryGuids;
    }

    public void setDeletedEntryGuids(List<String> deletedEntryGuids) {
        this.deletedEntryGuids = deletedEntryGuids;
    }
}
