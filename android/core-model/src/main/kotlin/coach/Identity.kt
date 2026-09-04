package coach.model

/**
 * DDM-v0: identity is composite. A reference that carries only a logical id would silently
 * slide to the newest version the moment curriculum is updated, so every user-side reference
 * carries the version it was pinned to.
 */
data class VersionedRef(
    val logicalId: String,
    val version: Int,
) {
    init {
        require(logicalId.isNotBlank()) { "logicalId must not be blank" }
        require(version >= 1) { "version must be >= 1" }
    }

    override fun toString(): String = "$logicalId@v$version"
}
