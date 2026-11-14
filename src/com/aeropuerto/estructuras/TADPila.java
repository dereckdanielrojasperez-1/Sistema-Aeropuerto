package com.aeropuerto.estructuras;

/**
 * =========================================================================
 * ESPECIFICACIÓN FORMAL DEL TIPO ABSTRACTO DE DATOS: TAD PILA (LIFO)
 * =========================================================================
 * 
 * DOMINIO CONCEPTUAL:
 * Colección dinámica lineal donde los elementos se insertan y retiran
 * exclusivamente por un único extremo denominado "tope", siguiendo la
 * disciplina LIFO (Last-In, First-Out). Aplicada para la reversibilidad
 * atómica de transacciones (Deshacer reservas de asientos) en CORPAC S.A.
 * 
 * SINTAXIS (Signatura de Operaciones):
 * - Constructores:
 *     crearPila: () -> TADPila
 * - Observadores:
 *     cima (peek): TADPila -> Object
 *     estaVacia: TADPila -> boolean
 *     obtenerTamano: TADPila -> int
 *     listar: TADPila -> String
 * - Modificadores:
 *     apilar (push): TADPila x Object -> void
 *     desapilar (pop): TADPila -> Object
 *     vaciar: TADPila -> void
 * 
 * SEMÁNTICA FORMAL (Axiomas e Invariantes):
 *   Axioma 1: desapilar(apilar(P, x)) == x
 *   Axioma 2: cima(apilar(P, x)) == x
 *   Axioma 3: estaVacia(crearPila()) == true
 *   Axioma 4: estaVacia(apilar(P, x)) == false
 * 
 * INVARIANTES DE REPRESENTACIÓN:
 *   Inv(Pila):
 *     tamano >= 0
 *     && (tamano == 0 <=> tope == null)
 *     && (tamano > 0 <=> tope != null)
 */
public interface TADPila {

    void apilar(Object dato);
    Object desapilar();
    Object cima();
    boolean estaVacia();
    int obtenerTamano();
    void vaciar();
    String listar();
}
