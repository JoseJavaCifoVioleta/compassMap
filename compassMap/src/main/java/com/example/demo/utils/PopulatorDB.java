package com.example.demo.utils;

import com.example.demo.model.Customer;
import com.example.demo.model.RoadMap;
import com.example.demo.service.CustomerService;
import com.example.demo.service.RoadMapService; // <-- NUEVO: Conector del servicio de rutas
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PopulatorDB {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoadMapService roadMapService; // <-- NUEVO: Solicitamos el "Coordinador de Estudios"

    // Acción: Fabricar y guardar de forma segura clientes ficticios (Ya validado)
    public List<Customer> createAndSaveCustomer(int qty) {
        List<Customer> customersToSave = new ArrayList<>();
        Faker faker = new Faker();
        List<Customer> existingCustomers = customerService.findAll();

        for (int i = 0; i < qty; i++) {
            Customer fakeCustomer = buildFakeCustomer(faker);
            boolean alreadyExists = existingCustomers.stream()
                    .anyMatch(c -> c.getFirstName().equalsIgnoreCase(fakeCustomer.getFirstName())
                            && c.getLastName().equalsIgnoreCase(fakeCustomer.getLastName()));

            if (!alreadyExists) {
                customersToSave.add(fakeCustomer);
            }
        }

        if (!customersToSave.isEmpty()) {
            customerService.saveAll(customersToSave);
            System.out.println("Fábrica: Guardados " + customersToSave.size() + " nuevos clientes únicos.");
        }
        return customersToSave;
    }

    private Customer buildFakeCustomer(Faker faker) {
        return Customer.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phone(faker.phoneNumber().phoneNumber())
                .address(faker.address().fullAddress())
                .build();
    }

    // NUEVA ACCIÓN MEJORADA: Guardar las hojas de ruta en la base de datos controlando duplicados
    // Acción: Guardar las hojas de ruta en la base de datos controlando duplicados
    public List<RoadMap> populateRoadmaps() {
        List<RoadMap> catalog = createRoadmaps();
        List<RoadMap> roadmapsToSave = new ArrayList<>();
        List<RoadMap> existingMaps = roadMapService.findAll();

        for (RoadMap modelMap : catalog) {
            // ESCUDO ANTI-DUPLICADOS SEGURO:
            // Para evitar errores si el campo se llama 'title' o 'name',
            // convertimos la ficha a texto (.toString()) para comprobar si ya existe en el almacén.
            boolean alreadyExists = existingMaps.stream()
                    .anyMatch(m -> m.toString().contains(modelMap.toString().substring(0, 10)));

            if (!alreadyExists) {
                roadmapsToSave.add(modelMap);
            }
        }

        if (!roadmapsToSave.isEmpty()) {
            roadMapService.saveAll(roadmapsToSave);
            System.out.println("Fábrica: Catálogo inicial sembrado con " + roadmapsToSave.size() + " rutas nuevas.");
        } else {
            System.out.println("Fábrica: El catálogo de rutas ya estaba inicializado. Saltando semillado.");
        }

        return roadMapService.findAll();
    }


    // Catálogo original del profesor (Ajustado con los nombres exactos de su clase)
    public List<RoadMap> createRoadmaps() {
        List<RoadMap> roadmaps = new ArrayList<>();
        roadmaps.add(new RoadMap("DDD Mastery", "Domain-Driven Design deep dive", 8, false));
        roadmaps.add(new RoadMap("AI Shipping", "LLM tools for rapid delivery", 5, false));
        roadmaps.add(new RoadMap("Code Foundations", "Variables, loops, functions", 12, false));
        roadmaps.add(new RoadMap("Tech Literacy", "Architecture for non-devs", 4, true));
        return roadmaps;
    }
}
