package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Clientes {
    private static final String FICHERO_CLIENTES = String.format("%s%s%s%s%s%s%s", "datos", File.separator, "ficheros", File.separator, "json", File.separator, "clientes.json");
    private static ObjectMapper mapper;
    private static Clientes instancia;

    private Clientes() {}

    static Clientes getInstancia() {
        if (instancia == null) {
            instancia = new Clientes();
        }
        return instancia;
    }

    public void comenzar() {}
    public void terminar() {}

    public Clientes[] leer() {
        List<Clientes> listaClientes = new ArrayList<>();
        try {
            mapper = new ObjectMapper();

            File jsonFile =new File("datos/ficheros/json/clientes.json");
            JsonNode rootNode = mapper.readTree(jsonFile);

            for (JsonNode node : rootNode) {
                String nombre = node.get("nombre").asText();
                int dni = node.get("dni").asInt();
                int telefono = node.get("telefono").asInt();


                Clientes clientes = new Clientes();
                listaClientes.add(clientes);
                return listaClientes.toArray(new Clientes[0]);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return listaClientes.toArray(new Clientes[0]);
    }

    public void escribir(Clientes [] clientes) {

    }
}
