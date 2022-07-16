package xyz.atrius.transfer

/**
 * @author Atri
 *
 * Provides database entities a way to be converted into DTO objects. This helps to establish
 * a hard link between a database entity, and its associated transfer object.
 *
 * @param T The DTO type of the base entity.
 */
interface DtoTranslation<T> {

    /**
     * Converts the associated object into a DTO translation of itself. This separates the entity from
     * its base data to avoid mutating objects in unexpected ways, and allows for requests to be made
     * to modify the base entity.
     *
     * @return The newly DTO object.
     */
    fun asDto(): T
}