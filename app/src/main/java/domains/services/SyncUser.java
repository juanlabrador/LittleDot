package domains.services;

/**
 * Created by juanlabrador on 28/09/15.
 */
public class SyncUser {

    private String userId;

    public SyncUser() {
    }

    public SyncUser(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
