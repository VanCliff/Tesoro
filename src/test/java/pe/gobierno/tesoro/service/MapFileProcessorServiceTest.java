package pe.gobierno.tesoro.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pe.gobierno.tesoro.model.Quest;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

class MapFileProcessorServiceTest {

    @ParameterizedTest
    @MethodSource("provideArgsForParseArgsTest")
    void parseArgsTest(String[] args) throws IOException {

        if (args.length == 0) {
            Assertions.assertEquals(Optional.empty(), MapFileProcessorService.readFile(args));
        } else {
            String filePath = args[0];

            if (filePath.isBlank()) {
                Assertions.assertEquals(Optional.empty(), MapFileProcessorService.readFile(args));
            } else {
                Optional<Quest> optionalQuest = MapFileProcessorService.readFile(args);
                Assertions.assertTrue(optionalQuest.isPresent());
            }
        }
    }

    private static Stream<Arguments> provideArgsForParseArgsTest() {
        String filePath = "src/test/resources/mapOK.txt";

        Arguments noArgs = Arguments.of((Object) new String[]{});
        Arguments filePathOK = Arguments.of((Object) new String[]{filePath});
        Arguments emptyArgs = Arguments.of((Object) new String[]{""});
        Arguments blankArgs = Arguments.of((Object) new String[]{" "});
        Arguments tooMuchArgsFilePathOK = Arguments.of((Object) new String[]{filePath, "foo", "bar"});
        Arguments tooMuchArgsFilePathKO = Arguments.of((Object) new String[]{" ", "foo", "bar"});

        return Stream.of(noArgs, filePathOK, emptyArgs, blankArgs, tooMuchArgsFilePathOK, tooMuchArgsFilePathKO);
    }


    @ParameterizedTest
    @MethodSource("provideArgsForHandleFileTest")
    void handleFileTest(String filePath, boolean isValid) throws IOException {
        Optional<Quest> quest = MapFileProcessorService.readFile(new String[] {filePath});

        Assertions.assertEquals(isValid, quest.isPresent());
    }

    private static Stream<Arguments> provideArgsForHandleFileTest() {
        String mapOK  = "src/test/resources/mapOK.txt";
        String mapKO  = "src/test/resources/emptyMap.txt";
        String mapOK2 = "src/test/resources/onlyMapLine.txt";
        String mapKO2 = "src/test/resources/mapWithCommentsOnly.txt";
        String mapKO3 = "src/test/resources/squareMap.txt";
        String mapKO4 = "src/test/resources/mapLineNotNumeric.txt";
        String mapKO5 = "src/test/resources/invalidMapCollision.txt";
        String mapKO6 = "src/test/resources/onlyAdventurerLine.txt";
        String mapKO7 = "src/test/resources/onlyMountainLine.txt";
        String mapKO8 = "src/test/resources/onlyTreasureLine.txt";
        String mapK09 = "src/test/resources/adventurerOrientationKO.txt";
        String mapK10 = "src/test/resources/adventurerMovementSequenceKO.txt";
        String mapK11 = "src/test/resources/invalidMapPosition1.txt";
        String mapK12 = "src/test/resources/invalidMapPosition2.txt";
        String mapK13 = "src/test/resources/invalidMapPosition3.txt";



        return Stream.of(Arguments.of(mapOK, true),
                Arguments.of(mapKO, false),
                Arguments.of(mapOK2, true),
                Arguments.of(mapKO2, false),
                Arguments.of(mapKO3, false),
                Arguments.of(mapKO4, false),
                Arguments.of(mapKO5, false),
                Arguments.of(mapKO6, false),
                Arguments.of(mapKO7, false),
                Arguments.of(mapKO8, false),
                Arguments.of(mapK09, false),
                Arguments.of(mapK10, false),
                Arguments.of(mapK11, false),
                Arguments.of(mapK12, false),
                Arguments.of(mapK13, false)
        );

    }

}