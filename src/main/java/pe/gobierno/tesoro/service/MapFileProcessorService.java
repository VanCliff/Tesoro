package pe.gobierno.tesoro.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pe.gobierno.tesoro.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MapFileProcessorService {

    private static final Logger LOGGER = Logger.getLogger(MapFileProcessorService.class.getName());

    /**
     * List of possible orientation
     */
    private static final List<String> listOrientation = Arrays.asList("N", "S", "E", "O");

    /**
     * List of possible movements
     */
    private static final List<Character> listMovement = Arrays.asList('A', 'G', 'D');

    private MapFileProcessorService() {}

    /***
     * This method will read the input arguments, parse the map file and create the quest.
     *
     * @param args Input parameters
     *
     * @return A {@link Quest} initialized or not
     */
    public static Optional<Quest> readFile(String[] args) throws IOException {
        Optional<String> filePath = parseArgs(args);

        if (filePath.isEmpty() || filePath.get().isBlank()) {
            return Optional.empty();
        }

        return handleFile(filePath.get());
    }

    /***
     * This method will handle (parse & validate) the map file
     *
     * @param filePath      the file path
     * @throws IOException  IOException
     */
    private static Optional<Quest> handleFile(String filePath) throws IOException {
        List<String> listMountainLines  = new ArrayList<>();
        List<String> listTreasureLines  = new ArrayList<>();
        List<String> listAdventurerLines = new ArrayList<>();

        String mapLine = parseFile(filePath, listTreasureLines, listMountainLines, listAdventurerLines);
        return validateFile(mapLine, listTreasureLines, listMountainLines, listAdventurerLines);

    }

    /***
     * This method will parse the map file, it will take every useful data
     *
     * @param filePath              the file path
     * @param listTreasureLines     every treasure lines
     * @param listMountainLines     every mountain lines
     * @param listAdventurerLines   every adventurer line
     * @throws IOException          IOException
     *
     * @return the line corresponding to the map
     */
    private static String parseFile(String filePath, List<String> listTreasureLines, List<String> listMountainLines, List<String> listAdventurerLines) throws IOException {
        String mapLine = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) {
                    continue;
                }

                line = line.replace(" ", "");

                switch (line.substring(0, 1)) {
                    case "C" -> {
                        if (mapLine.isEmpty()) {
                            mapLine = line;
                        } else {
                            LOGGER.warning("Map info line already parsed ! It will be ignored");
                        }
                    }
                    case "T" -> listTreasureLines.add(line);
                    case "M" -> listMountainLines.add(line);
                    case "A" -> listAdventurerLines.add(line);
                    default -> {/* line is ignored (for example # line) */}
                }
            }
        }

        return mapLine;
    }

    /***
     * This method will parse the map file, it will take every useful data
     *
     * @param mapLine             the card line
     * @param listTreasureLines   every treasure lines
     * @param listMountainLines   every mountain lines
     * @param listAdventurerLines every adventurer line
     */
    private static Optional<Quest> validateFile(String mapLine, List<String> listTreasureLines, List<String> listMountainLines,
                                                List<String> listAdventurerLines) {

        Quest quest = new Quest();

        validateMapInfo(mapLine, quest);
        validateAdventurerInfo(listAdventurerLines, quest);
        validateMountainInfo(listMountainLines, quest);
        validateTreasureInfo(listTreasureLines, quest);

        if (quest.isValid()) {
            return Optional.of(quest);
        }

        return Optional.empty();
    }

    /***
     * Validate the map line
     *
     * @param mapLine   the map line
     * @param quest     the Quest
     */
    private static void validateMapInfo(String mapLine, Quest quest) {
        String[] mapInfo = mapLine.replace(" ", "").split("-");
        long questWidth = -1;
        long questHeight = -1;

        if(mapInfo.length < 2) {
            LOGGER.log(Level.SEVERE, "Map dimension are not available");
            quest.setValid(false);
        }

        try {
            questWidth  = Long.parseLong(mapInfo[1]);
            questHeight = Long.parseLong(mapInfo[2]);
        } catch (NumberFormatException numberFormatException) {
            LOGGER.log(Level.SEVERE, "Map dimension are not numeric");
            quest.setValid(false);
        }

        if(questWidth > 0 && questWidth != questHeight) {
            quest.setQuestWidth(questWidth);
        }

        if(questHeight > 0 && questWidth != questHeight) {
            quest.setQuestHeight(questHeight);
        }

        if(quest.getQuestHeight() == 0L || quest.getQuestWidth() == 0L) {
            LOGGER.log(Level.SEVERE, "Map dimension are invalid (either negative/zero or form a square)");
            quest.setValid(false);
        }
    }

    /***
     * Validate the adventurer lines
     *
     * @param listAdventurerLines the list of adventurer lines
     * @param quest the Quest
     */
    private static void validateAdventurerInfo(List<String> listAdventurerLines, Quest quest) {
        int index = 0;

        var adventurerPositions = new HashSet<Pair<Long, Long>>();

        for (var adventurerLine : listAdventurerLines) {
            String[] adventurerInfo = adventurerLine.split("-");
            String movementSequence;
            long adventurerAbscissa = -1;
            long adventurerOrdinate = -1;
            String name;

            if (adventurerInfo.length < 5) {
                LOGGER.log(Level.SEVERE, "Adventurer information are not available");
                quest.setValid(false);
            }

            name = adventurerInfo[1];

            Adventurer adventurer = new Adventurer(index, name);

            try {
                adventurerAbscissa = Long.parseLong(adventurerInfo[2]);
                adventurerOrdinate = Long.parseLong(adventurerInfo[3]);
            } catch (NumberFormatException numberFormatException) {
                LOGGER.log(Level.SEVERE, "Adventurer's position are not numeric");
                quest.setValid(false);
            }

            if ((adventurerAbscissa >= 0 && adventurerAbscissa < quest.getQuestWidth())
                    && adventurerOrdinate >= 0 && adventurerOrdinate < quest.getQuestHeight()) {

                if (checkIsAdventurerPresent(adventurerAbscissa, adventurerOrdinate, adventurerPositions)) {
                    LOGGER.log(Level.SEVERE, "Adventurers share same position");
                    quest.setValid(false);
                } else {
                    adventurer.setPosX(adventurerAbscissa);
                    adventurer.setPosY(adventurerOrdinate);
                }
            } else {
                LOGGER.log(Level.SEVERE, "Adventurer's position are out of map");
                quest.setValid(false);
            }

            if (listOrientation.contains(adventurerInfo[4])) {
                adventurer.setOrientation(CardinalPointsType.valueOf(String.valueOf(adventurerInfo[4].charAt(0))));
            } else {
                LOGGER.log(Level.SEVERE, "Invalid orientation");
                quest.setValid(false);
            }

            movementSequence = adventurerInfo[5];
            Supplier<Stream<Character>> movementSequenceSupplier = () -> movementSequence.chars().mapToObj(c -> (char) c);
            Set<Character> movementSequenceCharacters = movementSequenceSupplier.get().collect(Collectors.toSet());

            if (movementSequenceCharacters.size() > 3 || movementSequenceCharacters.retainAll(listMovement)) {
                LOGGER.log(Level.SEVERE, "Invalid movement");
                quest.setValid(false);
            } else {
                adventurer.setMovementSequence(movementSequenceSupplier.get().collect(Collectors.toCollection(LinkedList::new)));
            }

            adventurerPositions.add(Pair.of(adventurer.getPosX(), adventurer.getPosY()));
            quest.getAdventurerList().add(adventurer);
            index++;
        }
    }


    /***
     * Validate the treasure lines
     *
     * @param listTreasureLines the list of treasure lines
     * @param quest the Quest
     */
    private static void validateTreasureInfo(List<String> listTreasureLines, Quest quest) {
        var groundMap = quest.getGroundMap();

        for (var treasureLine : listTreasureLines) {

            String[] treasureInfo = treasureLine.split("-");
            long treasureAbscissa = -1;
            long treasureOrdinate = -1;
            long treasureNumber = -1;

            if (treasureInfo.length < 3) {
                LOGGER.log(Level.SEVERE, "Treasure information are not available");
                quest.setValid(false);
            }

            try {
                treasureAbscissa = Long.parseLong(treasureInfo[1]);
                treasureOrdinate = Long.parseLong(treasureInfo[2]);
                treasureNumber   = Long.parseLong(treasureInfo[3]);
            } catch (NumberFormatException numberFormatException) {
                LOGGER.log(Level.SEVERE, "Treasure info are not numeric");
                quest.setValid(false);
            }

            if ((treasureAbscissa >= 0 && treasureAbscissa < quest.getQuestWidth())
                && treasureOrdinate >= 0 && treasureOrdinate < quest.getQuestHeight()
                && treasureNumber >= 0 ) {

                Ground ground = new Ground(GroundType.TREASURE, false, treasureNumber);
                groundMap.putIfAbsent(Pair.of(treasureAbscissa, treasureOrdinate), ground);
            }

            if (groundMap.get(Pair.of(treasureAbscissa, treasureOrdinate)) == null) {
                LOGGER.log(Level.SEVERE, "Treasure info are invalid (either negative/zero)");
                quest.setValid(false);
            }
        }

    }


    /***
     * Validate the mountain lines
     *
     * @param listMountainLines the list of mountain lines
     * @param quest the Quest
     */
    private static void validateMountainInfo(List<String> listMountainLines, Quest quest) {

        var groundMap = quest.getGroundMap();

        var adventurerPositions = quest.getAdventurerList().stream()
                .map(adventurer -> Pair.of(adventurer.getPosX(), adventurer.getPosY()))
                .collect(Collectors.toSet());

        for (var mountainLine : listMountainLines) {

            String[] treasureInfo = mountainLine.split("-");
            long mountainAbscissa = -1;
            long mountainOrdinate = -1;

            if (treasureInfo.length < 2) {
                LOGGER.log(Level.SEVERE, "Mountain information are not available");
                quest.setValid(false);
            }

            try {
                mountainAbscissa = Long.parseLong(treasureInfo[1]);
                mountainOrdinate = Long.parseLong(treasureInfo[2]);
            } catch (NumberFormatException numberFormatException) {
                LOGGER.log(Level.SEVERE, "Mountain info are not numeric");
                quest.setValid(false);
            }

            if ((mountainAbscissa >= 0 && mountainAbscissa < quest.getQuestWidth())
                    && mountainOrdinate >= 0 && mountainOrdinate < quest.getQuestHeight()) {

                if(checkIsAdventurerPresent(mountainAbscissa, mountainOrdinate, adventurerPositions)) {
                    LOGGER.log(Level.SEVERE, "Adventurer and mountain share same position");
                    quest.setValid(false);
                }

                Ground ground = new Ground(GroundType.MOUNTAIN,false);
                groundMap.putIfAbsent(Pair.of(mountainAbscissa, mountainOrdinate), ground);
            }

            if (groundMap.get(Pair.of(mountainAbscissa, mountainOrdinate)) == null) {
                LOGGER.log(Level.SEVERE, "Treasure info are invalid (either negative/zero)");
                quest.setValid(false);
            }
        }
    }

    /**
     * Check if an adventurer is present at specific position
     *
     * @param posX posX
     * @param posY posY
     * @param adventurerPositions the collection of every adventurer's position
     *
     * @return true if present false otherwise
     */
    private static boolean checkIsAdventurerPresent(long posX, long posY, Set<Pair<Long, Long>> adventurerPositions) {
        //noinspection SuspiciousNameCombination
        return adventurerPositions.contains(Pair.of(posX, posY));

    }

    /***
     * This method will check how many args are provided.
     *
     * @param args The provided args
     * @return The map file path or an empty optional
     */
    private static Optional<String> parseArgs(String[] args) {
        if (args.length == 0) {
            LOGGER.log(Level.INFO, "Please provide file paths as arguments.");
            return Optional.empty();
        }

        Arrays.stream(args).skip(1)
                .forEach(arg -> LOGGER.warning("Unrecognized argument : " + arg));

        return Optional.of(args[0]);
    }
}
