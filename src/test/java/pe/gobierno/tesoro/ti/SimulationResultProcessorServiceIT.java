package pe.gobierno.tesoro.ti;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pe.gobierno.tesoro.service.MapFileProcessorService;
import pe.gobierno.tesoro.service.QuestSimulationService;
import pe.gobierno.tesoro.service.SimulationResultProcessorService;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

@SpringBootTest
class SimulationResultProcessorServiceIT {

    @Test
    void testSimulationOK() throws IOException {
        var filePath = new String[]{"src/test/resources/mapOK.txt"};

        var quest = MapFileProcessorService.readFile(filePath);
        Assertions.assertTrue(quest.isPresent());
        QuestSimulationService.execute(quest.get());
        SimulationResultProcessorService.writeFile(quest.get());

        String fileExpectedPath = "src/test/resources/expectedResult.txt";
        String fileCurrentPath = "src/main/resources/result.txt";

        try (RandomAccessFile randomAccessFile1 = new RandomAccessFile(fileExpectedPath, "r");
             RandomAccessFile randomAccessFile2 = new RandomAccessFile(fileCurrentPath, "r")) {

            FileChannel ch1 = randomAccessFile1.getChannel();
            FileChannel ch2 = randomAccessFile2.getChannel();

            Assertions.assertEquals(ch1.size(), ch2.size());

            long size = ch1.size();
            MappedByteBuffer m1 = ch1.map(FileChannel.MapMode.READ_ONLY, 0L, size);
            MappedByteBuffer m2 = ch2.map(FileChannel.MapMode.READ_ONLY, 0L, size);

            Assertions.assertEquals(m1, m2);
        }
    }
}
