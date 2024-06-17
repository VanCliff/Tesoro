package pe.gobierno.tesoro.tu.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pe.gobierno.tesoro.tu.model.Adventurer;
import pe.gobierno.tesoro.tu.model.Ground;
import pe.gobierno.tesoro.tu.model.GroundType;
import pe.gobierno.tesoro.tu.model.Quest;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SimulationResultProcessorService {

    private static final String MAP_SEPARATOR = "C - ";
    private static final String MOUNTAIN_SEPARATOR = "M - ";
    private static final String TREASURE_SEPARATOR = "T - ";
    private static final String ADVENTURER_SEPARATOR = "A - ";
    private static final String SEPARATOR = " - ";

    private static final Logger LOGGER = Logger.getLogger(SimulationResultProcessorService.class.getName());

    private SimulationResultProcessorService() {}

    /**
     * Writes the results of the quest to a result file
     *
     * <p>This method create or overwrite a result file that contains the state of the map after the quest has been finished
     * It includes the positions in this order of :
     * - mountains
     * - remaining treasures (still having treasure left)
     * - final state of adventurers
     *
     * @param quest the quest
     * @throws IOException IOException
     */
    @SuppressWarnings("squid:S1612")
    public static void writeFile(Quest quest) throws IOException {
        LOGGER.info("Creating result file");

        StringBuilder mountainLines = new StringBuilder();
        StringBuilder treasureLines = new StringBuilder();
        StringBuilder adventurerLines = new StringBuilder();
        String mapLine = MAP_SEPARATOR + quest.getQuestWidth() + SEPARATOR + quest.getQuestHeight() + System.lineSeparator();

        for (var entry : quest.getGroundMap().entrySet()) {
            Pair<Long, Long> key = entry.getKey();
            Ground ground = entry.getValue();

            if (ground.getType().equals(GroundType.TREASURE) && ground.getNumberOfTreasure() == 0) {
                quest.getGroundMap().remove(key);
            }
        }

        List<Pair<Long, Long>> sortedGroundPositions = new ArrayList<>(quest.getGroundMap().keySet());

        sortedGroundPositions.sort(Comparator
                .comparing((Pair<Long, Long> pair) -> pair.getLeft())
                .thenComparing(Pair::getRight));

        for (var position : sortedGroundPositions) {
            var ground = quest.getGroundMap().get(position);

            switch (ground.getType()) {
                case MOUNTAIN -> {
                    mountainLines.append(MOUNTAIN_SEPARATOR).append(position.getLeft())
                            .append(SEPARATOR).append(position.getRight());

                    mountainLines.append(System.lineSeparator());
                }
                case TREASURE -> {
                    treasureLines.append(TREASURE_SEPARATOR).append(position.getLeft())
                            .append(SEPARATOR).append(position.getRight()).append(SEPARATOR).append(ground.getNumberOfTreasure());

                    treasureLines.append(System.lineSeparator());
                }
                default -> {/* nothing */}
            }
        }

        var sortedAdventurerList = quest.getAdventurerList().stream()
                .sorted(Comparator.comparing(Adventurer::getPosX).thenComparing(Adventurer::getPosY)).toList();

        sortedAdventurerList.forEach(adventurer ->
                adventurerLines.append(ADVENTURER_SEPARATOR).append(adventurer.getName())
                        .append(SEPARATOR).append(adventurer.getPosX())
                        .append(SEPARATOR).append(adventurer.getPosY())
                        .append(SEPARATOR).append(adventurer.getOrientation())
                        .append(SEPARATOR).append(adventurer.getTreasure()).append(System.lineSeparator())
        );

        String fileName = "src/main/resources/result.txt";

        try (RandomAccessFile writer = new RandomAccessFile(fileName, "rw");
             FileChannel channel = writer.getChannel()) {

            List<String> listResultString = List.of(mapLine, mountainLines.toString(), treasureLines.toString(),
                    adventurerLines.toString());

            for (String element : listResultString) {
                ByteBuffer buffer = ByteBuffer.wrap(element.getBytes(StandardCharsets.UTF_8));

                if (channel.write(buffer) == -1) {
                    LOGGER.log(Level.SEVERE, () -> "Error occurred while writing to the file : " + element);
                    return;
                }
            }
        }
        LOGGER.info(() -> fileName + " created !");
    }
}
