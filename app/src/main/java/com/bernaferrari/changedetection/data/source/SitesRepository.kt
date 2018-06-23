package com.bernaferrari.changedetection.data.source

import com.bernaferrari.changedetection.data.Site
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation to load sites from the data sources into a cache.
 *
 *
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 *
 * TODO: Implement this class using LiveData.
 *
 * Inspired from Architecture Components MVVM sample app
 */
@Singleton
class SitesRepository @Inject constructor(
    sitesLocalDataSource: SitesDataSource
) : SitesDataSource {

    override suspend fun getSites(): List<Site> {
        return mSitesLocalDataSource.getSites()
    }

    override suspend fun getSite(siteId: String): Site? {
        return mSitesLocalDataSource.getSite(siteId)
    }

    private val mSitesLocalDataSource: SitesDataSource = checkNotNull(sitesLocalDataSource)


    override suspend fun saveSite(site: Site) {
        checkNotNull(site)
        mSitesLocalDataSource.saveSite(site)
    }

    override suspend fun updateSite(site: Site) {
        checkNotNull(site)
        mSitesLocalDataSource.updateSite(site)
    }


    override suspend fun deleteAllSites() {
        mSitesLocalDataSource.deleteAllSites()
    }

    override suspend fun deleteSite(siteId: String) {
        mSitesLocalDataSource.deleteSite(checkNotNull(siteId))
    }
}
