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
            @RequestParam(required = false) String buscarTexto,
            @RequestParam(defaultValue = "firstName") String criterio,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion,
            Model model) {

        model.addAttribute("nuevoCliente", new Customer());
        model.addAttribute("criterioSeleccionado", criterio);
        model.addAttribute("textoBuscado", buscarTexto != null ? buscarTexto : "");
        model.addAttribute("columnaActual", ordenarPor);
        model.addAttribute("siguienteDireccion", direccion.equalsIgnoreCase("asc") ? "desc" : "asc");

        // 1. Configuración del motor de ordenación nativa de Spring Data
        org.springframework.data.domain.Sort.Direction sentido = direccion.equalsIgnoreCase("desc") ?
                org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC;
        org.springframework.data.domain.Sort reglaOrdenacion = org.springframework.data.domain.Sort.by(sentido, ordenarPor);

        // 2. LÓGICA DE BÚSQUEDA MULTICAMPO (Con ordenación integrada)
        if (buscarTexto != null && !buscarTexto.trim().isEmpty()) {
            System.out.println("Cartero Web: Filtrando y Ordenando por [" + criterio + "] con el texto: " + buscarTexto);
            java.util.List<Customer> resultadosFiltrados;

            switch (criterio) {
                case "lastName":
                    resultadosFiltrados = customerService.searchByLastName(buscarTexto, reglaOrdenacion);
                    break;
                case "email":
                    resultadosFiltrados = customerService.searchByEmail(buscarTexto, reglaOrdenacion);
                    break;
                case "firstName":
                default:
                    resultadosFiltrados = customerService.searchByFirstName(buscarTexto, reglaOrdenacion);
                    break;
            }

            model.addAttribute("listaDeClientes", resultadosFiltrados);
            model.addAttribute("paginaActual", 0);
            model.addAttribute("totalPaginas", 1);
            return "customers";
        }

        // 3. Si no hay búsqueda, se ejecuta la paginación tradicional con ordenación
        Pageable paginaSolicitada = PageRequest.of(page, size, reglaOrdenacion);
        Page<Customer> paginaDeClientes = customerService.findAllPaginated(paginaSolicitada);

        model.addAttribute("listaDeClientes", paginaDeClientes.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaDeClientes.getTotalPages());

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
