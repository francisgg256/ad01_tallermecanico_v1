package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.TipoTrabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Trabajo;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.ITrabajos;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.*;
import java.util.HashMap;

public class Trabajos implements ITrabajos {

    private static final String FICHERO_TRABAJOS = "datos/ficheros/json/trabajos.json";
    private static Trabajos instancia = null;
    private ObjectMapper mapper;

    private Trabajos() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    static Trabajos getInstancia() {
        if (instancia == null) {
            instancia = new Trabajos();
        }
        return instancia;
    }

    @Override
    public void comenzar() {}

    @Override
    public void terminar() {}

    private List<Trabajo> leer() {
        List<Trabajo> trabajos = new ArrayList<>();
        File fichero = new File(FICHERO_TRABAJOS);

        if (fichero.exists()) {
            try {
                Trabajo[] array = mapper.readValue(fichero, Trabajo[].class);
                trabajos = new ArrayList<>(List.of(array));
            } catch (IOException e) {
                System.out.println("Error al leer trabajos: " + e.getMessage());
            }
        }
        return trabajos;
    }

    private void escribir(List<Trabajo> trabajos) {
        File fichero = new File(FICHERO_TRABAJOS);
        fichero.getParentFile().mkdirs();

        try {
            mapper.writeValue(fichero, trabajos);
        } catch (IOException e) {
            System.out.println("Error al escribir trabajos: " + e.getMessage());
        }
    }

    @Override
    public List<Trabajo> get() {
        return new ArrayList<>(leer());
    }

    @Override
    public List<Trabajo> get(Cliente cliente) {
        List<Trabajo> trabajos = leer();
        List<Trabajo> trabajosCliente = new ArrayList<>();

        for (Trabajo trabajo : trabajos) {
            if (trabajo.getCliente().equals(cliente)) {
                trabajosCliente.add(trabajo);
            }
        }

        return trabajosCliente;
    }

    @Override
    public List<Trabajo> get(Vehiculo vehiculo) {
        List<Trabajo> trabajos = leer();
        List<Trabajo> trabajosVehiculo = new ArrayList<>();

        for (Trabajo trabajo : trabajos) {
            if (trabajo.getVehiculo().equals(vehiculo)) {
                trabajosVehiculo.add(trabajo);
            }
        }

        return trabajosVehiculo;
    }

    @Override
    public Map<TipoTrabajo, Integer> getEstadisticasMensuales(LocalDate mes) {
        List<Trabajo> trabajos = leer();
        Map<TipoTrabajo, Integer> estadisticas = new HashMap<>();

        estadisticas.put(TipoTrabajo.MECANICO, 0);
        estadisticas.put(TipoTrabajo.REVISION, 0);

        for (Trabajo trabajo : trabajos) {
            LocalDate fechaInicio = trabajo.getFechaInicio();

            if (fechaInicio.getYear() == mes.getYear() &&
                    fechaInicio.getMonth() == mes.getMonth()) {

                if (trabajo instanceof Mecanico) {
                    estadisticas.put(TipoTrabajo.MECANICO, estadisticas.get(TipoTrabajo.MECANICO) + 1);
                } else if (trabajo instanceof Revision) {
                    estadisticas.put(TipoTrabajo.REVISION, estadisticas.get(TipoTrabajo.REVISION) + 1);
                }
            }
        }

        return estadisticas;
    }

    @Override
    public void insertar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        List<Trabajo> trabajos = leer();

        if (trabajos.contains(trabajo)) {
            throw new TallerMecanicoExcepcion("Ya existe un trabajo igual.");
        }

        trabajos.add(trabajo);
        escribir(trabajos);
    }

    @Override
    public Trabajo anadirHoras(Trabajo trabajo, int horas) throws TallerMecanicoExcepcion {
        List<Trabajo> trabajos = leer();
        int indice = trabajos.indexOf(trabajo);

        if (indice == -1) {
            throw new TallerMecanicoExcepcion("No existe ese trabajo.");
        }

        Trabajo trabajoModificar = trabajos.get(indice);
        trabajoModificar.anadirHoras(horas);

        escribir(trabajos);

        return trabajoModificar;
    }

    @Override
    public Trabajo anadirPrecioMaterial(Trabajo trabajo, float precioMaterial) throws TallerMecanicoExcepcion {
        List<Trabajo> trabajos = leer();
        int indice = trabajos.indexOf(trabajo);

        if (indice == -1) {
            throw new TallerMecanicoExcepcion("No existe ese trabajo.");
        }

        Trabajo trabajoModificar = trabajos.get(indice);

        if (trabajoModificar instanceof Mecanico) {
            ((Mecanico) trabajoModificar).anadirPrecioMaterial(precioMaterial);
        } else {
            throw new TallerMecanicoExcepcion("Solo se puede añadir precio de material a trabajos mecánicos.");
        }

        escribir(trabajos);

        return trabajoModificar;
    }

    @Override
    public Trabajo cerrar(Trabajo trabajo, LocalDate fechaFin) throws TallerMecanicoExcepcion {
        List<Trabajo> trabajos = leer();
        int indice = trabajos.indexOf(trabajo);

        if (indice == -1) {
            throw new TallerMecanicoExcepcion("No existe ese trabajo.");
        }

        Trabajo trabajoCerrar = trabajos.get(indice);
        trabajoCerrar.cerrar(fechaFin);

        escribir(trabajos);

        return trabajoCerrar;
    }

    @Override
    public Trabajo buscar(Trabajo trabajo) {
        List<Trabajo> trabajos = leer();
        int indice = trabajos.indexOf(trabajo);
        return (indice != -1) ? trabajos.get(indice) : null;
    }

    @Override
    public void borrar(Trabajo trabajo) throws TallerMecanicoExcepcion {
        List<Trabajo> trabajos = leer();

        if (!trabajos.remove(trabajo)) {
            throw new TallerMecanicoExcepcion("No existe ese trabajo.");
        }

        escribir(trabajos);
    }
}


