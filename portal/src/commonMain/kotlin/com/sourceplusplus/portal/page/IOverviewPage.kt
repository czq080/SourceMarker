package com.sourceplusplus.portal.page

import com.sourceplusplus.portal.PortalPage
import com.sourceplusplus.protocol.artifact.endpoint.EndpointResult
import com.sourceplusplus.protocol.artifact.QueryTimeFrame

/**
 * todo: description.
 *
 * @since 0.1.0
 * @author [Brandon Fergerson](mailto:bfergerson@apache.org)
 */
interface IOverviewPage : PortalPage {

    fun displayEndpoints(endpointResult: EndpointResult)
    fun updateTime(interval: QueryTimeFrame)
}
