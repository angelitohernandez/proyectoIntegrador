package mx.edu.utez.proyectointegrador.data.model

enum class Rol(val label: String) {
    ADMIN("Administrador"),
    ENCARGADO("Encargado"),
    VENDEDOR("Vendedor");

    companion object {
        fun from(value: String?): Rol? = entries.firstOrNull { it.name == value }
    }
}
