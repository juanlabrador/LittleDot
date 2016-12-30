package domains.services;

import java.util.List;

import domains.Child;

/**
 * Created by juanlabrador on 10/09/15.
 */
public class SyncChildren {

    private String username;
    private List<Child> children;

    public SyncChildren() {
    }

    public SyncChildren(String username, List<Child> children) {
        this.username = username;
        this.children = children;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }
}
