package workshop

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.slf4j.MDC
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.handling.HandlerDecorator

@Slf4j
@CompileStatic
class LoggingResponseHandler implements Handler {


	public static HandlerDecorator decorator() {
		return HandlerDecorator.prepend(new LoggingResponseHandler())
	}

	@Override
	void handle(Context ctx) throws Exception {
		def loggingId = LoggingContext.LOGGING_HEADER_NAME
		log.debug("Logging id found: " + loggingId)
		loggingId = loggingId ?: UUID.randomUUID().toString()
		MDC.put(LoggingContext.LOGGING_ID_KEY, loggingId)

		ctx.response.beforeSend({ response ->
			response.headers.set(LoggingContext.LOGGING_HEADER_NAME, loggingId)
		})

		//Adding a known MDC value to dynamically adjust logging based on headers
		def dynamicLevel = ctx.request.headers.get("X-DEBUGLEVEL")
		if(dynamicLevel){
			MDC.put("dynamicLevel",dynamicLevel.toLowerCase())
		}

		ctx.next(ctx.single(new LoggingContext(loggingId)))
	}
}

