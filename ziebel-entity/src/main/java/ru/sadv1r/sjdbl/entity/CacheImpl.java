package ru.sadv1r.sjdbl.entity;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Objects;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * @author sadv1r
 */
@Slf4j
public class CacheImpl implements Cache {
    private static final CacheManager CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder().build();
    private static final CacheConfigurationBuilder<String, Object> CACHE_CONFIGURATION_BUILDER = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(String.class, Object.class, ResourcePoolsBuilder.heap(10));

    private final SessionFactory sessionFactory;
    private final org.ehcache.Cache<String, Object> sessionCache;

    static {
        CACHE_MANAGER.init();
    }

    public CacheImpl(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

        sessionCache = CACHE_MANAGER.createCache(
                String.format("sessionCache[%s]", sessionFactory.getIdentifier()),
                CACHE_CONFIGURATION_BUILDER
        );
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public boolean containsEntity(Class entityClass, String id) {
        return sessionCache.containsKey(id);
    }

    public void put(final String key, final Object entity) {
        sessionCache.put(key, entity);
    }

    public <T> T get(Class<T> type, final String key) {
        final Object cachedEntity = sessionCache.get(key);

        return Objects.castIfBelongsToType(cachedEntity, type);
    }

    @Override
    public void close() {
        //TODO!
    }
}
