package org.devgateway.viz.commons.observers;

import org.devgateway.viz.commons.domain.Dataset;
import org.springframework.cache.CacheManager;

public class CacheManagerDatasetObserver implements DatasetObserver {

    private final CacheManager cacheManager;

    public CacheManagerDatasetObserver(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onDatasetChange(final Dataset dataset, final DatasetAction action) {
        cacheManager.getCache("stats").clear();
    }
}
