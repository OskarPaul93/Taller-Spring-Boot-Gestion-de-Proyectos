package com.krakedev.proyectos.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.repositories.EmpleadoRepository;
import com.krakedev.proyectos.repositories.ProyectoRepository;
import com.krakedev.proyectos.services.TareaService;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService tareaService;
    private final ProyectoRepository proyectoRepository;
    private final EmpleadoRepository empleadoRepository;

    public TareaController(
            TareaService tareaService,
            ProyectoRepository proyectoRepository,
            EmpleadoRepository empleadoRepository) {

        this.tareaService = tareaService;
        this.proyectoRepository = proyectoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    // Registrar tarea con proyecto y empleados existentes
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Tarea tarea) {
        try {

            // Buscar el proyecto existente
            if (tarea.getProyecto() == null) {
                throw new RuntimeException("Debe indicar un proyecto");
            }

            Integer proyectoId = tarea.getProyecto().getId();

            Proyecto proyecto = proyectoRepository.findById(proyectoId)
                    .orElseThrow(() -> new RuntimeException(
                            "Proyecto no encontrado con ID: " + proyectoId));

            tarea.setProyecto(proyecto);

            // Buscar los empleados existentes
            List<Empleado> empleados = new ArrayList<>();

            if (tarea.getEmpleados() != null) {

                for (Empleado empleado : tarea.getEmpleados()) {

                    Integer empleadoId = empleado.getId();

                    Empleado empleadoExistente =
                            empleadoRepository.findById(empleadoId)
                            .orElseThrow(() -> new RuntimeException(
                                    "Empleado no encontrado con ID: " + empleadoId));

                    empleados.add(empleadoExistente);
                }
            }

            tarea.setEmpleados(empleados);

            // Guardar tarea
            Tarea respuesta = tareaService.guardar(tarea);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar tarea: " + e.getMessage());
        }
    }

    // Listar todas las tareas
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<Tarea> tareas = tareaService.listar();
            return ResponseEntity.ok(tareas);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al listar tareas: " + e.getMessage());
        }
    }
}
