package com.aeropuerto.servicio;

/**
 * Interfaz de Extensibilidad Arquitectónica para nuevos módulos y algoritmos.
 * Permite incorporar futuras funcionalidades (nuevos tipos de árboles, optimizadores de rutas,
 * analítica de datos, pasarelas de pago o sincronización externa) sin alterar el código existente.
 */
public interface ModuloExtensible {

    /**
     * Nombre descriptivo del módulo.
     */
    String obtenerNombre();

    /**
     * Descripción técnica del módulo y algoritmos empleados.
     */
    String obtenerDescripcion();

    /**
     * Inicialización del módulo (carga de recursos o estructuras auxiliares).
     * @return true si se inicializó correctamente sin errores.
     */
    boolean inicializar();

    /**
     * Ejecución de una acción o comando dentro del módulo.
     * @param comando Identificador de la acción a ejecutar
     * @param parametros Argumentos opcionales
     * @return Objeto resultado o mensaje de estado
     */
    Object ejecutarAccion(String comando, Object... parametros);

    /**
     * Estado operativo actual del módulo (Activo, Pausado, Listo, Error).
     */
    String obtenerEstado();
}
