import java.sql.Connection

fun main() {
    try {

        Class.forName("com.mysql.cj.jdbc.Driver")

        val connection = DatabaseConnection.getConnection()
        if (connection != null) {
            println("Conexión exitosa a la base de datos.")

            // función para obtener las tareas no programadas
            val tareasNoProgramadas = obtenerTareasNoProgramadas(connection)

            // Mostrar las tareas no programadas
            if (tareasNoProgramadas.isNotEmpty()) {
                println("\nTareas no programadas:")
                tareasNoProgramadas.forEach { tarea ->
                    println("ID: ${tarea["id"]}, Descripción: ${tarea["descripcion"]}, Duración: ${tarea["duracion"]} min, Utilidad: ${tarea["utilidad"]}")
                }

                // Seleccionar las tareas más rentables
                val tareasSeleccionadas = seleccionarTareasOptimas(tareasNoProgramadas)


                mostrarResultados(tareasSeleccionadas)

                // Actualizar el estado de las tareas seleccionadas
                val idsSeleccionados = tareasSeleccionadas.map { it["id"] as Int }
                actualizarEstadoTareas(connection, idsSeleccionados)
            } else {
                println("\nNo hay tareas no programadas disponibles.")
            }
        } else {
            println("Error al conectar con la base de datos.")
        }
    } catch (e: Exception) {
        println("Error al cargar el driver de MySQL: ${e.message}")
    }
}

// Función para obtener las tareas no programadas
fun obtenerTareasNoProgramadas(connection: Connection): List<Map<String, Any>> {
    val tareas = mutableListOf<Map<String, Any>>() // Lista para almacenar las tareas
    val query = "SELECT id, descripcion, duracion, utilidad FROM tareas WHERE programada = FALSE"

    // Crear la consulta en la base de datos
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(query)

    // Recorrer los resultados y agregar cada tarea a la lista
    while (resultSet.next()) {
        tareas.add(
            mapOf(
                "id" to resultSet.getInt("id"),
                "descripcion" to resultSet.getString("descripcion"),
                "duracion" to resultSet.getInt("duracion"),
                "utilidad" to resultSet.getDouble("utilidad")
            )
        )
    }

    return tareas // Devolver la lista de tareas no programadas
}

// Función para seleccionar las tareas más rentables
fun seleccionarTareasOptimas(tareas: List<Map<String, Any>>): List<Map<String, Any>> {
    val tiempoMaximo = 480 // 8 horas en minutos
    val tareasOrdenadas = tareas.sortedByDescending { it["utilidad"] as Double }
    val seleccionadas = mutableListOf<Map<String, Any>>()
    var tiempoTotal = 0

    for (tarea in tareasOrdenadas) {
        val duracion = tarea["duracion"] as Int
        if (tiempoTotal + duracion <= tiempoMaximo) {
            seleccionadas.add(tarea)
            tiempoTotal += duracion
        }
    }

    return seleccionadas
}

// Función para mostrar los resultados de las tareas seleccionadas
fun mostrarResultados(tareas: List<Map<String, Any>>) {
    println("\nTareas seleccionadas:")
    var tiempoTotal = 0
    var gananciaTotal = 0.0

    for (tarea in tareas) {
        println("- ${tarea["descripcion"]} (${tarea["duracion"]} min, \$${tarea["utilidad"]})")
        tiempoTotal += tarea["duracion"] as Int
        gananciaTotal += tarea["utilidad"] as Double
    }

    println("\nTiempo total: ${tiempoTotal / 60} horas y ${tiempoTotal % 60} minutos")
    println("Ganancia total: \$${gananciaTotal}")
}

// Función para actualizar el estado de las tareas seleccionadas en la base de datos
fun actualizarEstadoTareas(connection: Connection, ids: List<Int>) {
    if (ids.isEmpty()) return
    val query = "UPDATE tareas SET programada = TRUE WHERE id IN (${ids.joinToString(",")})"
    val statement = connection.createStatement()
    statement.executeUpdate(query)
}
