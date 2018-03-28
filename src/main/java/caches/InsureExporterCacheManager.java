package caches;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public enum InsureExporterCacheManager {

    /** Singleton-Instanz */
    INSTANCE;

    public static Logger getLogger() {
        return logger;
    }

    private static final Logger logger = LoggerFactory.getLogger(InsureExporterCacheManager.class);
    private static CacheManager manager;
    private static Cache objectCache;

    private InsureExporterCacheManager() {
        init();
    }

    static void init() {
        manager = CacheManager.getInstance();
        // manager.addCache("insure-parser-cache");

        objectCache = manager.getCache("insure-exporter-cache");

        if (objectCache == null) {
            objectCache = new Cache(
                new CacheConfiguration("insure-exporter-cache", 10000)
                    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                    .eternal(false)
                    .timeToLiveSeconds(600)
                    .timeToIdleSeconds(300)
                    .transactionalMode("off")
                    .memoryStoreEvictionPolicy("LFU")
                    .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP)));
            objectCache.disableDynamicFeatures();
            manager.addCache(objectCache);
        }

    }

    @SuppressWarnings("unchecked")
    public List<String> getKeys() {
        return objectCache.getKeys();
    }

    public void clearCache() {
        manager.removeAllCaches();
    }

    public void putInCache(String key, Object value) {
        try {
            if (this.retrieveFromCache(key) != null) {
                objectCache.remove(key);
                objectCache.put(new Element(key, value));
            } else {
                objectCache.put(new Element(key, value));
            }
        } catch (CacheException e) {
            logger.error(String.format("Problem occurred while putting data into cache: %s", e.getMessage()));
        }
    }

    public Object retrieveFromCache(String key) {
        try {
            Element element = objectCache.get(key);
            if (element != null)
                return element.getObjectValue();
        } catch (CacheException ce) {
            logger.error(String.format("Problem occurred while trying to retrieveSpecific from cache: %s", ce.getMessage()));
        }
        return null;
    }
}
