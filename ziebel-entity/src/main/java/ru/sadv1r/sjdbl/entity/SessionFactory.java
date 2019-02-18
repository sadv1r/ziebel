package ru.sadv1r.sjdbl.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.sadv1r.sjdbl.core.SiebelDataBeanWrapper;
import ru.sadv1r.sjdbl.core.ZiebelDataBean;
import ru.sadv1r.sjdbl.core.exception.SjdblException;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author sadv1r
 */
@Slf4j
public class SessionFactory {
    private Map<UUID, Session> openedSessions;
    @Getter
    private final UUID identifier;
    @Nullable
    @Getter
    private final Cache cache;

    private final String link;
    private final String configuredLogin;
    private final String configuredPassword;
    private final String configuredLang;

    private final SessionFactoryOptions sessionFactoryOptions;
    private boolean isClosed;

    public SessionFactory(final String link,
                          final String configuredLogin,
                          final String configuredPassword,
                          final String configuredLang,
                          final SessionFactoryOptions options) {
        log.debug("Building session factory");

        this.sessionFactoryOptions = options;

        this.link = link;
        this.configuredLogin = configuredLogin;
        this.configuredPassword = configuredPassword;
        this.configuredLang = configuredLang;

        identifier = UUID.randomUUID();
        if (sessionFactoryOptions.isCachingEnabled())
            cache = new CacheImpl(this);
        else cache = null;

        openedSessions = new HashMap<>();

        if (sessionFactoryOptions.getSessionFactoryName().isPresent())
            SessionFactoryRegistry.INSTANCE.addSessionFactory(
                    identifier,
                    sessionFactoryOptions.getSessionFactoryName().get(),
                    this
            );
        else
            SessionFactoryRegistry.INSTANCE.addSessionFactory(
                    identifier,
                    this
            );
    }

    public Session openSession() throws SjdblException {
        return openSession(configuredLogin, configuredPassword, configuredLang);
    }

    public Session openSession(final String login) throws SjdblException {
        return openSession(login, configuredPassword, configuredLang);
    }

    public Session openSession(final String login, final String password) throws SjdblException {
        return openSession(login, password, configuredLang);
    }

    public Session openSession(final String login, final String password, final String lang) throws SjdblException {
        final Session session = new Session(this, new ZiebelDataBean(link, login, password, lang));
        openedSessions.put(session.getSessionIdentifier(), session);
        return session;
    }

    void markClosed(final UUID sessionIdentifier) {
        openedSessions.remove(sessionIdentifier);
    }

    public void close() {
        if (isClosed) {
            log.warn("Already closed");

            return;
        }

        log.debug("Closing session factory [{}]", getIdentifier());

        if (cache != null)
            cache.close();

        if (sessionFactoryOptions.getSessionFactoryName().isPresent())
            SessionFactoryRegistry.INSTANCE.removeSessionFactory(
                    identifier,
                    sessionFactoryOptions.getSessionFactoryName().get()
            );
        else
            SessionFactoryRegistry.INSTANCE.removeSessionFactory(identifier);

        isClosed = true;
    }

    public SessionFactoryOptions getSessionFactoryOptions() {
        return sessionFactoryOptions;
    }

    void closeAllSessions() throws SjdblException {
        log.warn("Closing all sessions created by Factory {}!", getIdentifier());
        for (Session session : openedSessions.values()) {
            session.close();
        }
    }
}