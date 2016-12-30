package domains.services;

import java.util.List;

/**
 * Created by juanlabrador on 10/09/15.
 */
public class Entries {

    private String guid;
    private int categoryId;
    private String dateModified;
    private List<EntryData> entryData;
    private List<String> pictures;
    private String note = "";

    public Entries() {
    }

    public Entries(String guid, int categoryId, String date, List<EntryData> entryData, List<String> pictures, String note) {
        this.guid = guid;
        this.categoryId = categoryId;
        this.dateModified = date;
        this.entryData = entryData;
        this.pictures = pictures;
        if (note != null && !note.isEmpty())
            this.note = note;

    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getDate() {
        return dateModified;
    }

    public void setDate(String dateModified) {
        this.dateModified = dateModified;
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

    public List<EntryData> getEntryData() {
        return entryData;
    }

    public void setEntryData(List<EntryData> entryData) {
        this.entryData = entryData;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
