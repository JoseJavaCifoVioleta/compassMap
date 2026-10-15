package com.example.demo.repository;

import com.example.demo.model.RoadMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Esta interfaz actúa como nuestro "archivador inteligente de hojas de ruta".
// Spring Boot se encarga en segundo plano de crear los métodos para guardar,
// eliminar y buscar las guías de estudio sin que tengamos que escribir código de base de datos manual.
@Repository
public interface RoadMapRepository extends JpaRepository<RoadMap, String> {
    // Al igual que con los clientes, la llave única (ID) de cada mapa es un texto (String) generado por UUID.
}
