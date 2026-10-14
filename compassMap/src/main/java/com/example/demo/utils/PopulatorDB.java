package com.example.demo.utils;

import com.example.demo.model.Customer;
import com.example.demo.model.RoadMap;
import com.example.demo.service.CustomerService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Esta clase funciona como una "Fábrica Automatizada" de datos ficticios.
// Su única misión es rellenar los archivadores con simulaciones realistas para pruebas y demostraciones.
@Component
public class PopulatorDB {

    // Conectamos de forma automática el "Director de la Oficina" (Servicio)
    // para poder enviarle las fichas que fabrique esta factoría.
    @Autowired
    private CustomerService customerService;

    // Acción: Fabricar y guardar de forma segura una cantidad determinada de clientes ficticios.
    public List<Customer> createAndSaveCustomer(int qty) {
        List<Customer> customersToSave = new ArrayList<>();
        Faker faker = new Faker();

        // Recuperamos la lista de clientes que ya existen en el sistema para comprobar duplicados.
        List<Customer> existingCustomers = customerService.findAll();

        for (int i = 0; i < qty; i++) {
            // Utilizamos el constructor avanzado (Builder) para diseñar un cliente simulado muy realista.
            Customer fakeCustomer = buildFakeCustomer(faker);

            // CONTROL DE DUPLICADOS INTEGRADO:
            // Comprobamos si en nuestro almacén ya existe alguien con el mismo nombre y apellido.
            boolean alreadyExists = existingCustomers.stream()
                    .anyMatch(c -> c.getFirstName().equalsIgnoreCase(fakeCustomer.getFirstName())
                            && c.getLastName().equalsIgnoreCase(fakeCustomer.getLastName()));

            // Si el cliente simulado es totalmente nuevo, lo añadimos a la lista de empaquetado.
            if (!alreadyExists) {
                customersToSave.add(fakeCustomer);
            }
        }

        // Si la lista contiene registros únicos que pasaron el filtro, se envían al archivador central de golpe.
        if (!customersToSave.isEmpty()) {
            customerService.saveAll(customersToSave);
            System.out.println("Fábrica: Se han guardado con éxito " + customersToSave.size() + " nuevos clientes únicos.");
        } else {
            System.out.println("Fábrica: No se añadieron registros nuevos porque todos los generados ya existían en el sistema.");
        }

        return customersToSave;
    }

    // Herramienta interna: Molde automatizado para rellenar campos con datos ficticios realistas.
    private Customer buildFakeCustomer(Faker faker) {
        // Aprovechamos la potencia del patrón '.builder()' de Lombok.
        // Permite construir un objeto de forma modular, limpia y muy legible.
        // NOTA: El identificador único 'id' no lo rellenamos aquí; se encarga automáticamente el Servicio/JPA.
        return Customer.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .address(faker.address().fullAddress())
                .build();
    }

    // Acción: Fábrica de hojas de ruta de aprendizaje fijas (Semillas base del sistema).
    public List<RoadMap> createRoadmaps() {
        List<RoadMap> roadmaps = new ArrayList<>();
        // Creamos un catálogo estático de rutas recomendadas que servirá como guía base para los usuarios.
        roadmaps.add(new RoadMap("DDD Mastery", "Domain-Driven Design deep dive", 8, false));
        roadmaps.add(new RoadMap("AI Shipping", "LLM tools for rapid delivery", 5, false));
        roadmaps.add(new RoadMap("Code Foundations", "Variables, loops, functions", 12, false));
        roadmaps.add(new RoadMap("Tech Literacy", "Architecture for non-devs", 4, true));
        return roadmaps;
    }
}
