package io.moviles.IPN_Tycoon.rival

/** Estado global del Instituto Rival (mismo patron que GameState). */
object RivalState {

    const val DINERO_INICIAL = 500_000L

    var dinero: Long = DINERO_INICIAL
    /** id de propiedad -> nivel. */
    val edificios: MutableMap<String, Int> = mutableMapOf()
    var estado: RivalEstado = RivalEstado.EXPANDIR
    var ciclosVictoria: Int = 0
    var ciclosDerrota: Int = 0
    var finPartidaMostrado: Boolean = false

    fun reset() {
        dinero = DINERO_INICIAL
        edificios.clear()
        estado = RivalEstado.EXPANDIR
        ciclosVictoria = 0
        ciclosDerrota = 0
        finPartidaMostrado = false
    }

    fun posee(id: String): Boolean = edificios.containsKey(id)

    /** Formato: "dinero|ESTADO|victoria|derrota|fin|id:nivel,id:nivel". */
    fun serializar(): String {
        val ed = edificios.entries.joinToString(",") { "${it.key}:${it.value}" }
        return "$dinero|${estado.name}|$ciclosVictoria|$ciclosDerrota|${if (finPartidaMostrado) 1 else 0}|$ed"
    }

    /** Nunca lanza: una cadena vacia o corrupta deja el estado en reset(). */
    fun deserializar(s: String?) {
        if (s.isNullOrBlank()) { reset(); return }
        try {
            val p = s.split("|", limit = 6)
            if (p.size < 6) { reset(); return }
            val d = p[0].trim().toLong()
            val e = RivalEstado.valueOf(p[1].trim())
            val v = p[2].trim().toInt()
            val de = p[3].trim().toInt()
            val f = p[4].trim() == "1"
            val mapa = LinkedHashMap<String, Int>()
            if (p[5].isNotBlank()) {
                for (par in p[5].split(",")) {
                    val kv = par.split(":")
                    if (kv.size != 2 || kv[0].isBlank()) { reset(); return }
                    val nivel = kv[1].trim().toInt()
                    if (nivel < 1) { reset(); return }
                    mapa[kv[0].trim()] = nivel
                }
            }
            if (d < 0 || v < 0 || de < 0) { reset(); return }
            dinero = d
            estado = e
            ciclosVictoria = v
            ciclosDerrota = de
            finPartidaMostrado = f
            edificios.clear()
            edificios.putAll(mapa)
        } catch (_: Exception) {
            reset()
        }
    }
}
