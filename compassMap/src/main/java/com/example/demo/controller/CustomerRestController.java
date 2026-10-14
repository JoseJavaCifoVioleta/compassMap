package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Esta etiqueta indica al sistema que esta clase funciona como una "ventanilla virtual".
// Se encarga de escuchar peticiones que vengan desde internet (navegadores, móviles, etc.)
// y devolver las respuestas directamente en un formato de texto universal (JSON) que todos entienden.
@RestController
// Definimos la dirección de la ventanilla. Cualquier petición que empiece por "/api/customers" llegará aquí.
@RequestMapping("/api/customers")
public class CustomerRestController {

    // Conectamos de forma automática al "Director de la Oficina" (Servicio)
    // para que sea él quien busque o guarde la información real en el archivador.
    @Autowired
    private CustomerService customerService;

    // Acción: Consultar el listado completo de clientes a través de la web.
    // Se activa cuando alguien entra en la dirección web: GET http://localhost:8080/api/customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        System.out.println("Ventanilla: Recibida solicitud para listar todos los clientes.");
        return customerService.findAll();
    }

    // Acción: Localizar un único cliente usando su código en la URL.
    // Ejemplo de uso en el navegador: GET http://localhost:8080/api/customers/un-codigo-falso
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable String id) {
        System.out.println("Ventanilla: Buscando al cliente con código: " + id);
        return customerService.getCustomerById(id);
    }

    // Acción: Recibir los datos de un nuevo cliente por internet y registrarlo.
    // La etiqueta '@RequestBody' funciona como un buzón: agarra los datos que envía el usuario en formato texto
    // y los transforma automáticamente en un objeto 'Customer' de Java listo para guardar.
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        System.out.println("Ventanilla: Recibida orden de registro para un nuevo cliente.");
        return customerService.createCustomer(customer);
    }

    // Acción: Eliminar un cliente permanentemente enviando su código único.
    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable String id) {
        System.out.println("Ventanilla: Solicitud de baja para el ID: " + id);
        customerService.deleteCustomer(id);
        return "Ficha con código " + id + " eliminada del sistema con éxito.";
    }
}
