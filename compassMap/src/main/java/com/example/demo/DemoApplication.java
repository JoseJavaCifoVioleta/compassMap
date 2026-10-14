package com.example.demo;

import com.example.demo.service.CustomerService;
import com.example.demo.utils.BackOffice;
import com.example.demo.utils.PopulatorDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication  implements CommandLineRunner {

	@Autowired
	PopulatorDB populatorDB;

	@Autowired
	private CustomerService customerService; // Añadimos el servicio aquí arriba si no estaba

	@Autowired
	private org.springframework.core.env.Environment environment;

	//@Autowired
	//BackOffice backOffice;

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// JAVA SE strategy with STATIC methods
		// SO we call the class and then
		// the method
		//BackOffice.startBackOffice(populatorDB);
		// Le pasamos tanto la fábrica de datos como el director del archivador
		//BackOffice.startBackOffice(populatorDB, customerService);

		// JAVA EE, Spring Boot tools
		// with @Autowired and DI
		//backOffice.startMenu(populatorDB);


		// Preguntamos al sistema: ¿Estamos ejecutando pruebas automatizadas (modo test)?
		// Al hacer los tests, Spring Boot activa un perfil especial por defecto.
		for (String profile : environment.getActiveProfiles()) {
			if (profile.equalsIgnoreCase("test")) {
				System.out.println("Oficina: Detectado entorno de laboratorio (Test). Bloqueamos el menú para no atascar el proceso.");
				return; // Salimos del método inmediatamente sin abrir la consola
			}
		}

		// Si no estamos en un test, el programa continuará aquí y abrirá el menú interactivo para el usuario
		BackOffice.startBackOffice(populatorDB, customerService);
	}

}
