package com.example.demo.service;

import com.example.demo.model.RoadMap;
import com.example.demo.repository.RoadMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

// Esta clase funciona como el "Coordinador de Estudios".
// Se encarga de gestionar los mapas de aprendizaje solicitados por la web o el menú.
@Service
public class RoadMapService {

    // Conectamos automáticamente el archivador de mapas de ruta que acabamos de crear
    @Autowired
    private RoadMapRepository roadMapRepository;

    // Acción: Registrar una nueva guía de estudio en el almacén.
    public RoadMap createRoadMap(RoadMap roadMap) {
        RoadMap savedMap = roadMapRepository.save(roadMap);
        // Eliminamos el .getTitle() para que imprima la ficha completa de forma segura
        System.out.println("Coordinador: Nueva hoja de ruta registrada -> " + savedMap);

        return savedMap;
    }

    // Acción: Recuperar el catálogo completo de hojas de ruta disponibles.
    public List<RoadMap> findAll() {
        return roadMapRepository.findAll();
    }

    // Acción: Localizar una guía específica usando su código identificador.
    public RoadMap getRoadMapById(String id) {
        return roadMapRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró ningún mapa de estudio con el código: " + id));
    }

    // Acción: Contabilizar cuántas guías tenemos registradas en total.
    public long countRoadMaps() {
        return roadMapRepository.count();
    }

    // Acción: Guardar un paquete completo de hojas de ruta (Útil para el semillero inicial).
    public void saveAll(List<RoadMap> roadMaps) {
        roadMapRepository.saveAll(roadMaps);
    }

    // Acción: Vaciar por completo el archivador de mapas de estudio.
    public void deleteAll() {
        roadMapRepository.deleteAll();
    }
}
