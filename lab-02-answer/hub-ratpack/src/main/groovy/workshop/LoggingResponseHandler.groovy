package workshop

import groovy.transform.CompileStatic
import org.slf4j.MDC
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.handling.HandlerDecorator

@CompileStatic
class LoggingResponseHandler implements Handler {

	public static HandlerDecorator decorator() {
		return HandlerDecorator.prepend(new LoggingResponseHandler())
	}

	@Override
	void handle(Context ctx) throws Exception {
		def loggingId = ctx.request.headers.get(LoggingContext.LOGGING_HEADER_NAME) ?: UUID.randomUUID().toString()
		MDC.put(LoggingContext.LOGGING_ID_KEY, loggingId)

		ctx.response.beforeSend({ response ->
			response.headers.set(LoggingContext.LOGGING_HEADER_NAME,loggingId)
		})
		ctx.next(ctx.single(new LoggingContext(loggingId)))
	}
}

