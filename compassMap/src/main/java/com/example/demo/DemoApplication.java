package com.example.demo;

import com.example.demo.service.CustomerService;
import com.example.demo.utils.BackOffice;
import com.example.demo.utils.PopulatorDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	@Autowired
	private PopulatorDB populatorDB;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Al arrancar, ejecutamos de forma automática el semillado de clientes y rutas
		populatorDB.createAndSaveCustomer(100);
		populatorDB.populateRoadmaps();

		// Si detectamos el perfil de laboratorio, bloqueamos el menú para no congelar los tests
		for (String profile : environment.getActiveProfiles()) {
			if (profile.equalsIgnoreCase("test")) {
				System.out.println("Oficina: Entorno de laboratorio activo. Menú omitido.");
				return;
			}
		}

		// Inicio normal del menú por consola
		BackOffice.startBackOffice(populatorDB, customerService);
	}
}
