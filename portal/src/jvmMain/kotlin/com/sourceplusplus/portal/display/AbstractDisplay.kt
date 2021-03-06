package com.sourceplusplus.portal.display

import com.sourceplusplus.portal.SourcePortal
import com.sourceplusplus.portal.model.PageType
import io.vertx.kotlin.coroutines.CoroutineVerticle

/**
 * Contains common portal tab functionality.
 *
 * @since 0.1.0
 * @author [Brandon Fergerson](mailto:bfergerson@apache.org)
 */
abstract class AbstractDisplay(val thisTab: PageType) : CoroutineVerticle() {

    abstract fun updateUI(portal: SourcePortal)
}
