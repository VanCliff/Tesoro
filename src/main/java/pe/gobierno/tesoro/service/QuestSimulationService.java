package pe.gobierno.tesoro.service;

import org.apache.commons.lang3.tuple.Pair;
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

public class QuestSimulationService {

    private static final Logger LOGGER = Logger.getLogger(QuestSimulationService.class.getName());

    private static final char TURN_LEFT = 'G';

    private static final char TURN_RIGHT = 'D';

    private static final char FORWARD = 'A';

    private QuestSimulationService() {}

    /**
     * This method will execute the quest's logic
     * It will process the movements of each adventurer until ALL movements are consumed.
     *
     * @param quest the quest
     */
    @SuppressWarnings("java:S3776")
    public static void execute(Quest quest) {
        LOGGER.info("Quest begin !");

        Optional<Adventurer> adventurerWithLongestMovementSequence = quest.getAdventurerList().stream()
                .max(Comparator.comparingInt(a -> a.getMovementSequence().size()));

        if (adventurerWithLongestMovementSequence.isEmpty()) {
            return;
        }

        // While an adventurer still have movements
        while (!adventurerWithLongestMovementSequence.get().getMovementSequence().isEmpty()) {
            for (var adventurer : quest.getAdventurerList()) {
                // Get every adventurer current position for collision when they will move
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
                        var nextPosition = getNextPosition(currentPosition, adventurer.getOrientation());

                        if (shouldGoToNextGround(adventurersPositions, quest.getGroundMap(), nextPosition, adventurer)) {
                            // If the adventurer move and was on a treasure previously, the treasure ground don't have
                            // an adventurer anymore
                            if (quest.getGroundMap().get(currentPosition) != null) {
                                quest.getGroundMap().get(currentPosition).setAdventurerPresent(false);
                            }

                            adventurer.setPosX(nextPosition.getLeft());
                            adventurer.setPosY(nextPosition.getRight());
                        }
                    }
                    default -> {/* nothing */}
                }
                LOGGER.info(adventurer.getName() + " moved !");
                adventurer.getMovementSequence().removeFirst();
            }
        }
        LOGGER.info("Quest ended ! Time to count treasures");
    }

    /**
     * This method determines if an adventurer should move to the next ground position.
     * <p>
     * It checks whether the next position is already occupied by another adventurer or if the ground type at the next position
     * prevents movement (i.e., a mountain). It also handles treasure collection by adventurers if the next position contains a treasure.
     *
     * @param adventurersPositions the set of current positions of all adventurers
     * @param groundMap the map of ground positions
     * @param nextPosition the next position for the adventurer
     * @param adventurer the adventurer
     * @return True if the adventurer can move to the next position, false otherwise
     */
    private static boolean shouldGoToNextGround(Set<Pair<Long, Long>> adventurersPositions, Map<Pair<Long, Long>, Ground> groundMap,
                                                Pair<Long, Long> nextPosition, Adventurer adventurer) {

        if (!adventurersPositions.add(nextPosition)) {
            return false;
        }

        Ground plainGround = new Ground(GroundType.PLAIN, false);
        Ground ground = groundMap.getOrDefault(nextPosition, plainGround);
        switch (ground.getType()) {
            case MOUNTAIN -> {
                return false;
            }
            case TREASURE -> {
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

    /**
     * Calculates the next position based on the current position and the orientation.
     *
     * @param currentPosition currentPosition
     * @param orientation orientation
     * @return the next position
     */
    private static Pair<Long, Long> getNextPosition(Pair<Long, Long> currentPosition, CardinalPointsType orientation) {

        switch (orientation) {
            case N -> {
                return Pair.of(currentPosition.getLeft(), currentPosition.getRight() - 1);
            }
            case E -> {
                return Pair.of(currentPosition.getLeft() + 1, currentPosition.getRight());
            }
            case S -> {
                return Pair.of(currentPosition.getLeft(), currentPosition.getRight() + 1);
            }
            case O -> {
                return Pair.of(currentPosition.getLeft() - 1, currentPosition.getRight());
            }
            default -> {
                return currentPosition;
            }
        }
    }
}
