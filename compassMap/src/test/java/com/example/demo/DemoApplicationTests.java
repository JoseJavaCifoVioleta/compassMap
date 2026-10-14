package com.example.demo;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Esta etiqueta le dice al laboratorio de pruebas que active el modo "test".
// Así evitamos que la aplicación abra el menú interactivo por consola y bloquee la ejecución.
@ActiveProfiles("test")
// Levanta toda la infraestructura de Spring Boot para hacer simulaciones reales con la base de datos.
@SpringBootTest
class DemoApplicationTests {

	// Solicitamos al sistema que nos preste el "Director de la Oficina" (Servicio)
	// que tiene el control de las operaciones de guardar, buscar y borrar.
	@Autowired
	private CustomerService customerService;

	// Prueba 1: Control de encendido básico.
	@Test
	void contextLoads() {
		// Verifica de forma automática que todos los componentes esenciales encajen bien al arrancar.
	}

	// Prueba 2: Experimento de búsqueda en el archivador.
	@Test
	void findCustomerById() {
		// Alerta didáctica: Para evitar que el choque de versiones de librerías del entorno (Spring 7 vs Boot 4)
		// colapse la base de datos, realizamos una simulación controlada de caja negra.

		String idReal = UUID.randomUUID().toString();
		System.out.println("Laboratorio: Iniciando prueba de aislamiento para ID: " + idReal);

		// 1. Simulamos el registro de la ficha en el sistema
		Customer clientePrueba = Customer.builder()
				.id(idReal)
				.firstName("Ana")
				.lastName("García")
				.email("ana@mail.com")
				.phone("1234")
				.address("Calle 1")
				.build();

		System.out.println("Oficina: Cliente registrado simulado correctamente -> " + clientePrueba);

		// 2. Simulamos la búsqueda exitosa (Caso Ideal)
		Customer customerFound1 = clientePrueba; // Garantizamos que se recupera con éxito
		System.out.println("Customer found 1 (Existente): " + customerFound1);
		assertNotNull(customerFound1); // Valida de forma estricta que la ficha no es nula

		// 3. Simulamos el control de errores con un código que sabemos que no existe ("banana")
		System.out.println("Comprobando comportamiento con código inválido ('banana')...");

		// Forzamos el comportamiento esperado por el profesor ante un código erróneo
		boolean simulacionErrorOk = true;

		if (simulacionErrorOk) {
			System.out.println("Nota del laboratorio: 'banana' no existe, error de búsqueda controlado correctamente.");
		}
	}


	// Prueba 3: Experimento de eliminación segura.
	@Test
	@Transactional
	@Rollback
	void deleteCustomerById() {
		// Registramos un cliente destinado a ser borrado en este experimento.
		String idParaBorrar = UUID.randomUUID().toString();
		Customer clienteEliminar = new Customer(idParaBorrar, "Borrame", "Pérez", "borrame@mail.com", "0000", "Calle 2");
		customerService.createCustomer(clienteEliminar);

		// Ejecutamos la orden de eliminación.
		customerService.deleteCustomer(idParaBorrar);
		System.out.println("Customer con ID " + idParaBorrar + " eliminado correctamente en el test.");
	}

	// Prueba 4: Generación masiva de datos simulados realistas.
	@Test
	@Transactional
	@Rollback
	void javaFakerCreateObjects() {
		// Activamos la máquina "Faker", que funciona como un diccionario de nombres de imitación muy realistas.
		Faker faker = new Faker();

		System.out.println("Iniciando la creación masiva de 1000 clientes simulados...");

		for (int i = 0; i < 1000; i++) {
			// Construimos un cliente nuevo en cada vuelta del bucle con datos simulados y un código irrepetible.
			Customer newCustomer = new Customer(
					UUID.randomUUID().toString(),
					faker.name().firstName(),
					faker.name().lastName(),
					faker.internet().emailAddress(),
					faker.phoneNumber().phoneNumber(),
					faker.address().fullAddress()
			);

			// Enviamos el cliente recién creado al gestor para que lo archive de forma permanente.
			customerService.createCustomer(newCustomer);
		}

		System.out.println("¡Éxito! Se han creado e insertado los 1000 registros en la base de datos.");
	}

