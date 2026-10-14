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
}
