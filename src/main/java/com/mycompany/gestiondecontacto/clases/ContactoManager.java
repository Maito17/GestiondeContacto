/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestiondecontacto.clases;

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author jd153
 */
public class ContactoManager {
    private List<Contacto> listaContactosList;
    private final JsonManager jsonManager;
    
    public ContactoManager(){
        this.jsonManager = new JsonManager();
        this.listaContactosList = jsonManager.cargarDatos();
    }
    
    public void agregarContacto(Contacto nuevo){
        
        listaContactosList.add(nuevo);
        jsonManager.guardarDatos(listaContactosList); 
    }
    
    public boolean contactoExiste(String nombre, String apellido) {
        // Normaliza los criterios a minúsculas para una comparación no sensible a mayúsculas
        String nombreLower = nombre.toLowerCase();
        String apellidoLower = apellido.toLowerCase();

        for (Contacto c : listaContactosList) {
            if (c.getNombre().toLowerCase().equals(nombreLower) && 
                c.getApellido().toLowerCase().equals(apellidoLower)) {
                return true;
            }
        }
        return false;
    }
    
    public List<Contacto> buscarContactos(String criterio) {
        String criterioLower = criterio.toLowerCase();
        List<Contacto> resultados = new ArrayList<>();
        
        // Se itera sobre la lista y se filtran los resultados
        for (Contacto c : listaContactosList) {
            if (c.getNombre().toLowerCase().contains(criterioLower) ||
                c.getApellido().toLowerCase().contains(criterioLower)) {
                resultados.add(c);
            }
        }
        return resultados;
    }
    
    
    public void exportarDatos(String rutaArchivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            
            // Escribir encabezados (simulación, asumiendo estructura de Contacto)
            writer.println("CODIGO,NOMBRE,APELLIDO,NUMEROS_TOTALES"); 
            
            for (Contacto c : listaContactosList) {
                // Obtener el número total de teléfonos. Manejo de NullPointerException.
                int numTelefonos = c.getNumeros() != null ? c.getNumeros().size() : 0;
                
                // Escribir la línea de datos
                writer.printf("%d,%s,%s,%d\n", 
                        c.getCodigo(), 
                        c.getNombre(), 
                        c.getApellido(), 
                        numTelefonos);
            }
        } catch (IOException e) {
            // Se lanza la excepción para que sea manejada por el SwingWorker.done()
            throw new IOException("Error al escribir el archivo: " + e.getMessage(), e);
        }
    }

    public List<Contacto> getListaContactos(){
        return listaContactosList;
    }
    
    public void guardarAlSalir(){
        jsonManager.guardarDatos(listaContactosList);
    }
}
