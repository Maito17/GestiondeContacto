# 📂 Proyecto: Gestión de Contactos (Programación Concurrente - Unidad 3)

Este proyecto corresponde a la implementación de la Programación Concurrente en la aplicación de Gestión de Contactos, 
desarrollada previamente en las Unidades 1 y 2. El objetivo principal fue optimizar el rendimiento y la fluidez de la
Interfaz Gráfica (UI) al delegar las operaciones pesadas (I/O, validaciones y búsquedas complejas) a hilos de ejecución en segundo plano.

---

## 👨‍💻 Autor y Contexto

* **Nombre:** Jonathan Davalos
* **Grupo:** 67
* **Asignatura:** Programación de Interfaces
* **Unidad:** 3 - Programación Concurrente
* **Tecnología:** Java (NetBeans, Maven, Swing/AWT, SwingWorker)

---

## ✨ Requisitos de Concurrencia Implementados

Se utilizaron **`SwingWorker`** y el concepto de **sincronización (`synchronized`)** para garantizar que la Interfaz Gráfica de Usuario 
(EDT - Event Dispatch Thread) permanezca sensible y no se congele durante la ejecución de tareas que consumen tiempo.

### 1. Validación de Contactos en Segundo Plano (Guardado)
* **Clase:** `GuardarContactoWorker`
* **Funcionalidad:** Implementación de un hilo independiente para verificar si un contacto ya existe (`contactoExiste()`) antes de enviarlo a guardar.
* **Optimización:** El proceso de guardado (que incluye I/O y validación) simula un retraso de 500ms (`Thread.sleep(500)`) y se ejecuta en el fondo para evitar bloqueos.

### 2. Búsqueda Dinámica y Concurrente
* **Clase:** `BuscadorContactosWorker`
* **Funcionalidad:** La búsqueda en el campo de texto se ejecuta en un hilo de trabajo (`KeyReleased` event) para filtrar la lista de contactos sin interrumpir la interacción del usuario.
* **Optimización:** Se asegura que la UI no se congele, cumpliendo el requisito de fluidez en grandes volúmenes de datos.

### 3. Exportación Concurrente de Contactos
* **Clase:** `ExportarContactosWorker`
* **Funcionalidad:** El proceso de exportación a archivo (`.csv` o similar) se realiza en un hilo de fondo.
* **Sincronización:** Se utilizó el bloque **`synchronized(contactoManager)`** en el método `doInBackground()`
*  para asegurar que la lista de contactos no sea modificada (seguridad de datos) mientras se realiza la operación de I/O de exportación.

### 4. Gestión de UI y Notificaciones
* Todos los Workers manejan la actualización de la tabla (`jtDatos`) y las notificaciones (`JOptionPane`) dentro del método **`done()`**,
garantizando que las modificaciones de la UI se realicen de manera segura en el **EDT**.

---

## 🚀 Estructura del Proyecto

Las principales clases modificadas para implementar la concurrencia fueron:

| Archivo | Rol en Concurrencia |
| :--- | :--- |
| **`VistasContacto.java`** | Contiene las clases internas `SwingWorker` (`Cargador`, `Guardar`, `Eliminar`, `Buscador`, `Exportar`). |
| **`ContactoManager.java`** | Contiene los métodos de acceso a datos (`contactoExiste()`, `buscarContactos()`, `guardarAlSalir()`, `exportarDatos()`) 
que están debidamente **sincronizados** para el acceso seguro entre hilos. |
| **`pom.xml`** | Configuración de dependencias (Maven
