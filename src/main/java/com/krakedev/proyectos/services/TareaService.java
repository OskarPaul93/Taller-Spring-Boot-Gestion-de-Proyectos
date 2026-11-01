package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.repositories.TareaRepository;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;

    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    // Guardar tarea
    public Tarea guardar(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    // Listar todas las tareas
    public List<Tarea> listar() {
        return tareaRepository.findAll();
    }

    // Buscar tarea por ID
    public Tarea buscarPorId(Integer id) {
        return tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Tarea no encontrada con ID: " + id));
    }

    // Actualizar tarea
    public Tarea actualizar(Integer id, Tarea tarea) {

        Tarea existente = buscarPorId(id);

        existente.setDescripcion(tarea.getDescripcion());
        existente.setFechaLimite(tarea.getFechaLimite());
        existente.setCostoEstimado(tarea.getCostoEstimado());
        existente.setProyecto(tarea.getProyecto());
        existente.setEmpleados(tarea.getEmpleados());

        return tareaRepository.save(existente);
    }

    // Eliminar tarea
    public void eliminar(Integer id) {
        Tarea existente = buscarPorId(id);
        tareaRepository.delete(existente);
    }
}
