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
public class JsonManagerTipo {
    private static final String FILENAME = "tipos.json"; // Archivo separado para tipos
    private final Gson gson;
    // Define el tipo como una lista de Tipo
    private final Type tipoListType = new TypeToken<ArrayList<Tipo>>() {}.getType();

    public JsonManagerTipo() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /** Guarda la lista de Tipos en un archivo JSON. */
    public void guardarTipos(List<Tipo> tipos) {
        try (FileWriter writer = new FileWriter(FILENAME)) {
            gson.toJson(tipos, writer);
            System.out.println("Tipos guardados exitosamente en: " + FILENAME);
        } catch (IOException e) {
            System.err.println("Error al guardar los Tipos: " + e.getMessage());
        }
    }

    /** Carga la lista de Tipos desde el archivo JSON. */
    public List<Tipo> cargarTipos() {
        try (FileReader reader = new FileReader(FILENAME)) {
            List<Tipo> tipos = gson.fromJson(reader, tipoListType);
            return (tipos != null) ? tipos : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Archivo de Tipos no encontrado. Iniciando con tipos vacíos.");
            return new ArrayList<>(); 
        }
    }
    
}
