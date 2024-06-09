package pe.gobierno.tesoro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pe.gobierno.tesoro.service.MapFileProcessorService;
import pe.gobierno.tesoro.service.QuestSimulationService;
import pe.gobierno.tesoro.service.SimulationResultProcessorService;

@SpringBootApplication
public class TesoroApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesoroApplication.class, args);
		MapFileProcessorService.readFile();
		QuestSimulationService.execute();
		SimulationResultProcessorService.writeFile();
	}

}
