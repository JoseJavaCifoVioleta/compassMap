package com.example.demo.utils;

import com.example.demo.model.CodeProfile;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerProfile;
import com.example.demo.model.RoadMap;
import com.example.demo.service.*;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@SuppressWarnings("unused")
public class PopulatorDB {

    // Quitamos 'private' para que otras clases puedan acceder a ellos directamente
    @Autowired
    CustomerService customerService;

    @Autowired
    RoadMapService roadMapService;

    @Autowired
    CustomerProfileService customerProfileService;

    // Estos se pueden quedar privados porque solo los usa internamente PopulatorDB
    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionAndAnswerService questionAndAnswerService;

    public List<Customer> createAndSaveCustomer(int qty) {
        List<Customer> customers = new ArrayList<>();
        Faker faker = new Faker();

        for (int i = 0; i < qty; i++) {
            customers.add(new Customer(
                    UUID.randomUUID().toString(),
                    faker.name().firstName(),
                    faker.name().lastName(),
                    faker.internet().emailAddress(),
                    faker.phoneNumber().phoneNumber(),
                    faker.address().fullAddress()
            ));
        }
        customerService.saveAll(customers);
        return customers;
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

    public List<RoadMap> createRoadMaps() {
        List<RoadMap> roadmaps = new ArrayList<>();
        roadmaps.add(new RoadMap("DDD Mastery", "Domain-Driven Design deep dive", 8, false));
        roadmaps.add(new RoadMap("AI Shipping", "LLM tools for rapid delivery", 5, false));
        roadmaps.add(new RoadMap("Code Foundations", "Variables, loops, functions", 12, false));
        roadmaps.add(new RoadMap("Tech Literacy", "Architecture for non-devs", 4, true));
        return roadmaps;
    }

    public List<RoadMap> createAndSaveRoadMap() {
        List<RoadMap> roadmaps = createRoadMaps();
        roadMapService.saveAll(roadmaps);
        return roadmaps;
    }

    public List<CustomerProfile> createCustomerProfiles() {
        List<CustomerProfile> customerProfiles = new ArrayList<>();

        // Perfil 1
        customerProfiles.add(CustomerProfile.builder()
                .id("p-001")
                .codeProfile(CodeProfile.ARCHITECT)
                .name("Expert full-stack + AI")
                .description("Ja domina les dues disciplines i vol aprofundir en ambdues")
                .goal("Potenciar la integración de sistemas complejos con Inteligencia Artificial")
                .build());

        // Perfil 2
        customerProfiles.add(CustomerProfile.builder()
                .id("p-002")
                .codeProfile(CodeProfile.ARCHITECT)
                .name("Desenvolupador sènior que ja sab ML")
                .description("Vol pujar de nivell en programació, sense més ML")
                .goal("Perfeccionar patrones de diseño y escalabilidad en desarrollo tradicional")
                .build());

        // Perfil 3
        customerProfiles.add(CustomerProfile.builder()
                .id("p-003")
                .codeProfile(CodeProfile.BEGINNER)
                .name("Programador que vol entrar en ML")
                .description("Sòlid en codi i vol aprendre Machine Learning de debò")
                .goal("Adquirir bases matemáticas y algorítmicas de aprendizaje automático")
                .build());

        // Perfil 4
        customerProfiles.add(CustomerProfile.builder()
                .id("p-004")
                .codeProfile(CodeProfile.ARCHITECT)
                .name("Pujar de nivell en programació")
                .description("Vol millorar com a desenvolupador/arquitecte")
                .goal("Evolucionar hacia roles de liderazgo técnico y diseño de software")
                .build());

        // Perfil 5
        customerProfiles.add(CustomerProfile.builder()
                .id("p-005")
                .codeProfile(CodeProfile.ARCHITECT)
                .name("ML Engineer que no vol programar més")
                .description("Ja sap programar i ML, vol aprofundir només en ML")
                .goal("Especializarse puramente en modelos analíticos avanzados e infraestructura de datos")
                .build());

        // Perfil 6
        customerProfiles.add(CustomerProfile.builder()
                .id("p-006")
                .codeProfile(CodeProfile.ARCHITECT)
                .name("Usuari avançat d'eines AI/ML")
                .description("Saps programar i ML, prefereix usar eines sense aprofundir")
                .goal("Optimizar flujos utilizando integraciones y servicios de IA ya existentes")
                .build());

        // Perfil 7
        customerProfiles.add(CustomerProfile.builder()
                .id("p-007")
                .codeProfile(CodeProfile.BEGINNER)
                .name("Programador que vol usar ML")
                .description("Saps programar, vols aprendre ML de forma pragmàtica")
                .goal("Aplicar librerías de Inteligencia Artificial directamente en sus aplicaciones")
                .build());

        // Perfil 8
        customerProfiles.add(CustomerProfile.builder()
                .id("p-008")
                .codeProfile(CodeProfile.ARCHITECT)
                .name("Usar AI per crear projectes")
                .description("Saps programar, només vols productivitat amb AI")
                .goal("Apalancar asistentes de código globales para desarrollo acelerado")
                .build());

        // Perfil 9
        customerProfiles.add(CustomerProfile.builder()
                .id("p-009")
                .codeProfile(CodeProfile.BEGINNER)
                .name("Data Scientist que vol aprendre a programar")
                .description("Ja sap ML, vol reforçar les bases de programació")
                .goal("Escribir código de producción más limpio, robusto y mantenible")
                .build());

        // Perfil 10
        customerProfiles.add(CustomerProfile.builder()
                .id("p-010")
                .codeProfile(CodeProfile.BEGINNER)
                .name("Data Scientist que vol aprendre a programar millor")
                .description("Sap ML, vol aprendre a programar millor")
                .goal("Dominar buenas prácticas de ingeniería de software applied al dato")
                .build());

        // Perfil 11
        customerProfiles.add(CustomerProfile.builder()
                .id("p-011")
                .codeProfile(CodeProfile.BEGINNER)
                .name("Començar de 0 en tot")
                .description("No sap ni programar ni ML, vol aprendre ambdues")
                .goal("Iniciar una trayectoria integral de aprendizaje desde los fundamentos absolutos")
                .build());

        // Perfil 12
        customerProfiles.add(CustomerProfile.builder()
                .id("p-012")
                .codeProfile(CodeProfile.BEGINNER)
                .name("Començar de 0 en programació")
                .description("Vol aprendre a programar de debò")
                .goal("Construir la lógica inicial a través de variables, bucles y funciones")
                .build());

        // Perfil 13
        customerProfiles.add(CustomerProfile.builder()
                .id("p-013")
                .codeProfile(CodeProfile.NON_DEVELOPER)
                .name("Pujar de nivell només en ML")
                .description("No vol programar, ja sap ML i vol aprofundir")
                .goal("Centrarse en la investigación teórica o refinamiento estadístico de modelos")
                .build());

        // Perfil 14
        customerProfiles.add(CustomerProfile.builder()
                .id("p-014")
                .codeProfile(CodeProfile.NON_DEVELOPER)
                .name("Usuari de ML sense aprofundir")
                .description("Sap una mica de ML, no vol programar ni aprofundir")
                .goal("Utilizar tableros e interfaces de análisis predictivo de alto nivel")
                .build());

        // Perfil 15
        customerProfiles.add(CustomerProfile.builder()
                .id("p-015")
                .codeProfile(CodeProfile.NON_DEVELOPER)
                .name("Curs introductori d'IA/ML")
                .description("Només vol entender el llenguatge dels tècnics d'IA")
                .goal("Facilitar la comunicación bilingüe entre negocio y equipos de ingeniería de datos")
                .build());

        // Perfil 16
        customerProfiles.add(CustomerProfile.builder()
                .id("p-016")
                .codeProfile(CodeProfile.NON_DEVELOPER)
                .name("Curs introductori general")
                .description("Només cultura general o entender el que fan els altres")
                .goal("Comprender el panorama tecnológico actual sin impacto técnico operativo")
                .build());

        return customerProfiles;
    }
    public List<CustomerProfile> createAndSaveCustomerProfiles() {
        return createAndSaveCustomerProfiles();
    }
}
