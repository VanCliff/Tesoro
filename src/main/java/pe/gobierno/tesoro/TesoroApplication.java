package pe.gobierno.tesoro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pe.gobierno.tesoro.service.MapFileProcessorService;
import pe.gobierno.tesoro.service.QuestSimulationService;
import pe.gobierno.tesoro.service.SimulationResultProcessorService;

@SpringBootApplication
public class TesoroApplication {

	@Autowired
	private MapFileProcessorService mapFileProcessorService;

	@Autowired
	private QuestSimulationService questSimulationService;

	@Autowired
	private SimulationResultProcessorService simulationResultProcessorService;


	public static void main(String[] args) {
		SpringApplication.run(TesoroApplication.class, args);
		MapFileProcessorService.readFile();
		QuestSimulationService.execute();
		SimulationResultProcessorService.writeFile();
	}

}
