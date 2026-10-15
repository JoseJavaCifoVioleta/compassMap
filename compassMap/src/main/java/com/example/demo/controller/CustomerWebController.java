package com.example.demo.controller;

// CABECERAS DE CONTROL (Imports esenciales para que Java reconozca los componentes)
import com.example.demo.model.Customer;
import com.example.demo.model.RoadMap; // <-- ¡Dirección del mapa de ruta!
import com.example.demo.service.CustomerService;
import com.example.demo.service.RoadMapService; // <-- ¡Dirección del coordinador de rutas!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List; // <-- ¡Dirección de los almacenes List!
import java.util.ArrayList;

@Controller
public class CustomerWebController {

    @Autowired
    private CustomerService customerService;

    // Conectamos automáticamente el Coordinador de Estudios para tener acceso a los RoadMaps
    @Autowired
    private RoadMapService roadMapService;

    // Ruta de la página de inicio principal (La matriz interactiva de tu profesor)
    // Ruta de la página de inicio principal (La matriz interactiva de tu profesor)
    @GetMapping({"/", "/index"})
    public String showIndexPage(Model model) {
        System.out.println("Cartero Web: Cargando el catálogo de rutas para el tablero...");

        // Recuperamos las rutas del almacén a través del servicio
        List<RoadMap> listaRutas = roadMapService.findAll();
        List<String> listaNombresSeguros = new ArrayList<>();

        // Recorremos las rutas y guardamos su representación para que Thymeleaf pueda leerlas
        for (RoadMap r : listaRutas) {
            if (r != null) {
                // Usamos el reflejo básico de Java para obtener información o su descripción textual
                listaNombresSeguros.add(r.toString());
            }
        }

        // Metemos la lista de textos legibles en la maleta de viaje
        model.addAttribute("catalogoRutasTexto", listaNombresSeguros);

        return "index";
    }
    // Listar clientes de forma ordenada con paginación
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

        Sort.Direction sentido = direccion.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort reglaOrdenacion = Sort.by(sentido, ordenarPor);

        if (buscarTexto != null && !buscarTexto.trim().isEmpty()) {
            System.out.println("Cartero Web: Filtrando y Ordenando por [" + criterio + "]");
            List<Customer> resultadosFiltrados;

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

        Pageable paginaSolicitada = PageRequest.of(page, size, reglaOrdenacion);
        Page<Customer> paginaDeClientes = customerService.findAllPaginated(paginaSolicitada);

        model.addAttribute("listaDeClientes", paginaDeClientes.getContent());
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", paginaDeClientes.getTotalPages());

        return "customers";
    }

    @PostMapping("/customers/save")
    public String saveCustomerFromWeb(@ModelAttribute("nuevoCliente") Customer customer) {
        customerService.createCustomer(customer);
        return "redirect:/customers";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomerFromWeb(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String showEditPage(@PathVariable String id, Model model) {
        Customer clienteExistente = customerService.getCustomerById(id);
        model.addAttribute("clienteAEditar", clienteExistente);
        return "edit-customer";
    }
}
