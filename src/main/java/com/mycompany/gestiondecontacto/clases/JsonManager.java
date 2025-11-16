/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestiondecontacto.clases;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jd153
 */
public class JsonManager {

    private static final String FILENAME = "contactos.json";
    private final Gson gson;
    private final Type contactListType;

    public JsonManager() {
        // Inicializa Gson con formato legible (pretty printing)
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        // Define el tipo de dato que vamos a guardar/cargar: List<Contacto>
        this.contactListType = new TypeToken<ArrayList<Contacto>>() {
        }.getType();
    }

    /**
     * Guarda la lista de contactos en un archivo JSON.
     *
     * @param contactos La lista de Contacto a guardar.
     */
    public void guardarDatos(List<Contacto> contactos) {
        try (FileWriter writer = new FileWriter(FILENAME)) {
            // Convierte la lista de Java a una cadena JSON
            gson.toJson(contactos, writer);
            System.out.println("Datos guardados exitosamente en: " + FILENAME);
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    /**
     * Carga la lista de contactos desde el archivo JSON.
     *
     * @return Una lista de Contacto. Retorna una lista vacía si el archivo no
     * existe o hay un error.
     */
    public List<Contacto> cargarDatos() {
        try (FileReader reader = new FileReader(FILENAME)) {
            // Convierte la cadena JSON de vuelta a List<Contacto>
            List<Contacto> contactos = gson.fromJson(reader, contactListType);

            // Retorna la lista cargada, o una nueva lista si el archivo estaba vacío
            return (contactos != null) ? contactos : new ArrayList<>();
        } catch (IOException e) {
            // Esto es normal si el archivo no existe (primera ejecución)
            System.out.println("Archivo de datos no encontrado. Iniciando con lista vacía.");
            return new ArrayList<>();
        }
    }
}
