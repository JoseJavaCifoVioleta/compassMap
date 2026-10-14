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
            Model model) {

        System.out.println("Cartero Web: Cargando página número " + page + " con bloques de " + size + " clientes.");

        Pageable paginaSolicitada = PageRequest.of(page, size);
        Page<Customer> paginaDeClientes = customerService.findAllPaginated(paginaSolicitada);

        model.addAttribute("listaDeClientes", paginaDeClientes.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaDeClientes.getTotalPages());

        // MEJORA: Dejamos una "ficha en blanco" en la maleta para que el formulario HTML la rellene.
        model.addAttribute("nuevoCliente", new Customer());

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
