import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.net.URI

suspend fun main() {
    withContext(Dispatchers.IO) {
        Desktop.getDesktop().browse(URI("http://0.0.0.0:8080/headquarters/login"))
    }
}