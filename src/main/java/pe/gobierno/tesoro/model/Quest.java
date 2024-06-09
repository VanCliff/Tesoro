package pe.gobierno.tesoro.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final ConcurrentHashMap<Pair<Long, Long>, Ground> groundMap;
    private long questWidth, questHeight;

    public Quest(List<Adventurer> adventurerList, ConcurrentHashMap<Pair<Long, Long>, Ground> groundMap) {
        this.adventurerList = adventurerList;
        this.groundMap = groundMap;
    }

    public List<Adventurer> getAdventurerList() {
        return adventurerList;
    }

    public Map<Pair<Long, Long>, Ground> getGroundMap() {
        return groundMap;
    }
}
