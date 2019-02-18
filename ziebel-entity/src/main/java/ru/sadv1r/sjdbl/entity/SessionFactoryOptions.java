package ru.sadv1r.sjdbl.entity;

import lombok.Builder;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Aggregator of special options used to build the SessionFactory.
 *
 * @author sadv1r
 */
@Value
@Builder
public class SessionFactoryOptions {
    @Nullable private final String sessionFactoryName;
    private final boolean cachingEnabled;

    public Optional<String> getSessionFactoryName() {
        return Optional.ofNullable(sessionFactoryName);
    }
}