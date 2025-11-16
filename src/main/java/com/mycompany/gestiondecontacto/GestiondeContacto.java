/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.gestiondecontacto;

import javax.swing.SwingUtilities;
import com.mycompany.gestiondecontacto.loginGC;

/**
 *
 * @author jd153
 */
public class GestiondeContacto {

    public static void main(String[] args) {
        
        // Ejecutar la interfaz gráfica en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Crea y muestra tu ventana de Login
                new loginGC().setVisible(true);
            }
        });
    }
}
