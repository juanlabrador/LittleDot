package domains.services;

import java.util.List;

/**
 * Created by juanlabrador on 10/09/15.
 */
public class UnlockedCategories {

    private List<Integer> unlockedCategories;

    public UnlockedCategories() {
    }

    public UnlockedCategories(List<Integer> unlockedCategories) {
        this.unlockedCategories = unlockedCategories;
    }

    public List<Integer> getUnlockedCategories() {
        return unlockedCategories;
    }

    public void setUnlockedCategories(List<Integer> unlockedCategories) {
        this.unlockedCategories = unlockedCategories;
    }
}
