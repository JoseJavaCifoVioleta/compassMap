package com.example.demo.controller;

import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Esta etiqueta indica al sistema que esta clase es un "Cartero Visual".
// Su trabajo no es enviar datos en bruto, sino recibir peticiones web,
// recoger las fichas del archivador y empaquetarlas dentro de una página HTML (plantilla).
@Controller
public class CustomerWebController {

    // Conectamos automáticamente con el "Director de la Oficina" (Servicio)
    // para solicitarle las fichas de los clientes que queremos mostrar en pantalla.
    @Autowired
    private CustomerService customerService;

    // Acción: Preparar la pantalla visual de clientes.
    // Se activa cuando un usuario escribe en su navegador: http://localhost:8080/customers
    @GetMapping("/customers")
    public String showCustomersPage(Model model) {
        System.out.println("Cartero Web: Preparando el envío de la página de clientes...");

        // La herramienta 'Model' funciona como una "maleta de viaje".
        // Metemos dentro de la maleta la lista completa de clientes reales bajo el nombre "listaDeClientes".
        // Así, la página HTML podrá abrir la maleta y pintar los datos en la pantalla.
        model.addAttribute("listaDeClientes", customerService.findAll());

        // Le indicamos al cartero que busque y renderice la plantilla llamada 'customers.html'.
        return "customers";
    }
}
