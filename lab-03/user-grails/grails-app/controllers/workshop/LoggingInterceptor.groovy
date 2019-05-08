package workshop

import brave.ScopedSpan
import brave.http.HttpTracing
import groovy.util.logging.Slf4j
import org.slf4j.MDC

import javax.inject.Inject


@Slf4j
class LoggingInterceptor {

	@Inject
	HttpTracing httpTracing

	LoggingInterceptor(){
		matchAll()
	}

	boolean before() {
		ScopedSpan scopedSpan = httpTracing.tracing().tracer().startScopedSpan(request.requestURI)
		session.setAttribute("zipkin",scopedSpan)

		def loggingId = request.getHeader(LoggingContext.LOGGING_HEADER_NAME) ?: UUID.randomUUID().toString()
		MDC.put(LoggingContext.LOGGING_ID_KEY, loggingId)
		true
	}

	boolean after() {

		ScopedSpan scopedSpan = session.getAttribute("zipkin")
		scopedSpan.finish()
		log.debug("Setting outbound header")
		response.setHeader(LoggingContext.LOGGING_HEADER_NAME, MDC.get(LoggingContext.LOGGING_ID_KEY))
		true
	}

	void afterView() {
		// no-op
	}
}
