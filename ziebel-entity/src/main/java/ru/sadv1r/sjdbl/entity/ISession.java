package ru.sadv1r.sjdbl.entity;

import ru.sadv1r.sjdbl.core.exception.MoreThanOneRecordFoundExceptions;
import ru.sadv1r.sjdbl.core.exception.RecordNotFoundException;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author sadv1r
 */
public interface ISession extends AutoCloseable {
    /**
     * Get the session factory which created this session.
     *
     * @return The session factory
     */
    SessionFactory getSessionFactory();

    /**
     * A UUID associated with each Session. Useful mainly for logging.
     *
     * @return The UUID
     */
    UUID getSessionIdentifier();

    /**
     * Get the login ID of the user who started the Siebel application.
     *
     * @return The user's login ID
     */
    String getLoginId();

    /**
     * Get the login name of the user who started the Siebel application.
     *
     * @return The user's login name
     */
    String getLoginName();

    /**
     * Get the ID of the user position.
     *
     * @return The user's position ID
     */
    String getPositionId();

    /**
     * Get the name of current user position.
     *
     * @return The user's position name
     */
    String getPositionName();

    /**
     * Check if the session is still open
     *
     * @return boolean
     */
    boolean isOpen();

    /**
     * Disconnect the session from the current SiebelDataBean connection. This is intended for use in cases where the
     * application has supplied the SiebelDataBean connection to the session.
     * <p/>
     * It is considered an error to call this method on a session which was not opened by supplying the SiebelDataBean connection
     * and an exception will be thrown.
     *
     * @throws SjdblException
     */
//    void disconnect() throws SjdblException;

    /**
     * End the session by releasing the Siebel connection and cleaning up.
     *
     * @throws SjdblException
     */

    void close() throws SjdblException;

    /**
     * This method returns an object of the given entity class with the given identifier.
     *
     * @param clazz a persistent class
     * @param id    an identifier
     * @return a persistent instance or {@link Optional#empty()}
     */
    <T> Optional<T> get(Class<T> clazz, String id) throws SjdblException;

    /**
     * This method takes an object with certain fields populated with data
     * and performs a query based on the data in those fields. We only expect to return one record.
     *
     * @param object the object that both determines the query and houses the returned data
     * @return a persistent instance or {@link Optional#empty()}
     * @throws MoreThanOneRecordFoundExceptions if there are more than one record found with the data populated in query Object
     */
    boolean get(Object object) throws SjdblException;

    /**
     * Return the persistent instance of the given entity class with the given identifier,
     * assuming that the instance exists.
     * You should not use this method to determine if an instance exists (use {@link #get(Object)} instead).
     * Use this only to retrieve an instance that you assume exists, where non-existence would be an actual error.
     *
     * @param clazz a persistent class
     * @param id    an identifier
     * @return a persistent instance
     * @throws RecordNotFoundException if object is not exist
     */
    <T> T load(Class<T> clazz, String id) throws SjdblException;

    /**
     * Builds a query from the given object, queries Siebel for matching rows,
     * and returns these rows as java objects of the same type as the given query object.
     * The query is constructed exactly as it is constructed by {@link #get(Object)}, but in this case,
     * the query object is not populated with any of the result rows.
     *
     * @param <T>    the type of the query object, and correspondingly, the type of the returned objects
     * @param object determines the query based on its populated fields
     * @return a {@link List} of objects built from the rows returned from Siebel
     */
    <T> List<T> list(T object) throws SjdblException;

    /**
     * Inserts a row into the corresponding Siebel business component, built from the given object.
     *
     * @param object object the row to be inserted
     * @return the id assigned by Siebel for the newly inserted row
     */
    String save(Object object) throws SjdblException;

    /**
     * TODO Подумать, нужно ли создавать запись если не удалось апдейтнуть??
     * <p>
     * If a row exists with the same key as the given object, then update it with the given object's data,
     * otherwise, insert a new row with the given object's data.
     *
     * @param object object the row to be updated
     * @return the id of the row (which may have just been created)
     */
    String siebelUpsert(Object object);

    /**
     * TODO Тоже подумать над странной логикой
     * <p>
     * Deletes the row corresponding to the given object.
     * A siebel query is executed for rows that match the given object (as in {@link #get(Object)},
     * and then the first matching row is deleted; any others are ignored.
     *
     * @param object object the row to be deleted
     * @return true if a row was found and deleted; false otherwise
     */
    boolean siebelDelete(Object object);
}
