package org.openrefine.wikidata.utils;

import java.util.concurrent.TimeUnit;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EntityCache {
    private static EntityCache _entityCache = new EntityCache();
    
    private LoadingCache<EntityIdValue, EntityDocument> _cache;
    private WikibaseDataFetcher _fetcher;
    
    
    private EntityCache() {
        ApiConnection connection = ApiConnection.getWikidataApiConnection();
        _fetcher = new WikibaseDataFetcher(connection, Datamodel.SITE_WIKIDATA);

        _cache = CacheBuilder.newBuilder()
                .maximumSize(4096)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(
             new CacheLoader<EntityIdValue, EntityDocument>() {
                 public EntityDocument load(EntityIdValue entityId) throws Exception {
                     EntityDocument doc = _fetcher.getEntityDocument(entityId.getId());
                     return doc;
                 }
             });
    }
    
    public static EntityDocument getEntityDocument(EntityIdValue qid) {
        return _entityCache._cache.apply(qid);
    }
}