# mi-primer-repositorio
# Mi primer repositorio en GitHub para la clase de programación.
# Soy Donato de programacion/ 5°1°
# Me interesa aprender sobre todo



# Escáner de Red - Proyecto Final

# Este proyecto consiste en una aplicación de escritorio en Java que permite escanear un rango de direcciones IP dentro de una red local y verificar cuáles equipos # están activos.

# El programa permite:

# Ingresar un rango de direcciones IP (inicio y fin)

# Escanear todas las direcciones dentro de ese rango

# Detectar si un host está activo o inactivo

# Mostrar información como:

# Dirección IP

# Nombre del host

# Estado (activo/inactivo)

# Tiempo de respuesta en milisegundos

# Guardar los resultados en un archivo de texto

# Limpiar la tabla para realizar un nuevo escaneo

# Interfaz gráfica

# La aplicación está hecha con Swing, por lo que es sencilla pero bastante intuitiva.
# Lo principal que se puede ver es:

# Campos de entrada para poner la IP inicial y la IP final del rango que se quiere analizar

# Tabla de resultados donde se van mostrando las IPs escaneadas junto con la información de cada host

# Botones principales:

# Escanear: arranca el proceso

# Limpiar: borra los resultados para hacer otra prueba

# Guardar: exporta los resultados en un archivo de texto (resultados.txt).

# nBarra de progreso que muestra en tiempo real cómo va avanzando el escaneo

# Flujo del programa

# El funcionamiento es bastante simple:

# El usuario escribe un rango de IPs válido

# El programa va recorriendo cada dirección IP de ese rango

# Para cada IP:

# Envía un ping (con un máximo de 500ms de espera).

# Si responde, lo marca como activo y muestra su nombre de host

# Si no responde, lo marca como inactivo

# Todos los resultados se van cargando en la tabla

# Cuando termina, el programa avisa con un mensaje emergente
