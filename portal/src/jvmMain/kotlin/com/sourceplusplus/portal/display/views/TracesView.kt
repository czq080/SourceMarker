package com.sourceplusplus.portal.display.views

import com.sourceplusplus.portal.model.TraceDisplayType
import com.sourceplusplus.protocol.artifact.trace.TraceOrderType
import com.sourceplusplus.protocol.artifact.trace.TraceResult
import com.sourceplusplus.protocol.artifact.trace.TraceStack
import com.sourceplusplus.protocol.artifact.trace.TraceStackPath
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

/**
 * Holds the current view for the Traces portal tab.
 *
 * @since 0.1.0
 * @author [Brandon Fergerson](mailto:bfergerson@apache.org)
 */
class TracesView {

    var traceResultCache = ConcurrentHashMap<TraceOrderType, TraceResult>()
    var traceStacks = HashMap<String, TraceStack>() //todo: evicting cache
    var traceStack: TraceStack? = null
    var orderType = TraceOrderType.LATEST_TRACES
    var viewType = TraceDisplayType.TRACES
    var traceId: String? = null
    var traceStackPath: TraceStackPath? = null
    var spanId: Int = 0
    var viewTraceAmount = 10
    var innerTraceStack = false
    var rootArtifactQualifiedName: String? = null

    fun cacheArtifactTraceResult(artifactTraceResult: TraceResult) {
        val currentTraceResult = traceResultCache[artifactTraceResult.orderType]
        if (currentTraceResult != null) {
            val mergedArtifactTraceResult = artifactTraceResult.mergeWith(currentTraceResult)
            traceResultCache[mergedArtifactTraceResult.orderType] = mergedArtifactTraceResult.truncate(viewTraceAmount)
        } else {
            traceResultCache[artifactTraceResult.orderType] = artifactTraceResult.truncate(viewTraceAmount)
        }
    }

    val artifactTraceResult: TraceResult?
        get() = traceResultCache[orderType]

    fun cacheTraceStack(traceId: String, traceStack: TraceStack) {
        traceStacks[traceId] = traceStack
    }

    fun getTraceStack(traceId: String): TraceStack? {
        return traceStacks[traceId]
    }

    fun cloneView(view: TracesView) {
        traceResultCache = ConcurrentHashMap(view.traceResultCache)
        traceStacks = HashMap(view.traceStacks)
        traceStack = if (view.traceStack != null) {
            view.traceStack!!.copy(traceSpans = view.traceStack!!.traceSpans.toList())
        } else {
            null
        }
        traceStackPath = if (view.traceStackPath != null) {
            view.traceStackPath!!.copy(path = view.traceStackPath!!.path.toMutableList())
        } else {
            null
        }
        orderType = view.orderType
        viewType = view.viewType
        traceId = view.traceId
        spanId = view.spanId
    }
}
