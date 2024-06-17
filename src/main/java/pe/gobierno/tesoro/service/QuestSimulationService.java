package pe.gobierno.tesoro.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pe.gobierno.tesoro.model.Adventurer;
import pe.gobierno.tesoro.model.Quest;
import pe.gobierno.tesoro.model.Ground;
import pe.gobierno.tesoro.model.GroundType;
import pe.gobierno.tesoro.model.CardinalPointsType;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class QuestSimulationService {

    private static final Logger LOGGER = Logger.getLogger(QuestSimulationService.class.getName());

    private static final char TURN_LEFT = 'G';

    private static final char TURN_RIGHT = 'D';

    private static final char FORWARD = 'A';

    private QuestSimulationService() {}

    public static void execute(Quest quest) {

        Optional<Adventurer> adventurerWithLongestMovementSequence = quest.getAdventurerList().stream().max(Comparator.comparingInt(a -> a.getMovementSequence().size()));

        if(adventurerWithLongestMovementSequence.isEmpty()) {
            return;
        }

        while (!adventurerWithLongestMovementSequence.get().getMovementSequence().isEmpty()) {
            for (var adventurer : quest.getAdventurerList()) {
                Set<Pair<Long, Long>> adventurersPositions = quest.getAdventurerList().parallelStream()
                        .map(adv -> Pair.of(adv.getPosX(), adv.getPosY())).collect(Collectors.toSet());

                if (adventurer.getMovementSequence().isEmpty()) {
                    continue;
                }

                CardinalPointsType orientation = adventurer.getOrientation();
                char movement = adventurer.getMovementSequence().getFirst();

                switch (movement) {
                    case TURN_LEFT  -> adventurer.setOrientation(orientation.turnLeft());
                    case TURN_RIGHT -> adventurer.setOrientation(orientation.turnRight());
                    case FORWARD -> {
                        var currentPosition = Pair.of(adventurer.getPosX(), adventurer.getPosY());
                        Pair<Long, Long> nextPosition = getNextPosition(Pair.of(adventurer.getPosX(),
                                        adventurer.getPosY()), adventurer.getOrientation());

                        if (shouldGoToNextGround(adventurersPositions, quest.getGroundMap(), nextPosition, adventurer)) {
                            if(quest.getGroundMap().get(currentPosition) != null) {
                                quest.getGroundMap().get(currentPosition).setAdventurerPresent(false);
                            }

                            adventurer.setPosX(nextPosition.getLeft());
                            adventurer.setPosY(nextPosition.getRight());
                        }
                    }
                    default -> {/* nothing */}
                }

                adventurer.getMovementSequence().removeFirst();
            }
        }
    }

    private static boolean shouldGoToNextGround(Set<Pair<Long, Long>> adventurersPositions, Map<Pair<Long, Long>, Ground> groundMap,
                                                Pair<Long, Long> nextPosition, Adventurer adventurer) {

        if(!adventurersPositions.add(nextPosition)) {
            return false;
        }

        Ground plainGround = new Ground(GroundType.PLAIN, false);
        Ground ground = groundMap.getOrDefault(nextPosition, plainGround);
        switch (ground.getType()) {
            case GroundType.MOUNTAIN -> {
                return false;
            }
            case GroundType.TREASURE -> {
                if (ground.isAdventurerPresent()) {
                    return false;
                }
                if (ground.getNumberOfTreasure() > 0) {
                    ground.setNumberOfTreasure(ground.getNumberOfTreasure() - 1);
                    ground.setAdventurerPresent(true);
                    adventurer.setTreasure(adventurer.getTreasure() + 1);
                }
                return true;
            }
            default -> {
                return true;
            }
        }
    }

    private static Pair<Long, Long> getNextPosition(Pair<Long, Long> currentPosition, CardinalPointsType orientation) {

        switch (orientation) {
            case CardinalPointsType.N -> {
                return Pair.of(currentPosition.getLeft(), currentPosition.getRight() - 1);
            }
            case CardinalPointsType.E -> {
                return Pair.of(currentPosition.getLeft() + 1, currentPosition.getRight());
            }
            case CardinalPointsType.S -> {
                return Pair.of(currentPosition.getLeft(), currentPosition.getRight() + 1);
            }
            case CardinalPointsType.O -> {
                return Pair.of(currentPosition.getLeft() - 1, currentPosition.getRight());
            }
            default -> {
                return currentPosition;
            }
        }
    }
}
