package pe.gobierno.tesoro.service;

import org.springframework.stereotype.Service;
import pe.gobierno.tesoro.model.Quest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

        Quest quest = new Quest(Collections.emptyList(), new ConcurrentHashMap<>(), 0L, 0L);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.get()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

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
