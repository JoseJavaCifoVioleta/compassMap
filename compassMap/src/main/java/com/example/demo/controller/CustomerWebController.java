package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
public class CustomerWebController {

    @Autowired
    private CustomerService customerService;

    @GetMapping({"/", "/index"})
    public String showIndexPage() {
        return "index";
    }

    @GetMapping("/customers")
    public String showCustomersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String buscarNombre, // <-- NUEVO: Captura el texto del buscador
            Model model) {

        // Creamos una ficha en blanco para el formulario de inserción
        model.addAttribute("nuevoCliente", new Customer());

        // LÓGICA DE BÚSQUEDA: Si el usuario ha escrito algo en el buscador
        if (buscarNombre != null && !buscarNombre.trim().isEmpty()) {
            System.out.println("Cartero Web: Filtrando clientes por nombre: " + buscarNombre);
            java.util.List<Customer> resultadosFiltrados = customerService.searchByFirstName(buscarNombre);

            model.addAttribute("listaDeClientes", resultadosFiltrados);
            model.addAttribute("paginaActual", 0);
            model.addAttribute("totalPaginas", 1); // Al estar filtrado, lo tratamos como página única
            model.addAttribute("textoBuscado", buscarNombre); // Guardamos lo que buscó para mostrarlo en la caja
            return "customers";
        }

        // Si no hay búsqueda, se ejecuta el motor de paginación tradicional que ya teníamos
        Pageable paginaSolicitada = PageRequest.of(page, size);
        Page<Customer> paginaDeClientes = customerService.findAllPaginated(paginaSolicitada);

        model.addAttribute("listaDeClientes", paginaDeClientes.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaDeClientes.getTotalPages());
        model.addAttribute("textoBuscado", "");

        return "customers";
    }


    // NUEVA ACCIÓN: Recibir los datos del formulario web y guardarlos de verdad.
    @PostMapping("/customers/save")
    public String saveCustomerFromWeb(@ModelAttribute("nuevoCliente") Customer customer) {
        System.out.println("Cartero Web: Recibidos datos del formulario para registrar a: " + customer.getFirstName());

        // Guardamos el cliente en el archivador digital a través del servicio
        customerService.createCustomer(customer);

        // Redirigimos al usuario de vuelta a la lista para que vea su nuevo registro al instante
        return "redirect:/customers";
    }
}
