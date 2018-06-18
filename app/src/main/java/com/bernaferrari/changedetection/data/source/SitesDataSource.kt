package com.bernaferrari.changedetection.data.source

import com.bernaferrari.changedetection.data.Site

/**
 * Main entry point for accessing sites data.
 * Inspired from Architecture Components MVVM sample app
 */
interface SitesDataSource {

    suspend fun getSites(): List<Site>

    suspend fun getSite(siteId: String): Site?

    suspend fun saveSite(site: Site)

    suspend fun updateSite(site: Site)

    suspend fun deleteAllSites()

    suspend fun deleteSite(siteId: String)
}
