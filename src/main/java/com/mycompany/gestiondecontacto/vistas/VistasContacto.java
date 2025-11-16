/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.gestiondecontacto.vistas;

import com.mycompany.gestiondecontacto.clases.Contacto;
import com.mycompany.gestiondecontacto.clases.ContactoManager;
import com.mycompany.gestiondecontacto.clases.JsonManagerTipo;
import com.mycompany.gestiondecontacto.clases.Tipo;
import com.mycompany.gestiondecontacto.vistas.VistaTipo;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.mycompany.gestiondecontacto.vistas.VistaModificar;
import java.util.List;
import javax.swing.SwingWorker;
import javax.swing.JFileChooser;

public class VistasContacto extends javax.swing.JFrame {

    /**
     * Creates new form VistasContacto
     */
    private final ContactoManager contactoManager = new ContactoManager();
    private final JsonManagerTipo tipoManager = new JsonManagerTipo();
    DefaultTableModel modelo;
    private ArrayList<Tipo> tipos;
    static int codigo = 1;

    public void forzaGuardadoContacto() {
        this.contactoManager.guardarAlSalir();
    }

    public ImageIcon scaleImage(ImageIcon icon, int w, int h) {
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    public void mostrarDatos() {
        new CargadorContactosWorker().execute();
    }

    public void actualizarTablaUI(ArrayList<Contacto> contactos) {
        // Limpiar filas existentes antes de rellenar
        modelo.setNumRows(0);

        for (Contacto contacto : contactos) {
            String numerosStr;
            try {

                numerosStr = String.valueOf(contacto.getNumeros().size());

            } catch (NullPointerException e) {
                // Manejo de caso si getNumeros() devuelve null.
                numerosStr = "0";
            }

            modelo.addRow(new Object[]{
                contacto.getCodigo(),
                contacto.getNombre(),
                contacto.getApellido(),
                numerosStr
            });
        }
    }

    private class CargadorContactosWorker extends SwingWorker<ArrayList<Contacto>, Void> {

        // Se ejecuta en el HILO DE TRABAJO (Background Thread)
        @Override
        protected ArrayList<Contacto> doInBackground() throws Exception {
            // Operación de I/O pesada (cargar contactos)
            // Simulación de una carga lenta
            Thread.sleep(50);
            return (ArrayList<Contacto>) contactoManager.getListaContactos();
        }

        // Se ejecuta en el HILO DE DESPACHO DE EVENTOS (EDT - UI Thread)
        @Override
        protected void done() {
            try {
                ArrayList<Contacto> contactos = get(); // Obtiene el resultado de doInBackground
                actualizarTablaUI(contactos); // Actualiza la UI de forma segura
            } catch (Exception e) {
                JOptionPane.showMessageDialog(VistasContacto.this,
                        "Error al cargar contactos: " + e.getMessage(),
                        "Error de Carga",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class GuardarContactoWorker extends SwingWorker<Boolean, Void> {

        private final String nombre;
        private final String apellido;
        private String validationMessage = null;

        public GuardarContactoWorker(String nombre, String apellido) {
            this.nombre = nombre;
            this.apellido = apellido;
            // Deshabilita el botón al iniciar la tarea
            button1.setEnabled(false);
        }

        // Se ejecuta en el HILO DE TRABAJO (Background Thread)
        @Override
        protected Boolean doInBackground() throws Exception {
            if (contactoManager.contactoExiste(nombre, apellido)) {
                validationMessage = "El contacto " + nombre + " " + apellido + " ya existe.";
                return false; // Retorna falso si el contacto ya existe
            }

            // [Requisito 1: Implementar Thread.sleep(500) para simular trabajo I/O]
            Thread.sleep(500);

            // [Requisito 5: Sincronización en modificación]
            synchronized (contactoManager) {
                Contacto c = new Contacto();
                // OJO: Usar 'codigo++' es thread-safe si solo un hilo lo hace.
                // Como el GuardarWorker es el único que lo usa, se mantiene.
                c.setCodigo(codigo++);
                c.setNombre(nombre);
                c.setApellido(apellido);
                contactoManager.agregarContacto(c);
                contactoManager.guardarAlSalir();
            }

            return true;
        }

        // Se ejecuta en el HILO DE DESPACHO DE EVENTOS (EDT - UI Thread)
        @Override
        protected void done() {
            // [Requisito 4: Notificaciones en la Interfaz Gráfica]
            try {
                if (get()) {
                    // Éxito
                    tfNombre.setText("");
                    tfApellido.setText("");
                    mostrarDatos();

                    JOptionPane.showMessageDialog(VistasContacto.this,
                            "Contacto guardado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Error de validación (si se implementara la lógica de validationMessage)
                    if (validationMessage != null) {
                        JOptionPane.showMessageDialog(VistasContacto.this,
                                validationMessage,
                                "Error de Validación",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(VistasContacto.this,
                        "Error al guardar: " + e.getMessage(),
                        "Error de Guardado",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                button1.setEnabled(true); // Re-habilita el botón de guardar
            }
        }
    }

    // =========================================================
    // IMPLEMENTACIÓN SWINGWORKER (Eliminación Concurrente)
    // =========================================================
    private class EliminarContactoWorker extends SwingWorker<Boolean, Void> {

        private final int indiceLista; // Es más seguro usar el índice de la lista interna.

        public EliminarContactoWorker(int indiceLista) {
            this.indiceLista = indiceLista;
            button4.setEnabled(false); // Deshabilita el botón al iniciar la tarea
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            Thread.sleep(200); // Simulación de I/O

            synchronized (contactoManager) {
                // Solo se elimina el contacto si el índice es válido
                ArrayList<Contacto> contactos = (ArrayList<Contacto>) contactoManager.getListaContactos();
                if (indiceLista >= 0 && indiceLista < contactos.size()) {
                    contactos.remove(indiceLista);
                    contactoManager.guardarAlSalir();
                    return true;
                }
                return false;
            }
        }

        @Override
        protected void done() {
            try {
                if (get()) {
                    // **CORRECCIÓN DE RECARGA:** Llama a mostrarDatos(), 
                    // que inicia un CargadorContactosWorker, para recargar 
                    // de forma concurrente y segura.
                    mostrarDatos();

                    JOptionPane.showMessageDialog(VistasContacto.this,
                            "Contacto eliminado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(VistasContacto.this,
                        "Error al eliminar: " + e.getMessage(),
                        "Error de Eliminación",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                button4.setEnabled(true);
            }
        }
    }
    // =========================================================
// IMPLEMENTACIÓN SWINGWORKER (Búsqueda Concurrente)
// =========================================================

    private class BuscadorContactosWorker extends SwingWorker<List<Contacto>, Void> {

        private final String criterioBusqueda;

        public BuscadorContactosWorker(String criterioBusqueda) {
            this.criterioBusqueda = criterioBusqueda;
            // Opcional: Deshabilitar o mostrar indicador de carga si lo tiene
        }

        @Override
        protected List<Contacto> doInBackground() throws Exception {
            Thread.sleep(100); // Simulación para mostrar la concurrencia/fluidez
            // Llama al método de ContactoManager implementado previamente
            return contactoManager.buscarContactos(criterioBusqueda);
        }

        @Override
        protected void done() {
            try {
                List<Contacto> resultados = get();

                // Llama al método para actualizar la tabla con los resultados (debe ser implementado)
                actualizarTablaUI((ArrayList<Contacto>) resultados);

                // Opcional: Notificación de resultados
                // labelResultados.setText("Encontrados: " + resultados.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(VistasContacto.this,
                        "Error durante la búsqueda: " + e.getMessage(),
                        "Error de Búsqueda",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public VistasContacto() {

        // 1. Cargar tipos al inicio (se hace al inicio del constructor)
        ArrayList<Tipo> tiposCargados = (ArrayList<Tipo>) tipoManager.cargarTipos();
        if (tiposCargados == null) {
            this.tipos = new ArrayList<>();
        } else {
            this.tipos = tiposCargados;
        }

        initComponents();

        // 2. Inicializar el DefaultTableModel y sus columnas
        this.modelo = new DefaultTableModel();
        this.modelo.addColumn("CODE");
        this.modelo.addColumn("NAME");
        this.modelo.addColumn("LAST NAME");
        // ¡Se debe agregar la columna de números para que el modelo coincida con mostrarDatos()!
        this.modelo.addColumn("NUMERB PHONE");

        // 3. Asignar el modelo a la JTable
        jtDatos.setModel(this.modelo);

        // 4. Cargar y mostrar los datos. ¡Ahora es seguro llamarlo!
        mostrarDatos();

        // 5. Configurar el icono
        int width = 120;
        int height = 120;
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/img/libro-de-contactos.png"));

        if (originalIcon.getImage() != null) {
            ImageIcon scaledIcon = scaleImage(originalIcon, width, height);
            jLabel1.setIcon(scaledIcon);
            jLabel1.setText("");
        }

    }

    private class ExportarContactosWorker extends SwingWorker<Boolean, Void> {

        private final String rutaArchivo;

        public ExportarContactosWorker(String rutaArchivo) {
            this.rutaArchivo = rutaArchivo;
            // Opcional: Deshabilita el botón de exportar (si es necesario)
            jButton1.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            Thread.sleep(1000); // Simulación de exportación pesada

            // 🔒 Requisito: Usar synchronized para garantizar la seguridad de los datos 
            synchronized (contactoManager) {
                // 💾 Se llama al método I/O de ContactoManager
                contactoManager.exportarDatos(rutaArchivo);
            }
            return true;
        }

        // Se ejecuta en el HILO DE DESPACHO DE EVENTOS (EDT - UI Thread)
        @Override
        protected void done() {
            // [Requisito: Notificaciones en la interfaz gráfica] 
            try {
                if (get()) {
                    JOptionPane.showMessageDialog(VistasContacto.this,
                            "✅ Datos exportados correctamente a: " + rutaArchivo,
                            "Éxito en Exportación",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(VistasContacto.this,
                        "❌ Error al exportar: " + e.getMessage(),
                        "Error de Exportación",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                // Re-habilita el botón de exportar
                jButton1.setEnabled(true);
            }
        }
    }

    private void tfBuscarKeyReleased(java.awt.event.KeyEvent evt) {
        String criterio = tfBuscar.getText().trim();

        if (criterio.isEmpty()) {
            // Si no hay texto, recarga la tabla completa de forma segura
            mostrarDatos();
        } else {
            // Inicia la búsqueda concurrente [cite: 29]
            new BuscadorContactosWorker(criterio).execute();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        label3 = new java.awt.Label();
        tfNombre = new java.awt.TextField();
        tfApellido = new java.awt.TextField();
        button1 = new java.awt.Button();
        button2 = new java.awt.Button();
        button3 = new java.awt.Button();
        button4 = new java.awt.Button();
        button5 = new java.awt.Button();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtDatos = new javax.swing.JTable();
        button6 = new java.awt.Button();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        tfBuscar = new java.awt.TextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        label1.setText("AGENDA");
        getContentPane().add(label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 0, 105, 54));

        label2.setText("LAST NAME:");
        getContentPane().add(label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 210, 70, 29));

        label3.setText("NAME:");
        getContentPane().add(label3, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 160, 70, 29));
        getContentPane().add(tfNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 169, 321, -1));
        getContentPane().add(tfApellido, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 219, 321, -1));

        button1.setLabel("SAVE");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });
        getContentPane().add(button1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 380, 86, 37));

        button2.setLabel("ADD TYPE");
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });
        getContentPane().add(button2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 330, 97, 37));

        button3.setLabel("MODIFY");
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });
        getContentPane().add(button3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 380, 86, 37));

        button4.setLabel("ELIMINATE");
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });
        getContentPane().add(button4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 380, 86, 37));

        button5.setLabel("CANCEL");
        button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button5ActionPerformed(evt);
            }
        });
        getContentPane().add(button5, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 650, 86, 37));

        jtDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jtDatos);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 440, -1, 182));

        button6.setLabel("NUMBERS");
        button6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button6ActionPerformed(evt);
            }
        });
        getContentPane().add(button6, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 380, 88, 37));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/libro-de-contactos.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(222, 64, 113, 95));

        jButton1.setBackground(new java.awt.Color(204, 204, 204));
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("EXPORT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 650, 110, 30));

        tfBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfBuscarActionPerformed(evt);
            }
        });
        getContentPane().add(tfBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 270, 320, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //este es el boton numero 3
    //este es para modificar 
    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        // TODO add your handling code here:

        if (jtDatos.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "SELECIONA UN REGISTRO QUE DESEA MODIFICAR");
        } else {
            Contacto contactoSeleccionado = contactoManager.getListaContactos().get(jtDatos.getSelectedRow());
            VistaModificar vm = new VistaModificar(contactoSeleccionado, this);
            vm.setVisible(true);
        }
    }//GEN-LAST:event_button3ActionPerformed
    // este es el botn numero 4
    //este para eliminar
    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
        // TODO add your handling code here:
        int filaSeleccionada = jtDatos.getSelectedRow();

        // 2. Realizar la validación de UI (rápido - en el EDT)
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(null, "SELECIONE EL REGISTRO PARA ELIMINAR");
        } else {
            // 3. INICIAR LA TAREA CONCURRENTE
            // Ejecuta el Worker para realizar la eliminación y el guardado (I/O)
            new EliminarContactoWorker(filaSeleccionada).execute();

            // La recarga de la tabla y las notificaciones se manejarán dentro del done() del Worker.
        }
    }//GEN-LAST:event_button4ActionPerformed
    // este es el botn numero 6 
    //este es para numero 
    private void button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button5ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button5ActionPerformed
    // este es el boton numero 1
    //este es para guardar
    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        // TODO add your handling code here:

        // 1. Obtener los datos del formulario (rápido - en el EDT)
        String nombre = tfNombre.getText();
        String apellido = tfApellido.getText();

        // 2. Realizar la validación de UI (rápido - en el EDT)
        if (!nombre.equals("") && !apellido.equals("")) {

            // 3. INICIAR LA TAREA CONCURRENTE
            // Crea y ejecuta el Worker, delegando el guardado (I/O) y la validación
            new GuardarContactoWorker(nombre, apellido).execute();

            // IMPORTANTE: NO se limpia la UI (tfNombre.setText("")) ni se llama a mostrarDatos() aquí.
            // Esas acciones se realizan DENTRO del método done() del Worker si la operación es exitosa,
            // asegurando que solo se actualice la UI después de que la tarea pesada haya terminado.
        } else {
            // Notificación de error de formulario (rápido - en el EDT)
            JOptionPane.showMessageDialog(null, "INGRESE LOS DATOS");
        }

    }//GEN-LAST:event_button1ActionPerformed
    //este es botn numero 2 
    //este es para gregar tipo
    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        // TODO add your handling code here:

        VistaTipo vt = new VistaTipo(this.tipos, this);
        vt.setVisible(true);
        this.setVisible(false);


    }//GEN-LAST:event_button2ActionPerformed
    //este es ell boton 6
    //este dirige a numero 
    private void button6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button6ActionPerformed
        // TODO add your handling code here:

        if (jtDatos.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "SELECIONA EL REGISTRO");
        } else {
            // 2. Obtener el objeto Contacto seleccionado usando el índice de la tabla.
            Contacto contactoSeleccionado = contactoManager.getListaContactos().get(jtDatos.getSelectedRow());

            VistaNumeros vn = new VistaNumeros(contactoSeleccionado, this, this.tipos);
            vn.setVisible(true);
            this.setVisible(false);
        }
    }//GEN-LAST:event_button6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar ubicación para exportar contactos");

        // Sugerir el nombre de archivo con la extensión CSV
        fileChooser.setSelectedFile(new java.io.File("Contactos_Exportados.csv"));

        // Mostrar el diálogo de guardar
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // Obtener la ruta completa seleccionada
            String rutaDeExportacion = fileChooser.getSelectedFile().getAbsolutePath();

            // 2. Ejecutar el Worker en un hilo de fondo (Requisito: Exportación con hilos múltiples )
            new ExportarContactosWorker(rutaDeExportacion).execute();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tfBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfBuscarActionPerformed
        // TODO add your handling code here
        String criterio = tfBuscar.getText().trim();

        if (criterio.isEmpty()) {
            mostrarDatos(); // Recarga toda la tabla
        } else {
            // Inicia la Búsqueda Concurrente (Requisito de Unidad 3)
            new BuscadorContactosWorker(criterio).execute();
        }
    }//GEN-LAST:event_tfBuscarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VistasContacto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VistasContacto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VistasContacto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VistasContacto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VistasContacto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    private java.awt.Button button2;
    private java.awt.Button button3;
    private java.awt.Button button4;
    private java.awt.Button button5;
    private java.awt.Button button6;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtDatos;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.TextField tfApellido;
    private java.awt.TextField tfBuscar;
    private java.awt.TextField tfNombre;
    // End of variables declaration//GEN-END:variables

    void setTipos(ArrayList<Tipo> tipos) {
        this.tipos = tipos;
    }
}
