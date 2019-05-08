package workshop

import groovy.util.logging.Slf4j
import org.slf4j.MDC


@Slf4j
class LoggingInterceptor {

	LoggingInterceptor(){
		matchAll()
	}

	boolean before() {
		def loggingId = request.getHeader(LoggingContext.LOGGING_HEADER_NAME) ?: UUID.randomUUID().toString()
		MDC.put(LoggingContext.LOGGING_ID_KEY, loggingId)
		true
	}

	boolean after() {
		log.debug("Setting outbound header")
		response.setHeader(LoggingContext.LOGGING_HEADER_NAME, MDC.get(LoggingContext.LOGGING_ID_KEY))
		true
	}

	void afterView() {
		// no-op
	}
}
