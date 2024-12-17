import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseConnection {
    companion object {
        private const val URL = "jdbc:mysql://localhost:3306/planificador_tareas"
        private const val USER = "root"
        private const val PASSWORD = "271121"

        fun getConnection(): Connection? {
            return try {
                DriverManager.getConnection(URL, USER, PASSWORD)
            } catch (e: SQLException) {
                println("Error al conectar con la base de datos: ${e.message}")
                null
            }
        }
    }
}
