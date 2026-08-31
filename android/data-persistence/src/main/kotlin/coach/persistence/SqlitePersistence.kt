package coach.persistence

import coach.ports.PersistencePort
import coach.ports.ProjectionRecord
import coach.ports.TruthRecord

/**
 * Implements the core-owned PersistencePort. The physical schema, its triggers and its
 * migrations are 10D; this module exists at 10A to fix the boundary and the dependency.
 *
 * Two accepted requirements shape what goes here later:
 *  - append-only is enforced by the storage layer, not by discipline in calling code, and is
 *    proven by attempted UPDATE and DELETE being rejected (TVSX-v0 §8.1),
 *  - the bundled SQLite driver runs on the JVM, so tier T2 exercises a real storage engine
 *    off-device instead of only on the phone.
 */
class SqlitePersistence : PersistencePort {

    override fun <T> inTransaction(block: () -> T): T =
        TODO("Transaction boundary is wired at 10D; one learner action commits as one transaction.")

    override fun appendTruth(record: TruthRecord): Unit =
        TODO("Append-only truth tables are defined at 10D (DDM-v0 physical schema).")

    override fun readProjection(key: String): ProjectionRecord? =
        TODO("Projection storage is defined at 10D.")

    override fun writeProjection(record: ProjectionRecord): Unit =
        TODO("Projection storage is defined at 10D.")
}
