package pe.gobierno.tesoro.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pe.gobierno.tesoro.model.Quest;
import pe.gobierno.tesoro.service.file.MapFileProcessorService;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

class MapFileProcessorServiceTest {


    @ParameterizedTest
    @MethodSource("provideArgsForReadFile")
    void readFile(String[] args) throws IOException {

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

    private static Stream<Arguments> provideArgsForReadFile() {
        String filePath = "src/test/resources/mapBeforeQuest.txt";

        Arguments noArgs = Arguments.of((Object) new String[]{});
        Arguments filePathOK= Arguments.of((Object) new String[]{filePath});
        Arguments emptyArgs = Arguments.of((Object) new String[]{""});
        Arguments blankArgs = Arguments.of((Object) new String[]{" "});
        Arguments tooMuchArgsFilePathOK = Arguments.of((Object) new String[]{filePath, "foo", "bar"});
        Arguments tooMuchArgsFilePathKO = Arguments.of((Object) new String[]{" ", "foo", "bar"});

        return Stream.of(noArgs, filePathOK, emptyArgs, blankArgs, tooMuchArgsFilePathOK, tooMuchArgsFilePathKO);
    }
}