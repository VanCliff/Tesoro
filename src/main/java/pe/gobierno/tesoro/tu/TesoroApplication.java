package pe.gobierno.tesoro.tu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pe.gobierno.tesoro.tu.model.Quest;
import pe.gobierno.tesoro.tu.service.MapFileProcessorService;
import pe.gobierno.tesoro.tu.service.QuestSimulationService;
import pe.gobierno.tesoro.tu.service.SimulationResultProcessorService;

import java.util.Optional;

@SpringBootApplication
public class TesoroApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TesoroApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Optional<Quest> optionalQuest = MapFileProcessorService.readFile(args);

		if (optionalQuest.isPresent()) {
			QuestSimulationService.execute(optionalQuest.get());
			SimulationResultProcessorService.writeFile(optionalQuest.get());
		}

	}

}
