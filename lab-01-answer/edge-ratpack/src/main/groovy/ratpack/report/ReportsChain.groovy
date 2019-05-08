package ratpack.report

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.netty.handler.codec.http.HttpHeaderValues
import ratpack.func.Action
import ratpack.groovy.Groovy
import ratpack.handling.Chain
import ratpack.http.client.HttpClient
import ratpack.http.client.ReceivedResponse

@Slf4j
@Singleton
class ReportsChain implements Action<Chain> {

	private final HttpClient httpClient

	@Inject
	ReportsChain(HttpClient httpClient) {
		this.httpClient = httpClient
	}

	@Override
	void execute(Chain chain) throws Exception {
		Groovy.chain(chain) {
			path {
				byMethod {
					get {
						//list reports
					}
				}
			}

		}
	}
}
