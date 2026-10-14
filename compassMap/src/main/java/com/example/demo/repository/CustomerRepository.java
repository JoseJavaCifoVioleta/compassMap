package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Esta interfaz actúa como nuestro "archivador inteligente automático".
// Al extender 'JpaRepository', Spring Boot se encarga de fabricar en segundo plano
// todas las consultas necesarias (Guardar, Buscar, Modificar, Eliminar) sin que tengamos que escribir código SQL.
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // Aquí dentro no necesitamos declarar métodos manuales por ahora.
    // El sistema ya sabe que guardaremos fichas de tipo 'Customer' usando una 'String' (texto) como llave única.
    // El motor de Spring traducirá esto automáticamente en:
    // "Buscar clientes cuyo nombre contenga el texto introducido (ignorando mayúsculas y minúsculas)"

    // Añadimos el parámetro 'Sort' para que ordene los resultados del filtro sobre la marcha
    java.util.List<Customer> findByFirstNameContainingIgnoreCase(String firstName, org.springframework.data.domain.Sort sort);

    // El motor de Spring traducirá esto automáticamente para buscar por Apellido
    // Añadimos el parámetro 'Sort' para que ordene los resultados del filtro sobre la marcha
    java.util.List<Customer> findByLastNameContainingIgnoreCase(String lastName, org.springframework.data.domain.Sort sort);

    // El motor de Spring traducirá esto automáticamente para buscar por Correo Electrónico
    // Añadimos el parámetro 'Sort' para que ordene los resultados del filtro sobre la marcha
    java.util.List<Customer> findByEmailContainingIgnoreCase(String email, org.springframework.data.domain.Sort sort);

}
