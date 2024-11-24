package pe.gobierno.tesoro;

import pe.gobierno.tesoro.model.Quest;
import pe.gobierno.tesoro.service.MapFileProcessorService;
import pe.gobierno.tesoro.service.QuestSimulationService;
import pe.gobierno.tesoro.service.SimulationResultProcessorService;

import java.io.IOException;
import java.util.Optional;

public class TesoroApplication {

	public static void main(String[] args) throws IOException {
		Optional<Quest> optionalQuest = MapFileProcessorService.readFile(args);

		if (optionalQuest.isPresent()) {
			QuestSimulationService.execute(optionalQuest.get());
			SimulationResultProcessorService.writeFile(optionalQuest.get());
		}

	}

}
