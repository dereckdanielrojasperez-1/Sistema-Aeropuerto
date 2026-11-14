package com.aeropuerto.estructuras;

/**
 * =========================================================================
 * ESPECIFICACIÓN FORMAL DEL TIPO ABSTRACTO DE DATOS: TAD COLA (FIFO CON PRIORIDAD)
 * =========================================================================
 * 
 * DOMINIO CONCEPTUAL:
 * Colección dinámica lineal donde los elementos se extraen por el extremo
 * "frente" y se insertan respetando la disciplina FIFO (First-In, First-Out)
 * o según niveles de prelación operacional (Cola con Prioridad).
 * Utilizada por la Torre de Control (ATC) de CORPAC S.A. para ordenar y
 * despachar aeronaves en pista de despegue (Runway 16L/34R).
 * 
 * NIVELES DE PRIORIDAD:
 *   - Prioridad 1: Máxima Prioridad / Emergencia Declarada (Squawk 7700, Auxilio Médico, Falla de Combustible).
 *   - Prioridad 2: Prioritaria / Vuelo Comercial con Conexión Crítica.
 *   - Prioridad 3: Regular / FIFO Estándar por orden cronológico de llegada a pista.
 * 
 * SINTAXIS (Signatura de Operaciones):
 * - Constructores:
 *     crearCola: () -> TADCola
 * - Observadores:
 *     frente (peek): TADCola -> Object
 *     estaVacia: TADCola -> boolean
 *     obtenerTamano: TADCola -> int
 *     listar: TADCola -> String
 * - Modificadores:
 *     encolar (enqueue regular): TADCola x Object -> void
 *     encolarConPrioridad: TADCola x Object x int -> void
 *     desencolar (dequeue): TADCola -> Object
 *     vaciar: TADCola -> void
 * 
 * SEMÁNTICA FORMAL (Axiomas e Invariantes):
 *   Axioma 1: estaVacia(crearCola()) == true
 *   Axioma 2: frente(encolar(crearCola(), x)) == x
 *   Axioma 3: Para elementos con igual nivel de prioridad, el orden de salida respeta estrictamente FIFO.
 *   Axioma 4: Para elementos x con prioridad p1 e y con prioridad p2, si p1 < p2, x precede a y en el despacho.
 * 
 * INVARIANTES DE REPRESENTACIÓN:
 *   Inv(Cola):
 *     tamano >= 0
 *     && (tamano == 0 <=> (frente == null && fin == null))
 *     && (tamano > 0 <=> (frente != null && fin != null))
 */
public interface TADCola {

    void encolar(Object dato);
    void encolarConPrioridad(Object dato, int prioridad);
    Object desencolar();
    Object frente();
    boolean estaVacia();
    int obtenerTamano();
    void vaciar();
    String listar();
}
