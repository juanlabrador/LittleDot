package domains.services;

import java.util.List;

import domains.Child;

/**
 * Created by juanlabrador on 10/09/15.
 */
public class SyncEntriesByChildren {

    private String username;
    private List<Children> children;

    public SyncEntriesByChildren() {
    }

    public SyncEntriesByChildren(String username, List<Children> children) {
        this.username = username;
        this.children = children;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Children> getChildren() {
        return children;
    }

    public void setChildren(List<Children> children) {
        this.children = children;
    }

}
