package com.example.demo.controller;

// ¡ESTOS IMPORTS SON LAS LLAVES QUE FALTABAN!
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

// Esta etiqueta indica al sistema que esta clase es un "Cartero Visual".
@Controller
public class CustomerWebController {

    // CORRECCIÓN: Declaramos la variable 'customerService' y le ponemos el conector automático
    @Autowired
    private CustomerService customerService;

    // Nueva Ruta: Página de Inicio Principal (La matriz interactiva de tu profesor)
    @GetMapping({"/", "/index"})
    public String showIndexPage() {
        return "index";
    }

    // Ruta Modificada: Listar clientes de forma ordenada con paginación
    @GetMapping("/customers")
    public String showCustomersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        System.out.println("Cartero Web: Cargando página número " + page + " con bloques de " + size + " clientes.");

        // Solicitamos el bloque específico de datos al servicio
        Pageable paginaSolicitada = PageRequest.of(page, size);
        Page<Customer> paginaDeClientes = customerService.findAllPaginated(paginaSolicitada);

        // Metemos en la maleta de viaje los datos del bloque y los controles de navegación
        model.addAttribute("listaDeClientes", paginaDeClientes.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaDeClientes.getTotalPages());

        return "customers";
    }
}
