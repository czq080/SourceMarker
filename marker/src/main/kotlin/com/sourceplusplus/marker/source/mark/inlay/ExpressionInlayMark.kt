package com.sourceplusplus.marker.source.mark.inlay

import com.sourceplusplus.marker.source.SourceFileMarker
import com.sourceplusplus.marker.source.mark.api.ExpressionSourceMark
import com.sourceplusplus.marker.source.mark.inlay.config.InlayMarkConfiguration
import org.jetbrains.uast.UExpression
import com.sourceplusplus.marker.SourceMarker.configuration as pluginConfiguration

/**
 * Represents an [InlayMark] associated to an expression artifact.
 *
 * @since 0.1.0
 * @author [Brandon Fergerson](mailto:bfergerson@apache.org)
 */
open class ExpressionInlayMark @JvmOverloads constructor(
    override val sourceFileMarker: SourceFileMarker,
    override var psiExpression: UExpression,
    override val configuration: InlayMarkConfiguration = pluginConfiguration.inlayMarkConfiguration.copy()
) : ExpressionSourceMark(sourceFileMarker, psiExpression), InlayMark
