package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IClientes;

public class Clientes implements IClientes {

    private static final String FICHERO_CLIENTES = "datos/ficheros/json/clientes.json";
    private static Clientes instancia = null;
    private ObjectMapper mapper;

    private Clientes() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static Clientes getInstancia() {
        if (instancia == null) {
            instancia = new Clientes();
        }
        return instancia;
    }

    @Override
    public void comenzar() {}

    @Override
    public void terminar() {}

    private List<Cliente> leer() {
        List<Cliente> clientes = new ArrayList<>();
        File fichero = new File(FICHERO_CLIENTES);

        if (fichero.exists()) {
            try {
                Cliente[] array = mapper.readValue(fichero, Cliente[].class);
                clientes = new ArrayList<>(List.of(array));
            } catch (IOException e) {
                System.out.println("Error al leer clientes: " + e.getMessage());
            }
        }
        return clientes;
    }

    private void escribir(List<Cliente> clientes) {
        File fichero = new File(FICHERO_CLIENTES);
        fichero.getParentFile().mkdirs();

        try {
            mapper.writeValue(fichero, clientes);
        } catch (IOException e) {
            System.out.println("Error al escribir clientes: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> get() {
        return new ArrayList<>(leer());
    }

    @Override
    public void insertar(Cliente cliente) throws TallerMecanicoExcepcion {
        List<Cliente> clientes = leer();

        if (clientes.contains(cliente)) {
            throw new TallerMecanicoExcepcion("Ya existe un cliente con ese DNI.");
        }

        clientes.add(cliente);
        escribir(clientes);
    }

    @Override
    public Cliente modificar(Cliente cliente, String nombre, String telefono) throws TallerMecanicoExcepcion {
        List<Cliente> clientes = leer();
        int indice = clientes.indexOf(cliente);

        if (indice == -1) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }

        Cliente clienteModificar = clientes.get(indice);

        if (nombre != null && !nombre.isBlank()) {
            clienteModificar.setNombre(nombre);
        }
        if (telefono != null && !telefono.isBlank()) {
            clienteModificar.setTelefono(telefono);
        }

        escribir(clientes);

        return new Cliente(clienteModificar);
    }


    @Override
    public Cliente buscar(Cliente cliente) {
        List<Cliente> clientes = leer();
        int indice = clientes.indexOf(cliente);
        return (indice != -1) ? new Cliente(clientes.get(indice)) : null;
    }

    @Override
    public void borrar(Cliente cliente) throws TallerMecanicoExcepcion {
        List<Cliente> clientes = leer();

        if (!clientes.remove(cliente)) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }

        escribir(clientes);
    }
}

