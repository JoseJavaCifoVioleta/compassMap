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
            @RequestParam(required = false) String buscarTexto, // Cambiamos el nombre a uno más genérico
            @RequestParam(defaultValue = "firstName") String criterio, // NUEVO: Captura el campo elegido en el desplegable
            Model model) {

        model.addAttribute("nuevoCliente", new Customer());
        model.addAttribute("criterioSeleccionado", criterio); // Guardamos el criterio para mantenerlo seleccionado en la web

        // LÓGICA DE BÚSQUEDA MULTICAMPO
        if (buscarTexto != null && !buscarTexto.trim().isEmpty()) {
            System.out.println("Cartero Web: Filtrando clientes por [" + criterio + "] con el texto: " + buscarTexto);
            java.util.List<Customer> resultadosFiltrados;

            // Evaluamos qué estantería del archivador consultar según el criterio seleccionado
            switch (criterio) {
                case "lastName":
                    resultadosFiltrados = customerService.searchByLastName(buscarTexto);
                    break;
                case "email":
                    resultadosFiltrados = customerService.searchByEmail(buscarTexto);
                    break;
                case "firstName":
                default:
                    resultadosFiltrados = customerService.searchByFirstName(buscarTexto);
                    break;
            }

            model.addAttribute("listaDeClientes", resultadosFiltrados);
            model.addAttribute("paginaActual", 0);
            model.addAttribute("totalPaginas", 1);
            model.addAttribute("textoBuscado", buscarTexto);
            return "customers";
        }

        // Si no hay texto en el buscador, se ejecuta la paginación tradicional
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

    // NUEVA ACCIÓN: Eliminar un cliente directamente haciendo clic en un enlace de la web
    // Se activa al pulsar el botón "Eliminar" en la tabla: http://localhost:8080/customers/delete/un-id
    @GetMapping("/customers/delete/{id}")
    public String deleteCustomerFromWeb(@org.springframework.web.bind.annotation.PathVariable String id) {
        System.out.println("Cartero Web: Procesando solicitud de baja visual para el código: " + id);

        // Ordenamos al servicio que retire la ficha del archivador
        customerService.deleteCustomer(id);

        // Redirigimos de vuelta a la lista actualizada
        return "redirect:/customers";
    }

    // NUEVA ACCIÓN: Abrir la pantalla con el formulario para modificar los datos de un cliente
    // Se activa al pulsar el botón "Editar": http://localhost:8080/customers/edit/un-id
    @GetMapping("/customers/edit/{id}")
    public String showEditPage(@org.springframework.web.bind.annotation.PathVariable String id, Model model) {
        System.out.println("Cartero Web: Abriendo la pantalla de edición para el código: " + id);

        // Localizamos los datos actuales del cliente para meterlos en la maleta
        Customer clienteExistente = customerService.getCustomerById(id);
        model.addAttribute("clienteAEditar", clienteExistente);

        // Viajamos a la nueva pantalla de edición
        return "edit-customer";
    }

}
