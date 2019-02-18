package ru.sadv1r.sjdbl.entity;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry of all {@link SessionFactory} instances for the same classloader as this class.
 *
 * @author sadv1r
 */
@Slf4j
public class SessionFactoryRegistry {
    /**
     * Singleton access
     */
    public static final SessionFactoryRegistry INSTANCE = new SessionFactoryRegistry();

    /**
     * A map for mapping the UUID of a SessionFactory to the corresponding SessionFactory instance
     */
    private final ConcurrentHashMap<UUID, SessionFactory> sessionFactoryMap = new ConcurrentHashMap<>();

    /**
     * A cross-reference for mapping a SessionFactory name to its UUID.  Not all SessionFactories get named
     */
    private final ConcurrentHashMap<String, UUID> nameUuidXref = new ConcurrentHashMap<>();

    private SessionFactoryRegistry() {
        log.debug("Initializing SessionFactoryRegistry: {}", this);
    }

    /**
     * Adds a SessionFactory to the registry
     *
     * @param uuid     The uuid under which to register the SessionFactory
     * @param instance The SessionFactory instance
     */
    public void addSessionFactory(
            UUID uuid,
            SessionFactory instance) {

        log.debug("Registering SessionFactory: {}", uuid);
        sessionFactoryMap.put(uuid, instance);
    }

    /**
     * Adds a SessionFactory to the registry
     *
     * @param uuid     The uuid under which to register the SessionFactory
     * @param name     The name under which to register the SessionFactory
     * @param instance The SessionFactory instance
     */
    public void addSessionFactory(
            UUID uuid,
            String name,
            SessionFactory instance) {

        log.debug("Registering SessionFactory: {} ({})", uuid, name);
        sessionFactoryMap.put(uuid, instance);
        nameUuidXref.put(name, uuid);
    }

    /**
     * Remove a previously added SessionFactory
     *
     * @param uuid The uuid
     */
    public void removeSessionFactory(
            UUID uuid) {
        sessionFactoryMap.remove(uuid);
    }

    /**
     * Remove a previously added SessionFactory
     *
     * @param uuid The uuid
     * @param name The optional name
     */
    public void removeSessionFactory(
            UUID uuid,
            String name) {
        nameUuidXref.remove(name);
        sessionFactoryMap.remove(uuid);
    }

    /**
     * Get a registered SessionFactory by name
     *
     * @param name The name
     * @return The SessionFactory
     */
    public Optional<SessionFactory> getNamedSessionFactory(String name) {
        log.debug("Lookup: name=%s", name);
        final UUID uuid = nameUuidXref.get(name);
        if (uuid != null)
            return getSessionFactory(uuid);
        else
            return Optional.empty();
    }

    public Optional<SessionFactory> getSessionFactory(UUID uuid) {
        log.debug("Lookup: uuid={}", uuid);
        final SessionFactory sessionFactory = sessionFactoryMap.get(uuid);
        if (sessionFactory == null) {
            log.debug("Not found: {}", uuid);
            log.debug(sessionFactoryMap.toString());
        }

        return Optional.ofNullable(sessionFactory);
    }

    public Optional<SessionFactory> findSessionFactory(UUID uuid, @Nullable String name) {
        Optional<SessionFactory> sessionFactory = getSessionFactory(uuid);
        if (!sessionFactory.isPresent() && !Strings.isNullOrEmpty(name)) {
            sessionFactory = getNamedSessionFactory(name);
        }
        return sessionFactory;
    }

    /**
     * Does this registry currently contain registrations?
     *
     * @return true/false
     */
    public boolean hasRegistrations() {
        return !sessionFactoryMap.isEmpty();
    }

    public void clearRegistrations() {
        nameUuidXref.clear();
        for (SessionFactory factory : sessionFactoryMap.values()) {
            try {
                factory.close();
            } catch (Exception ignore) {
            }
        }
        sessionFactoryMap.clear();
    }
}
