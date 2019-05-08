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
		ctx.next()
	}
}

