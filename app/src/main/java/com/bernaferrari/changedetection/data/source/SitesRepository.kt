package com.bernaferrari.changedetection.data.source

import com.bernaferrari.changedetection.data.Site
import com.bernaferrari.changedetection.data.source.local.SiteAndLastSnap

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
class SitesRepository // Prevent direct instantiation.
private constructor(
    sitesLocalDataSource: SitesDataSource
) : SitesDataSource {

    override fun getLastFewContentTypes(siteId: String, callback: (List<String>) -> Unit) {
        mSitesLocalDataSource.getLastFewContentTypes(siteId) {
            callback.invoke(it)
        }
    }

    private val mSitesLocalDataSource: SitesDataSource = checkNotNull(sitesLocalDataSource)

    override fun getSiteAndLastSnap(callback: (MutableList<SiteAndLastSnap>) -> Unit) {
        mSitesLocalDataSource.getSiteAndLastSnap {
            callback.invoke(it)
        }
    }

    override fun getSites(callback: SitesDataSource.LoadSitesCallback) {
        checkNotNull(callback)

        // Query the local storage if available. If not, query the network.
        mSitesLocalDataSource.getSites(object : SitesDataSource.LoadSitesCallback {
            override fun onSitesLoaded(sites: List<Site>) {
                callback.onSitesLoaded(sites)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun saveSite(site: Site) {
        checkNotNull(site)
        mSitesLocalDataSource.saveSite(site)
    }

    override fun updateSite(site: Site) {
        checkNotNull(site)
        mSitesLocalDataSource.updateSite(site)
    }


    /**
     * Gets sites from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     *
     *
     * Note: [GetTaskCallback.onDataNotAvailable] is fired if both data sources fail to
     * get the data.
     */
    override fun getSite(siteId: String, callback: SitesDataSource.GetSiteCallback) {
        // Load from server/persisted if needed.

        // Is the site in the local data source? If not, query the network.
        mSitesLocalDataSource.getSite(siteId, object : SitesDataSource.GetSiteCallback {
            override fun onSiteLoaded(site: Site) {
                callback.onSiteLoaded(site)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun deleteAllSites() {
        mSitesLocalDataSource.deleteAllSites()
    }

    override fun deleteSite(siteId: String) {
        mSitesLocalDataSource.deleteSite(checkNotNull(siteId))
    }

    companion object {

        @Volatile
        private var INSTANCE: SitesRepository? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         *
         * @param sitesLocalDataSource  the device storage data source
         * @return the [SitesRepository] instance
         */
        fun getInstance(
            sitesLocalDataSource: SitesDataSource
        ): SitesRepository {
            if (INSTANCE == null) {
                synchronized(SitesRepository::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = SitesRepository(sitesLocalDataSource)
                    }
                }
            }
            return INSTANCE!!
        }

        /**
         * Used to force [.getInstance] to create a new instance
         * next time it's called.
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
