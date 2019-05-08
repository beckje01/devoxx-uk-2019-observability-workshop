package workshop

import groovy.util.logging.Slf4j
import org.slf4j.MDC


@Slf4j
class LoggingInterceptor {

	LoggingInterceptor(){
		matchAll()
	}

	boolean before() {
		true
	}

	boolean after() {
		true
	}

	void afterView() {
		// no-op
	}
}
