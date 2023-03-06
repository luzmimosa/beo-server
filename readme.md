### Servidor de juego de BEO
El servidor principal está pensado para actuar como intermediario entre los usuarios y las máquinas virtuales que ejecutan las partidas. Sus principales objetivos son:
- [ ] Monitorizar toda la actividad de las partidas y del propio servidor
- [ ] Servir como puente entre las conexiones que piden estadísticas de juego y los servidores de juego.
- [ ] Desplegar los servidores de juego, o delegar el mismo de forma controlada.
- [ ] Obtener los archivos de juego de los usuarios y gestionar la disposición de los servidores de juego a los mismos.

------------


### Diario de desarrollo
#### 05/03/2023
- Esqueleto del proyecto
- Creación del sistema de log
- Prototipados del sistema de consola
- Estructura de datos implementando una cola circular (CircularQueue)
- Estructura global de clave-valor (NamespacedKey)

#### 06/03/2023
- Avance en el sistema de eventos
- Creación de las interfaces del servidor
- Implementados colores en la consola