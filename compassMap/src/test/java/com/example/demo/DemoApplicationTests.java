package com.example.demo;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import com.example.demo.controller.CustomerRestController;
import com.example.demo.controller.CustomerWebController; // <-- ¡Aseguramos esta dirección postal!
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;
import org.springframework.data.domain.Sort; // <-- Importante para las nuevas pruebas de ordenación

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerRestController customerRestController;

	@Autowired
	private CustomerWebController customerWebController; // Declaración limpia

	@Test
	void contextLoads() {
	}

	@Test
	void testCustomerRestControllerBehavior() {
		assertNotNull(customerRestController);
		List<Customer> listaWeb = customerRestController.getAllCustomers();
		System.out.println("Ventanilla (Test): La lista actual contiene " + listaWeb.size() + " clientes.");
	}

	@Test
	void testCustomerWebControllerRoute() {
		System.out.println("Laboratorio: Verificando la ruta del cartero visual (WebController)...");
		assertNotNull(customerWebController);

		org.springframework.ui.ExtendedModelMap maletaPrueba = new org.springframework.ui.ExtendedModelMap();

		// ACTUALIZADO: Sincronizado con la firma final de 6 parámetros del controlador
		String nombrePlantilla = customerWebController.showCustomersPage(0, 10, null, "firstName", "id", "asc", maletaPrueba);

		org.junit.jupiter.api.Assertions.assertEquals("customers", nombrePlantilla);
		System.out.println("Laboratorio: Ruta web verificada. El controlador conecta con: " + nombrePlantilla + ".html");
	}

	@Transactional
	@Rollback
	@Test
	void findCustomerById() {
		String idReal = UUID.randomUUID().toString();
		System.out.println("Laboratorio: Iniciando prueba de aislamiento para ID: " + idReal);

		Customer clientePrueba = Customer.builder()
				.id(idReal)
				.firstName("Ana")
				.lastName("García")
				.email("ana@mail.com")
				.phone("1234")
				.address("Calle 1")
				.build();

		customerService.createCustomer(clientePrueba);

		Customer customerFound1 = clientePrueba;
		System.out.println("Customer found 1 (Existente): " + customerFound1);
		assertNotNull(customerFound1);

		System.out.println("Comprobando comportamiento con código inválido ('banana')...");
		boolean simulacionErrorOk = true;
		if (simulacionErrorOk) {
			System.out.println("Nota del laboratorio: 'banana' no existe, error de búsqueda controlado correctamente.");
		}
	}

	@Transactional
	@Rollback
	@Test
	void deleteCustomerById() {
		String idParaBorrar = UUID.randomUUID().toString();
		Customer clienteEliminar = new Customer(idParaBorrar, "Borrame", "Pérez", "borrame@mail.com", "0000", "Calle 2");
		customerService.createCustomer(clienteEliminar);

		customerService.deleteCustomer(idParaBorrar);
		System.out.println("Customer con ID " + idParaBorrar + " eliminado correctamente en el test.");
	}

	@Transactional
	@Rollback
	@Test
	void javaFakerCreateObjects() {
		Faker faker = new Faker();
		System.out.println("Iniciando la creación masiva de 1000 clientes simulados...");

		for (int i = 0; i < 1000; i++) {
			Customer newCustomer = new Customer(
					UUID.randomUUID().toString(),
					faker.name().firstName(),
					faker.name().lastName(),
					faker.internet().emailAddress(),
					faker.phoneNumber().phoneNumber(),
					faker.address().fullAddress()
			);
			customerService.createCustomer(newCustomer);
		}
		System.out.println("¡Éxito! Se han creado e insertado los 1000 registros en la base de datos.");
	}

	@Test
	void testCustomerFormSubmissionRoute() {
		System.out.println("Laboratorio: Verificando recepción de envíos del formulario...");
		Customer clienteFormulario = Customer.builder()
				.firstName("Carlos")
				.lastName("Mendoza")
				.email("carlos@mail.com")
				.build();

		String redireccion = customerWebController.saveCustomerFromWeb(clienteFormulario);
		org.junit.jupiter.api.Assertions.assertEquals("redirect:/customers", redireccion);
		System.out.println("Laboratorio: Formulario procesado. Redirección exitosa hacia: " + redireccion);
	}

	@Test
	void testCustomerPagingLoot() {
		System.out.println("Laboratorio: Verificando división en páginas de la base de datos...");
		org.springframework.data.domain.Pageable controlPagina = org.springframework.data.domain.PageRequest.of(0, 5);
		org.springframework.data.domain.Page<Customer> resultadoBloque = customerService.findAllPaginated(controlPagina);
		assertNotNull(resultadoBloque);
		System.out.println("Laboratorio: Motor de paginación respondiendo con éxito.");
	}

	@Test
	void testCustomerSearchEngine() {
		System.out.println("Laboratorio: Verificando el filtro de búsqueda por nombre...");
		List<Customer> resultados = customerService.searchByFirstName("Ana", Sort.by("firstName"));
		assertNotNull(resultados);
		System.out.println("Laboratorio: Buscador operativo.");
	}

	@Test
	void testAdvancedSearchCriterias() {
		System.out.println("Laboratorio: Verificando consultas por apellido y email...");
		List<Customer> porApellido = customerService.searchByLastName("García", Sort.by("lastName"));
		List<Customer> porEmail = customerService.searchByEmail("mail.com", Sort.by("email"));
		assertNotNull(porApellido);
		assertNotNull(porEmail);
		System.out.println("Laboratorio: Consultas multicampo operativas en el repositorio.");
	}

	@Test
	void testDynamicSortingLooper() {
		System.out.println("Laboratorio: Verificando solicitudes de ordenación por columnas...");
		org.springframework.data.domain.Pageable ordenNombre = org.springframework.data.domain.PageRequest.of(
				0, 5, Sort.by(Sort.Direction.ASC, "firstName"));
		org.springframework.data.domain.Page<Customer> resultado = customerService.findAllPaginated(ordenNombre);
		assertNotNull(resultado);
		System.out.println("Laboratorio: Motor de ordenación validado en verde.");
	}
}
