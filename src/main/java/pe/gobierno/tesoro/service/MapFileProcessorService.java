package pe.gobierno.tesoro.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import pe.gobierno.tesoro.model.Ground;
import pe.gobierno.tesoro.model.GroundType;
import pe.gobierno.tesoro.model.Quest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class MapFileProcessorService {

    private MapFileProcessorService() {}

    /***
     * This method will read the input arguments, parse the map file and create the quest.
     *
     * @param args Input parameters
     * @return A {@link Quest} initialized or not
     */
    public static Optional<Quest> readFile(String[] args) throws IOException {
        Optional<String> filePath = parseArgs(args);

        if (filePath.isEmpty() || filePath.get().isBlank()) {
            return Optional.empty();
        }

        Quest quest = new Quest();

        handleFile(filePath.get());

        return Optional.of(quest);
    }

    /***
     * This method will handle (parse & validate) the map file
     *
     * @param filePath the file path
     * @throws IOException IOException
     */
    private static void handleFile(String filePath) throws IOException {

        List<String> listMountainLines  = new ArrayList<>();
        List<String> listTreasureLines  = new ArrayList<>();
        List<String> listAdventurerLines = new ArrayList<>();
        String mapLine = "";

        parseFile(filePath, mapLine, listTreasureLines, listMountainLines, listAdventurerLines);
        validateFile( mapLine, listTreasureLines, listMountainLines, listAdventurerLines);

    }

    /***
     * This method will parse the map file, it will take every useful data
     *
     * @param filePath the file path
     * @param mapLine the card line
     * @param listTreasureLines every treasure lines
     * @param listMountainLines every mountain lines
     * @param listAdventurerLines every adventurer line
     * @throws IOException IOException
     */
    private static void parseFile(String filePath, String mapLine, List<String> listTreasureLines, List<String> listMountainLines, List<String> listAdventurerLines) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {

                if(line.isBlank()) {
                    continue;
                }

                switch (line.substring(0, 1)) {
                    case "C" -> {
                        if(mapLine.isEmpty()) {
                            mapLine = line;
                        } else {
                            System.out.println("WARN : Map info line already parsed ! It will be ignored");
                        }
                    }
                    case "T" -> listTreasureLines.add(line);
                    case "M" -> listMountainLines.add(line);
                    case "A" -> listAdventurerLines.add(line);
                    default -> {/* line is ignored (for example # line) */}
                }
            }
        }
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

        // TOOD : Refactor tout ça, code bien trop en commun
        Optional<Quest> quest = validateMapInfo(mapLine, new Quest());

        if(quest.isPresent()) {
            quest = validateTreasureInfo(listTreasureLines, quest.get());
        }

        if(quest.isPresent()) {

        }

        return quest;

    }

    /***
     * Validate the map line
     *
     * @param mapLine the map line
     * @param quest the Quest
     *
     * @return A Quest with map info or empty
     */
    private static Optional<Quest> validateMapInfo(String mapLine, Quest quest) {
        String[] mapInfo = mapLine.split("-");
        long questWidth;
        long questHeight;

        if(mapInfo.length < 2) {
            System.out.println("ERROR : Map dimension are not available");
            return Optional.empty();
        }

        try {
            questWidth  = Long.parseLong(mapInfo[1]);
            questHeight = Long.parseLong(mapInfo[2]);
        } catch (NumberFormatException numberFormatException) {
            System.out.println("ERROR : Map dimension are not numeric");
            return Optional.empty();
        }

        if(questWidth > 0 && questWidth != questHeight) {
            quest.setQuestWidth(questWidth);
        }

        if(questHeight > 0 && questWidth != questHeight) {
            quest.setQuestHeight(questHeight);
        }

        if(quest.getQuestHeight() == 0L || quest.getQuestWidth() == 0L) {
            System.out.println("ERROR : Map dimension are invalid (either negative/zero or form a square)");
            return Optional.empty();
        }
        return Optional.of(quest);
    }

    /***
     * Validate the treasure lines
     *
     * @param listTreasureLines the list of treasure lines
     * @param quest the Quest
     *
     * @return A Quest with treasure info or empty
     */
    private static Optional<Quest> validateTreasureInfo(List<String> listTreasureLines, Quest quest) {
        ConcurrentMap<Pair<Long, Long>, Ground> groundMap = new ConcurrentHashMap<>();

        for (var treasureLine : listTreasureLines) {

            String[] treasureInfo = treasureLine.split("-");
            long treasureAbscissa;
            long treasureOrdinate;
            long treasureNumber;

            if (treasureInfo.length < 3) {
                System.out.println("ERROR : Treasure information are not available");
                return Optional.empty();
            }

            try {
                treasureAbscissa = Long.parseLong(treasureInfo[1]);
                treasureOrdinate = Long.parseLong(treasureInfo[2]);
                treasureNumber   = Long.parseLong(treasureInfo[3]);
            } catch (NumberFormatException numberFormatException) {
                System.out.println("ERROR : Treasure info are not numeric");
                return Optional.empty();
            }

            if ((treasureAbscissa >= 0 && treasureAbscissa < quest.getQuestWidth())
                && treasureOrdinate >= 0 && treasureOrdinate < quest.getQuestHeight()
                && treasureNumber >= 0 ) {

                // TODO : Doit être fait après avoir obtenus les aventuriers
                Ground ground = new Ground(GroundType.TREASURE, false);
                groundMap.putIfAbsent(Pair.of(treasureAbscissa, treasureOrdinate), ground);
            }

            if (groundMap.get(Pair.of(treasureAbscissa, treasureOrdinate)) == null) {
                System.out.println("ERROR : Treasure info are invalid (either negative/zero)");
                return Optional.empty();
            }
        }

        quest.setGroundMap(groundMap);
        return Optional.of(quest);
    }


    /***
     * This method will check how many args are provided.
     *
     * @param args The provided args
     * @return The map file path or an empty optional
     */
    private static Optional<String> parseArgs(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide file paths as arguments.");
            return Optional.empty();
        }

        Arrays.stream(args).skip(1)
                .forEach(arg -> System.out.println("Unrecognized argument : " + arg));

        return Optional.of(args[0]);
    }
}
