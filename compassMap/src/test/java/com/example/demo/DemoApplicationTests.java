package com.example.demo;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Esta etiqueta le dice al laboratorio de pruebas que levante toda la infraestructura
// de Spring Boot y prepare el acceso a la base de datos simulada en memoria.
@SpringBootTest
class DemoApplicationTests {

	// Solicitamos al sistema que nos preste el "Gestor de Clientes" (Servicio)
	// que ya tiene listas las funciones de guardar, buscar y borrar.
	@Autowired
	private CustomerService customerService;

	// Prueba 1: Control de encendido básico.
	@Test
	void contextLoads() {
		// Verifica de forma automática que todos los componentes esenciales encajen bien.
	}

	// Prueba 2: Experimento de búsqueda en el archivador.
	@Test
	void findCustomerById() {
		// Creamos y guardamos un cliente real primero para asegurarnos de que existe en esta prueba.
		String idReal = UUID.randomUUID().toString();
		Customer clientePrueba = new Customer(idReal, "Ana", "García", "ana@mail.com", "1234", "Calle 1");
		customerService.createCustomer(clientePrueba);

		// Intentamos buscar el cliente que acabamos de registrar usando su código único.
		Customer customerFound1 = customerService.getCustomerById(idReal);
		System.out.println("Customer found 1 (Existente): " + customerFound1);
		assertNotNull(customerFound1); // Valida que realmente se haya encontrado algo.

		// Experimento de control de errores: buscamos un código que sabemos que no existe ("banana").
		System.out.println("Intentando buscar un cliente inexistente ('banana')...");
		assertThrows(java.util.NoSuchElementException.class, () -> {
			customerService.getCustomerById("banana");
		});
	}

	// Prueba 3: Experimento de eliminación segura.
	@Test
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
	void javaFakerCreateObjects() {
		// Activamos la máquina "Faker", que es como un diccionario de nombres de imitación muy realistas.
		Faker faker = new Faker();

		System.out.println("Iniciando la creación masiva de 1000 clientes simulados...");

		for (int i = 0; i < 1000; i++) {
			// Es vital construir un cliente nuevo en cada vuelta del bucle.
			// Cada uno recibe un código único (UUID) y datos simulados por la factoría Faker.
			Customer newCustomer = new Customer(
					UUID.randomUUID().toString(), // Genera un código único automático e irrepetible.
					faker.name().firstName(),     // Nombre ficticio realista.
					faker.name().lastName(),      // Apellido ficticio realista.
					faker.internet().emailAddress(), // Correo electrónico estructurado simulado.
					faker.phoneNumber().phoneNumber(), // Teléfono ficticio.
					faker.address().fullAddress()    // Dirección postal completa simulada.
			);

			// Enviamos el cliente recién creado al gestor para que lo archive de forma permanente.
			customerService.createCustomer(newCustomer);
		}

		System.out.println("¡Éxito! Se han creado e insertado los 1000 registros en la base de datos.");
	}
}
