package pe.gobierno.tesoro.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * This class implements all business logic.
 * <p>
 * It has :
 * - the list of adventurers
 * - the location of every mountain, treasures & adventurers
 * - the size of the quest (i.e. the width/height)
 * - if the quest's data is valid
 */
public class Quest {
    private final List<Adventurer> adventurerList = new ArrayList<>();
    private final Map<Pair<Long, Long>, Ground> groundMap = new HashMap<>();
    private long questWidth;
    private long questHeight;
    private boolean isValid = true;

    public List<Adventurer> getAdventurerList() {
        return adventurerList;
    }

    public Map<Pair<Long, Long>, Ground> getGroundMap() {
        return groundMap;
    }

    public long getQuestWidth() {
        return questWidth;
    }

    public long getQuestHeight() {
        return questHeight;
    }

    public void setQuestWidth(long questWidth) {
        this.questWidth = questWidth;
    }

    public void setQuestHeight(long questHeight) {
        this.questHeight = questHeight;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
