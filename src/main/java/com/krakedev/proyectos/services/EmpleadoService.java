package com.krakedev.proyectos.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.repositories.EmpleadoRepository;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    // Guardar empleado
    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    // Listar todos los empleados
    public List<Empleado> listar() {
        return empleadoRepository.findAll();
    }

    // Buscar empleado por ID
    public Empleado buscarPorId(Integer id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Empleado no encontrado con ID: " + id));
    }

    // Actualizar empleado
    public Empleado actualizar(Integer id, Empleado empleado) {

        Empleado existente = buscarPorId(id);

        existente.setNombre(empleado.getNombre());
        existente.setCargo(empleado.getCargo());

        return empleadoRepository.save(existente);
    }

    // Eliminar empleado
    public void eliminar(Integer id) {
        Empleado existente = buscarPorId(id);
        empleadoRepository.delete(existente);
    }
}
