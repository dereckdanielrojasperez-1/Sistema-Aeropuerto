# Sistema de Gestión Aeroportuaria - CORPAC S.A.
**Curso:** Algoritmos y Estructuras de Datos (AED)  
**Avance:** Proyecto Final - Semana 18  
**Organización:** Corporación Peruana de Aeropuertos y Aviación Comercial (CORPAC S.A.)  
**Tecnología:** Java Standard Edition (JDK 17 / 21 / 25), Swing, NetBeans Ant, MySQL 8.0+

---

## 1. Requisitos del Sistema
- **Java Development Kit (JDK):** JDK 17 LTS, JDK 21 LTS o JDK 25 (probado y certificado con Eclipse Adoptium OpenJDK 25.0.4).
- **Herramienta de Construcción:** Apache Ant 1.10+ (incluido en NetBeans IDE o por línea de comandos).
- **Base de Datos (Opcional):** MySQL 8.0+. Si MySQL no está iniciado, el sistema conmuta automáticamente al **Modo Local en Memoria** sin interrumpir su funcionamiento.

---

## 2. Instrucciones de Compilación y Ejecución

### Opción A: Compilación rápida desde consola (Windows)
Ejecute el script por lotes ubicado en la raíz del proyecto:
```cmd
clean_and_build.bat
```
El script compilará todas las clases con Ant y generará el archivo ejecutable en `dist/SistemaDeAeropuerto.jar`.

### Opción B: Ejecución directa del archivo JAR
```bash
java -jar dist/SistemaDeAeropuerto.jar
```

### Opción C: Ejecución de la Suite Oficial de 131 Pruebas Automatizadas de TADs
Para ejecutar las 131 pruebas automatizadas de verificación de estructuras de datos y algoritmos (100% PASS):
```bash
java -cp "build/classes;dist/lib/*" com.aeropuerto.test.TestAuditoriaIntegralTADs
```

### Opción D: Apertura en NetBeans IDE / Eclipse / IntelliJ IDEA
1. Abra NetBeans IDE.
2. Seleccione **File -> Open Project** y elija la carpeta del proyecto.
3. Haga clic derecho sobre el proyecto y seleccione **Clean and Build**.
4. Presione **F6** o **Run Project**.

---

## 3. Credenciales de Acceso
- **Modo Pasajero:** Acceso libre directo para reserva de vuelos, selección de cabina y emisión de pases de abordar con QR SUNAT.
- **Modo Administrador (AOC):**
  - **Usuario:** `admin`
  - **Contraseña:** `admin123`
  - Permite acceso al Radar en vivo, gestión de Pistas/Supervisores, TADs dinámicos integrados en pestañas nativas del centro de control.

---

## 4. Estructuras de Datos y Algoritmos Implementados (12 Estructuras Oficiales)

### Unidad 1 y 2: Estructuras Lineales y Arreglos
1. **Arreglo Unidimensional Dinámico:** Catálogo de vuelos con auto-redimensionamiento y búsqueda binaria.
2. **Matriz Bidimensional (10x5):** Plano físico de cabina del *Airbus A320neo*.
3. **Lista Enlazada Simple:** Gestión y despacho de equipajes comerciales facturados por DNI.
4. **Lista Doblemente Enlazada:** Catálogo bidireccional de rutas aéreas.
5. **Lista Enlazada Circular:** Carrusel continuo de puertas de embarque (*Gates*) sin fin de lista.
6. **Pila (LIFO):** Reversibilidad y desapilado de acciones de reserva (*Deshacer / Undo*) con capacidad acotada para control de memoria heap.
7. **Cola con Prioridad (FIFO):** Despacho de aeronaves en pista por la Torre de Control (Niveles: 1-Emergencia, 2-Prioritario, 3-Regular).

### Unidad 3: Hashing y Prioridades Avanzadas
8. **Tabla Hash (Separate Chaining):** Indexación de pasajeros por DNI en O(1) con función bit-shift, factor de carga alpha <= 0.75 y rehashing dinámico a módulos primos.
9. **Montículo Binario (Min-Heap):** Cola de prioridad logarítmica O(log n) con operaciones `flotar()` y `hundir()` para la asignación óptima de slots de pista.

### Unidad 4: Estructuras No Lineales (Árboles y Grafos)
10. **Árbol Binario de Búsqueda (ABB):** Indexación jerárquica con recorridos iterativos Preorden, Inorden, Postorden y Por Niveles (BFS).
11. **Árbol AVL Auto-balanceado:** Estructura balanceada en altura con implementación explícita de las **4 rotaciones canónicas** (RSD/LL, RSI/RR, RDID/LR, RDDI/RL) manteniendo |FE| <= 1.
12. **Grafo Ponderado de Rutas:** Red nacional CORPAC representada en matriz y lista de adyacencia:
    - **Algoritmo de Dijkstra:** Camino de costo mínimo O((V+E) log V).
    - **Algoritmo de Prim:** Árbol de Expansión Mínima (MST) conectando todos los aeropuertos al menor costo.
    - **Algoritmo de Kruskal:** Árbol de Expansión Mínima (MST) con estructura de conjuntos disjuntos (Union-Find / DSU).
    - **Algoritmo de Floyd-Warshall:** Matriz de todos los pares de distancias mínimas en O(V^3) para consultas directas en O(1).

### Algoritmos de Ordenamiento Avanzado y Benchmarking
- **Burbuja e Inserción (O(n^2)):** Algoritmos cuadráticos de base.
- **Quicksort (O(n log n) In-Place):** Partición según pivote Hoare.
- **Mergesort (O(n log n) Estable):** Divide y vencerás con memoria auxiliar.
- **Heapsort (O(n log n) sobre Montículo):** Reorganización in-place por montículo.
- **Módulo de Benchmarking Comparativo:** Medición en nanosegundos y microsegundos con tabla de evaluación de rendimiento en vivo.
