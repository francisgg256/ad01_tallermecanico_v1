package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IVehiculos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Vehiculos implements IVehiculos {

    private static final String FICHERO_VEHICULOS = "datos/ficheros/json/vehiculos.json";
    private static Vehiculos instancia = null;
    private ObjectMapper mapper;

    private Vehiculos() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    static Vehiculos getInstancia() {
        if (instancia == null) {
            instancia = new Vehiculos();
        }
        return instancia;
    }

    @Override
    public void comenzar() {}

    @Override
    public void terminar() {}

    private List<Vehiculo> leer() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        File fichero = new File(FICHERO_VEHICULOS);

        if (fichero.exists()) {
            try {
                Vehiculo[] array = mapper.readValue(fichero, Vehiculo[].class);
                vehiculos = new ArrayList<>(List.of(array));
            } catch (IOException e) {
                System.out.println("Error al leer vehículos: " + e.getMessage());
            }
        }
        return vehiculos;
    }

    private void escribir(List<Vehiculo> vehiculos) {
        File fichero = new File(FICHERO_VEHICULOS);
        fichero.getParentFile().mkdirs();

        try {
            mapper.writeValue(fichero, vehiculos);
        } catch (IOException e) {
            System.out.println("Error al escribir vehículos: " + e.getMessage());
        }
    }

    @Override
    public List<Vehiculo> get() {
        return new ArrayList<>(leer());
    }

    @Override
    public void insertar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        List<Vehiculo> vehiculos = leer();

        if (vehiculos.contains(vehiculo)) {
            throw new TallerMecanicoExcepcion("Ya existe un vehículo con esa matrícula.");
        }

        vehiculos.add(vehiculo);
        escribir(vehiculos);
    }

    @Override
    public Vehiculo buscar(Vehiculo vehiculo) {
        List<Vehiculo> vehiculos = leer();
        int indice = vehiculos.indexOf(vehiculo);
        return (indice != -1) ? vehiculos.get(indice) : null;
    }

    @Override
    public void borrar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        List<Vehiculo> vehiculos = leer();

        if (!vehiculos.remove(vehiculo)) {
            throw new TallerMecanicoExcepcion("No existe ningún vehículo con esa matrícula.");
        }

        escribir(vehiculos);
    }
}

