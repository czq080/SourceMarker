package com.sourceplusplus.mentor.impl.task.monitor

import com.sourceplusplus.mentor.base.ContextKey
import com.sourceplusplus.mentor.base.MentorJob
import com.sourceplusplus.mentor.base.MentorTask
import com.sourceplusplus.monitor.skywalking.SkywalkingClient
import com.sourceplusplus.monitor.skywalking.bridge.EndpointTracesBridge
import com.sourceplusplus.monitor.skywalking.model.GetEndpointTraces
import com.sourceplusplus.monitor.skywalking.model.ZonedDuration
import com.sourceplusplus.protocol.artifact.QueryTimeFrame
import com.sourceplusplus.protocol.artifact.trace.TraceOrderType
import com.sourceplusplus.protocol.artifact.trace.TraceResult
import monitor.skywalking.protocol.metadata.GetAllServicesQuery
import monitor.skywalking.protocol.metadata.GetServiceInstancesQuery
import java.time.ZonedDateTime

/**
 * todo: description.
 *
 * @since 0.1.0
 * @author [Brandon Fergerson](mailto:bfergerson@apache.org)
 */
class GetTraces(
    private val byServiceId: ContextKey<GetAllServicesQuery.Result>,
    private val byServiceInstanceId: ContextKey<GetServiceInstancesQuery.Result>? = null,
    private val byEndpointIds: ContextKey<List<String>>? = null,
    private val orderType: TraceOrderType,
    private val timeFrame: QueryTimeFrame, //todo: impl start/end in QueryTimeFrame
    private val endpointName: String? = null,
    private val limit: Int = 10
) : MentorTask() {

    companion object {
        val TRACE_RESULT: ContextKey<TraceResult> = ContextKey("GetTraces.TRACE_RESULT")
    }

    override val outputContextKeys = listOf(TRACE_RESULT)

    override suspend fun executeTask(job: MentorJob) {
        job.trace(
            "Task configuration\n\t" +
                    "orderType: $orderType\n\t" +
                    "timeFrame: $timeFrame\n\t" +
                    "endpointName: $endpointName\n\t" +
                    "limit: $limit"
        )

        val serviceInstanceId = if (byServiceInstanceId != null) job.context.get(byServiceInstanceId).id else null
        val traceResult: TraceResult
        if (byEndpointIds != null) {
            var finalTraceResult: TraceResult? = null
            job.context.get(byEndpointIds).forEach { endpointId ->
                val traces = EndpointTracesBridge.getTraces(
                    GetEndpointTraces(
                        serviceId = job.context.get(byServiceId).id,
                        serviceInstanceId = serviceInstanceId,
                        endpointId = endpointId,
                        appUuid = "null", //todo: likely not necessary
                        artifactQualifiedName = "null", //todo: likely not necessary
                        orderType = orderType,
                        zonedDuration = ZonedDuration( //todo: use timeFrame
                            ZonedDateTime.now().minusMinutes(15),
                            ZonedDateTime.now(),
                            SkywalkingClient.DurationStep.MINUTE
                        ),
                        pageSize = limit
                    ), job.vertx
                )
                finalTraceResult = if (finalTraceResult == null) {
                    traces
                } else {
                    finalTraceResult!!.mergeWith(traces)
                }
            }
            traceResult = finalTraceResult!!
        } else {
            traceResult = EndpointTracesBridge.getTraces(
                GetEndpointTraces(
                    endpointName = endpointName,
                    appUuid = "null", //todo: likely not necessary
                    artifactQualifiedName = "null", //todo: likely not necessary
                    orderType = orderType,
                    zonedDuration = ZonedDuration( //todo: use timeFrame
                        ZonedDateTime.now().minusMinutes(15),
                        ZonedDateTime.now(),
                        SkywalkingClient.DurationStep.MINUTE
                    ),
                    pageSize = limit
                ), job.vertx
            )
        }

        if (traceResult.traces.isNotEmpty()) {
            job.trace("Found ${traceResult.traces.size} matching traces")
        }
        job.context.put(TRACE_RESULT, traceResult)
        job.trace("Added context\n\tKey: $TRACE_RESULT\n\tSize: ${traceResult.traces.size}")
    }
}
