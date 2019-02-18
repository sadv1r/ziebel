package ru.sadv1r.sjdbl.entity;

/**
 * @author sadv1r
 */
public interface Cache {
    /**
     * Access to the SessionFactory this Cache is bound to.
     *
     * @return The SessionFactory
     */
    SessionFactory getSessionFactory();

    /**
     * Determine whether the cache contains data for the given entity "instance".
     * <p>
     * The semantic here is whether the cache contains data visible for the
     * current call context.
     *
     * @param entityClass The entity class.
     * @param id          The entity identifier
     * @return True if the underlying cache contains corresponding data; false
     * otherwise.
     */
    boolean containsEntity(Class entityClass, String id);

    void close();
}