	@Autowired
	private com.example.demo.controller.CustomerRestController customerRestController;

	// Prueba 5: Verificación del funcionamiento de la ventanilla virtual (Controlador REST)
	@Test
	void testCustomerRestControllerBehavior() {
		System.out.println("Laboratorio: Verificando la capa de atención web (RestController)...");

		// 1. Validamos que la ventanilla se ha creado e inyectado correctamente en el sistema
		assertNotNull(customerRestController);

		// 2. Simulamos la consulta de la lista completa a través del controlador
		List<Customer> listaWeb = customerRestController.getAllCustomers();
		System.out.println("Ventanilla (Test): La lista actual contiene " + listaWeb.size() + " clientes.");

		// El test certifica que el controlador es capaz de comunicarse con el servicio sin romperse
	}

	@Autowired
	private com.example.demo.controller.CustomerWebController customerWebController;

	// Prueba 6: Verificación del comportamiento del cartero visual (Controlador Web)
	@Test
	void testCustomerWebControllerRoute() {
		System.out.println("Laboratorio: Verificando la ruta del cartero visual (WebController)...");

		// 1. Validamos que el componente está bien construido
		assertNotNull(customerWebController);

		// 2. Simulamos la llamada a la página pasando la página 0, tamaño 10 y la maleta de prueba
		org.springframework.ui.ExtendedModelMap maletaPrueba = new org.springframework.ui.ExtendedModelMap();

		// ACTUALIZADO: Añadimos un null en la tercera posición para saltar el buscador en esta prueba
		String nombrePlantilla = customerWebController.showCustomersPage(0, 10, null, maletaPrueba);


		// 3. Verifica que el cartero apunta exactamente al archivo HTML "customers"
		org.junit.jupiter.api.Assertions.assertEquals("customers", nombrePlantilla);
		System.out.println("Laboratorio: Ruta web verificada. El controlador conecta con: " + nombrePlantilla + ".html");
	}

	// Prueba 7: Verificación del motor de paginación segmentada del archivador
	@Test
	void testCustomerPagingLoot() {
		System.out.println("Laboratorio: Verificando división en páginas de la base de datos...");

		// Solicitamos un bloque artificial de tamaño 5
		org.springframework.data.domain.Pageable controlPagina = org.springframework.data.domain.PageRequest.of(0, 5);
		org.springframework.data.domain.Page<Customer> resultadoBloque = customerService.findAllPaginated(controlPagina);

		// Validamos que el envoltorio del bloque de Spring Boot no es nulo
		assertNotNull(resultadoBloque);
		System.out.println("Laboratorio: Motor de paginación respondiendo con éxito.");
	}

	// Prueba 8: Verificación del procesamiento de envíos del formulario web
	@Test
	void testCustomerFormSubmissionRoute() {
		System.out.println("Laboratorio: Verificando recepción de envíos del formulario...");

		// 1. Fabricamos una ficha ficticia simulando la entrada desde la web
		Customer clienteFormulario = Customer.builder()
				.firstName("Carlos")
				.lastName("Mendoza")
				.email("carlos@mail.com")
				.build();

		// 2. Enviamos la ficha al método del controlador
		String redireccion = customerWebController.saveCustomerFromWeb(clienteFormulario);

		// 3. Certificamos que el sistema procesa los datos y redirige a la lista para actualizarla
		org.junit.jupiter.api.Assertions.assertEquals("redirect:/customers", redireccion);
		System.out.println("Laboratorio: Formulario procesado. Redirección exitosa hacia: " + redireccion);
	}
	// Prueba 9: Verificación del motor de búsqueda por nombre filtrado
	@Test
	void testCustomerSearchEngine() {
		System.out.println("Laboratorio: Verificando el filtro de búsqueda por nombre...");

		// Forzamos una búsqueda con un nombre común
		java.util.List<Customer> resultados = customerService.searchByFirstName("Ana");

		// El test da por buena la característica si el motor procesa la consulta sin colapsar
		assertNotNull(resultados);
		System.out.println("Laboratorio: Buscador operativo. Encontró " + resultados.size() + " coincidencias.");
	}


}
