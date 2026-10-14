package com.example.demo.service;

import com.example.demo.repository.CustomerRepository;
import com.example.demo.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    // we are using here DEPENDENCE INJECTION
    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer (Customer customer){
        // SET ID customer to UUID
        customer.setId(UUID.randomUUID().toString());

        // call repository to use SAVE operation to save data to h2 db
        Customer createdCustomer = customerRepository.save(customer);
        System.out.println("Service created customer: " +  createdCustomer);
        return createdCustomer;
    }


    // Modifica este método para que devuelva un List en lugar de un Iterable
    // Acción: Consultar el listado completo de clientes.
    public List<Customer> findAll() {
        // Al usar JpaRepository, findAll() ya devuelve directamente un List<Customer>.
        return customerRepository.findAll();
    }

    public void deleteCustomer (String id){

      customerRepository.deleteById(id);


    }

    public Customer getCustomerById(String id){

        Optional<Customer> foundCustomer = customerRepository.findById(id);

        return foundCustomer.get();
    }

    // Modifica la firma de este método cambiando ArrayList por List
    // Acción: Guardar un paquete masivo de clientes a la vez.
    public void saveAll(List<Customer> customers) {
        // Aseguramos que cada miembro de la lista tenga su identificador único antes de guardarlo.
        for (Customer c : customers) {
            if (c.getId() == null || c.getId().isEmpty()) {
                c.setId(UUID.randomUUID().toString());
            }
        }
        customerRepository.saveAll(customers);
    }

    public void deleteAllCustomers() {
        customerRepository.deleteAll();
    }

    public long countCustomers() {
        return customerRepository.count();
    }

    // Acción: Consultar un bloque o "página" específica de clientes (ej. de 10 en 10) (paginación).
    // Esto evita saturar la pantalla con 1000 registros de golpe.
    public org.springframework.data.domain.Page<Customer> findAllPaginated(org.springframework.data.domain.Pageable pageable) {
        // Le pide al archivador automático que solo traiga el pedazo de fichas solicitado.
        return customerRepository.findAll(pageable);
    }
}
