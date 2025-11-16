/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestiondecontacto.clases;

import java.util.ArrayList;




/**
 *
 * @author jd153
 */
public class Contacto {
    private int codigo;
    private String nombre;
    private String apellido;
    private ArrayList<Numero> numeros;

    public Contacto() {
        numeros = new ArrayList();
    }

    public Contacto(int codigo, String nombre, String apellido, ArrayList<Numero> numeros) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeros = numeros;
    }

    public Contacto(int codigo, String nombre, String apellido) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.apellido = apellido;
        numeros = new ArrayList();
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public ArrayList<Numero> getNumeros() {
        return numeros;
    }

    public void setNumeros(ArrayList<Numero> numeros) {
        this.numeros = numeros;
    }
    
}
