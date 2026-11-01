package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.repositories.ProyectoRepository;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    // Guardar proyecto
    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    // Listar todos los proyectos
    public List<Proyecto> listar() {
        return proyectoRepository.findAll();
    }

    // Buscar proyecto por ID
    public Proyecto buscarPorId(Integer id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Proyecto no encontrado con ID: " + id));
    }

    // Actualizar proyecto
    public Proyecto actualizar(Integer id, Proyecto proyecto) {

        Proyecto existente = buscarPorId(id);

        existente.setNombre(proyecto.getNombre());
        existente.setDescripcion(proyecto.getDescripcion());
        existente.setFechaInicio(proyecto.getFechaInicio());

        return proyectoRepository.save(existente);
    }

    // Eliminar proyecto
    public void eliminar(Integer id) {
        Proyecto existente = buscarPorId(id);
        proyectoRepository.delete(existente);
    }
}
