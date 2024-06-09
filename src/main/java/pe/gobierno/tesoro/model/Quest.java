package pe.gobierno.tesoro.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/***
 * This class implements all business logic.
 * <p>
 * It has :
 * - the list of adventurers
 * - the location of every mountain, treasures & adventurers
 * - the size of the quest (i.e. the width/height)
 */
public class Quest {
    private final List<Adventurer> adventurerList;
    private final ConcurrentMap<Pair<Long, Long>, Ground> groundMap;
    private final long questWidth;
    private final long questHeight;

    public Quest(List<Adventurer> adventurerList, ConcurrentMap<Pair<Long, Long>, Ground> groundMap,
                 long questWidth, long questHeight) {

        this.adventurerList = adventurerList;
        this.groundMap = groundMap;
        this.questWidth = questWidth;
        this.questHeight= questHeight;
    }

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
}
