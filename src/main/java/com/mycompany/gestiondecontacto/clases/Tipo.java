/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestiondecontacto.clases;

public class Tipo {
    private int codigo;
    private String nombre;
    
    public Tipo(){
    }
    public Tipo(int codigo, String nombre){
          this.codigo = codigo;
          this.nombre = nombre;
    }
    
    public int getCodigo(){
    return this.codigo;
    }
    
    public String getNombre(){
    return this.nombre;
    }
    
    public void setCodigo(int codigo){
       this.codigo = codigo;
    }
    
    public void setNombre(String nombre){
    this.nombre = nombre;
    }
}
